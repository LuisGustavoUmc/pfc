package br.com.findpark.service;

import br.com.findpark.dtos.usuarios.AtualizarUsuarioDto;
import br.com.findpark.dtos.usuarios.RegistrarUsuarioDto;
import br.com.findpark.email.EmailService;
import br.com.findpark.email.EmailTemplate;
import br.com.findpark.entities.Usuario;
import br.com.findpark.entities.enums.usuarios.Validade;
import br.com.findpark.exceptions.usuario.RecursoJaExisteException;
import br.com.findpark.repositories.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

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

    public Page<Usuario> buscarTodos(Pageable pageable) {
        return usuarioRepository.findAll(pageable);
    }

    public void salvar(Usuario usuario) {
        usuarioRepository.save(usuario);
    }

    public Optional<Usuario> buscarPorId(String id) {
        return usuarioRepository.findById(id);
    }

    public Optional<Usuario> buscarPorEmail(String email) {
        return usuarioRepository.findByEmail(email);
    }

    public boolean emailJaRegistrado(String email) {
        return usuarioRepository.findByEmail(email).isPresent();
    }

    public void enviarEmailConfirmacao(Usuario usuario) {
        Context context = new Context();
        context.setVariable("usuario", usuario);
        context.setVariable("token", usuario.getTokenConfirmacao());
        emailService.enviarEmail("Confirmação de Cadastro - " + usuario.getNome(), usuario.getEmail(), EmailTemplate.VALIDAR_USUARIO, context, Optional.empty());
    }

    public void updatePassword(Usuario usuario, String password) {
        String hashedPassword = this.passwordEncoder.encode(password);

        usuario.setSenha(hashedPassword);

        salvar(usuario);
    }

    public void update(Usuario usuario, AtualizarUsuarioDto atualizarUsuarioDto) {
        if (atualizarUsuarioDto.nome() != null) usuario.setNome(atualizarUsuarioDto.nome());
        if (atualizarUsuarioDto.email() != null) usuario.setEmail(atualizarUsuarioDto.email());
        if (atualizarUsuarioDto.telefone() != null) usuario.setTelefone(atualizarUsuarioDto.telefone());

        usuarioRepository.save(usuario);
    }

    public void delete(Usuario usuario) {
        usuarioRepository.delete(usuario);
    }

    public Optional<Usuario> buscarPorTokenConfirmacao(String token) {
        return usuarioRepository.findByTokenConfirmacao(token);
    }

    @Scheduled(fixedRate = 600_000) // a cada 10 minutos
    public void excluirUsuariosNaoValidados() {
        LocalDateTime limite = LocalDateTime.now().minusHours(2);
        List<Usuario> expirados = usuarioRepository.findByValidadeAndCriadoEmBefore(Validade.PENDENTE, limite);
        usuarioRepository.deleteAll(expirados);
    }
}
