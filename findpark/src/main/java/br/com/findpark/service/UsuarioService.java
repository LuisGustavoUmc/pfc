package br.com.findpark.service;

import br.com.findpark.dtos.usuarios.AtualizarUsuarioDto;
import br.com.findpark.dtos.usuarios.RegistrarUsuarioDto;
import br.com.findpark.email.EmailService;
import br.com.findpark.email.EmailTemplate;
import br.com.findpark.entities.Cliente;
import br.com.findpark.entities.Estacionamento;
import br.com.findpark.entities.Proprietario;
import br.com.findpark.entities.Usuario;
import br.com.findpark.entities.enums.usuarios.UserRole;
import br.com.findpark.entities.enums.usuarios.Validade;
import br.com.findpark.exceptions.usuario.RecursoJaExisteException;
import br.com.findpark.repositories.EstacionamentoRepository;
import br.com.findpark.repositories.UsuarioRepository;
import br.com.findpark.repositories.VagaRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.context.Context;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
public class UsuarioService {

    @Value("${EMAIL_USERNAME}")
    private String sender;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private EmailService emailService;

    @Autowired
    private ReservaService reservaService;

    @Autowired
    private EstacionamentoRepository estacionamentoRepository;

    @Autowired
    private VagaRepository vagaRepository;


    // Cria um novo usuário a partir dos dados fornecidos, garantindo unicidade do e-mail,
    // gera token de confirmação e envia e-mail para validação.
    public Usuario criarUsuario(RegistrarUsuarioDto registrarUsuarioDto) {

        if (emailJaRegistrado(registrarUsuarioDto.email())) throw new RecursoJaExisteException("Usuário já cadastrado com esse e-mail!");

        Usuario novoUsuario = new Usuario();
        novoUsuario.setNome(registrarUsuarioDto.nome());
        novoUsuario.setEmail(registrarUsuarioDto.email());
        novoUsuario.setSenha(passwordEncoder.encode(registrarUsuarioDto.senha()));
        novoUsuario.setTelefone(registrarUsuarioDto.telefone());
        novoUsuario.setRole(registrarUsuarioDto.role());
        novoUsuario.setValidade(Validade.PENDENTE);
        novoUsuario.setCriadoEm(LocalDateTime.now());
        novoUsuario.setTokenConfirmacao(UUID.randomUUID().toString());

        usuarioRepository.save(novoUsuario);
        enviarEmailConfirmacao(novoUsuario);
        return novoUsuario;
    }

    // Busca todos os usuários com paginação.
    public Page<Usuario> buscarTodos(Pageable pageable) {
        return usuarioRepository.findAll(pageable);
    }

    // Salva ou atualiza o usuário no repositório.
    public void salvar(Usuario usuario) {
        usuarioRepository.save(usuario);
    }

    // Busca um usuário pelo ID, retorna Optional.
    public Optional<Usuario> buscarPorId(String id) {
        return usuarioRepository.findById(id);
    }

    // Busca um usuário pelo e-mail, retorna Optional.
    public Optional<Usuario> buscarPorEmail(String email) {
        return usuarioRepository.findByEmail(email);
    }

    // Verifica se o e-mail já está registrado.
    public boolean emailJaRegistrado(String email) {
        return usuarioRepository.findByEmail(email).isPresent();
    }

    // Envia e-mail de confirmação para o usuário com token único.
    public void enviarEmailConfirmacao(Usuario usuario) {
        Context context = new Context();
        context.setVariable("usuario", usuario);
        context.setVariable("token", usuario.getTokenConfirmacao());
        emailService.enviarEmail("Confirmação de Cadastro - " + usuario.getNome(), usuario.getEmail(), EmailTemplate.VALIDAR_USUARIO, context, Optional.empty());
    }

    // Atualiza a senha do usuário, armazenando-a criptografada.
    public void updatePassword(Usuario usuario, String password) {
        String hashedPassword = this.passwordEncoder.encode(password);

        usuario.setSenha(hashedPassword);

        salvar(usuario);
    }

    // Atualiza os dados pessoais do usuário com base no DTO.
    public void update(Usuario usuario, AtualizarUsuarioDto atualizarUsuarioDto) {
        if (atualizarUsuarioDto.nome() != null) usuario.setNome(atualizarUsuarioDto.nome());
        if (atualizarUsuarioDto.email() != null) usuario.setEmail(atualizarUsuarioDto.email());
        if (atualizarUsuarioDto.telefone() != null) usuario.setTelefone(atualizarUsuarioDto.telefone());

        usuarioRepository.save(usuario);
    }

    // Exclui o usuário do sistema.
    @Transactional
    public void delete(Usuario usuario) {
        if (usuario.getRole() == UserRole.CLIENTE) {
            reservaService.cancelarReservasDoCliente(usuario.getId());
        }

        if (usuario.getRole() == UserRole.PROPRIETARIO){
            List<Estacionamento> estacionamentos = estacionamentoRepository.findByIdProprietario(usuario.getId());

            for (Estacionamento est : estacionamentos) {
                vagaRepository.deleteByEstacionamentoId(est.getId()); // Remove vagas associadas
            }

            estacionamentoRepository.deleteAll(estacionamentos); // Remove estacionamentos
            log.info("Proprietário {} teve seus estacionamentos e vagas deletados.", usuario.getId());
        }

        usuarioRepository.delete(usuario);
    }



    // Busca um usuário pelo token de confirmação.
    public Optional<Usuario> buscarPorTokenConfirmacao(String token) {
        return usuarioRepository.findByTokenConfirmacao(token);
    }

    // Remove usuários não validados (pendentes) há mais de 2 horas, executado periodicamente.
    @Scheduled(fixedRate = 600_000)
    public void excluirUsuariosNaoValidados() {
        LocalDateTime limite = LocalDateTime.now().minusHours(2);
        List<Usuario> expirados = usuarioRepository.findByValidadeAndCriadoEmBefore(Validade.PENDENTE, limite);
        usuarioRepository.deleteAll(expirados);
    }
}
