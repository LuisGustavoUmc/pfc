package br.com.findpark.auth;

import br.com.findpark.auth.dtos.*;
import br.com.findpark.auth.jwt.TokenService;
import br.com.findpark.email.EmailService;
import br.com.findpark.email.EmailTemplate;
import br.com.findpark.entities.Usuario;
import br.com.findpark.entities.enums.usuarios.Validade;
import br.com.findpark.exceptions.usuario.RecursoNaoEncontradoException;
import br.com.findpark.exceptions.usuario.UsuarioSenhaInvalidaException;
import br.com.findpark.exceptions.usuario.UsuarioValidacaoException;
import br.com.findpark.service.UsuarioService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;

import java.util.Map;
import java.util.Optional;

@Service
public class AuthService {

    @Autowired
    private PasswordEncoder passwordEncoder; // Serviço para codificar e comparar senhas

    @Autowired
    private TokenService tokenService; // Serviço para gerar e validar tokens JWT

    @Autowired
    private UsuarioService usuarioService; // Serviço para operações com usuários

    @Autowired
    private EmailService emailService; // Serviço para envio de emails

    /**
     * Método para obter o usuário logado a partir do token JWT.
     * @param accessToken token JWT recebido (apenas o token, sem "Bearer ")
     * @return ResponseEntity com o usuário encontrado.
     */
    public ResponseEntity<Usuario> usuarioLogged(String accessToken) {
        // Valida token e extrai email do usuário
        String subjectEmail = tokenService.validateToken(accessToken);

        // Busca usuário pelo email extraído do token
        Usuario usuario = usuarioService.buscarPorEmail(subjectEmail)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Usuario não encontrado!"));

        return ResponseEntity.ok(usuario);
    }

    /**
     * Realiza login do usuário.
     * @param loginUsuarioDto DTO com email e senha
     * @param response HttpServletResponse (não usado aqui diretamente)
     * @return ResponseEntity com dados do usuário e token JWT
     */
    public ResponseEntity<AuthResponseDto> login(LoginUsuarioDto loginUsuarioDto, HttpServletResponse response) {
        // Busca usuário pelo email
        Usuario usuario = usuarioService.buscarPorEmail(loginUsuarioDto.email())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Usuario não encontrado!"));

        // Verifica se senha está correta
        if (!passwordEncoder.matches(loginUsuarioDto.senha(), usuario.getSenha()))
            throw new UsuarioSenhaInvalidaException();

        // Verifica se o usuário está pendente de validação
        if (usuario.getValidade() == Validade.PENDENTE)
            throw new UsuarioValidacaoException("Forbidden validation", usuario.getValidade(), HttpStatus.FORBIDDEN);

        // Verifica se o usuário está recusado na validação
        if (usuario.getValidade() == Validade.RECUSADO)
            throw new UsuarioValidacaoException("Unauthorized validation", usuario.getValidade(), HttpStatus.UNAUTHORIZED);

        // Gera token JWT para o usuário logado
        String accessToken = tokenService.generateAccessToken(usuario);

        // Retorna dados do usuário junto com token e role
        return ResponseEntity.ok(new AuthResponseDto(
                usuario.getNome(),
                usuario.getEmail(),
                accessToken,
                usuario.getRole().name()
        ));
    }

    /**
     * Logout do usuário (aqui só retorna mensagem de sucesso, não faz logout real no backend).
     */
    public ResponseEntity<Map<String, String>> logout(HttpServletResponse response, String accessToken) {
        // Simplesmente retorna mensagem de logout com sucesso
        return ResponseEntity.ok(Map.of("successfully", "true", "message", "Logout Successfully"));
    }

    /**
     * Envia email para recuperação de senha.
     * @param dto DTO contendo o email do usuário que quer recuperar senha.
     */
    public void sendEmailRecoverPassword(RecoverDto dto) {
        // Busca usuário pelo email informado
        Usuario usuario = usuarioService.buscarPorEmail(dto.email())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Usuario não encontrado!"));

        // Gera token de recuperação com validade de 30 minutos
        String token = tokenService.generateAccessToken(usuario, 0, 30);

        // Prepara o contexto para o template do email
        Context context = new Context();
        context.setVariable("name", usuario.getNome());
        context.setVariable("token", token);

        // Envia email usando o template de recuperação de senha
        emailService.enviarEmail("Recuperação de senha", usuario.getEmail(), EmailTemplate.RECUPERAR_SENHA, context, Optional.empty());
    }

    /**
     * Atualiza a senha do usuário a partir do token de recuperação e nova senha informada.
     * @param dto DTO com nova senha.
     * @param token Token JWT de recuperação.
     */
    public void updatePassword(RecoverPasswordDto dto, String token) {
        // Valida token e extrai email
        String email = tokenService.validateToken(token);

        // Busca usuário pelo email
        Usuario usuario = usuarioService.buscarPorEmail(email)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Usuario não encontrado!"));

        // Atualiza a senha do usuário
        usuarioService.updatePassword(usuario, dto.senha());
    }

    /**
     * Troca a senha do usuário logado, validando a senha atual.
     * @param usuario Usuário logado.
     * @param dto DTO com senha atual e nova senha.
     */
    public void trocarSenha(Usuario usuario, TrocarSenhaDto dto) {
        // Verifica se a senha atual informada bate com a senha salva
        if (!passwordEncoder.matches(dto.senhaAtual(), usuario.getSenha())) {
            throw new RuntimeException("Senha atual incorreta.");
        }

        // Atualiza a senha para a nova senha
        usuarioService.updatePassword(usuario, dto.novaSenha());
    }
}

