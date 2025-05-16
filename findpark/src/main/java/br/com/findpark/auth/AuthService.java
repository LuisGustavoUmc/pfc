package br.com.findpark.auth;

import br.com.findpark.auth.dtos.AuthResponseDto;
import br.com.findpark.auth.dtos.LoginUsuarioDto;
import br.com.findpark.auth.dtos.RecoverDto;
import br.com.findpark.auth.dtos.RecoverPasswordDto;
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
    private PasswordEncoder passwordEncoder;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private EmailService emailService;

    public ResponseEntity<Usuario> usuarioLogged(String accessToken) {
        String subjectEmail = tokenService.validateToken(accessToken);

        Usuario usuario = usuarioService.buscarPorEmail(subjectEmail)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Usuario não encontrado!"));

        return ResponseEntity.ok(usuario);
    }

    public ResponseEntity<AuthResponseDto> login(LoginUsuarioDto loginUsuarioDto, HttpServletResponse response) {
        Usuario usuario = usuarioService.buscarPorEmail(loginUsuarioDto.email())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Usuario não encontrado!"));

        if (!passwordEncoder.matches(loginUsuarioDto.senha(), usuario.getSenha()))
            throw new UsuarioSenhaInvalidaException();

        if (usuario.getValidade() == Validade.PENDENTE)
            throw new UsuarioValidacaoException("Forbidden validation", usuario.getValidade(), HttpStatus.FORBIDDEN);

        if (usuario.getValidade() == Validade.RECUSADO)
            throw new UsuarioValidacaoException("Unauthorized validation", usuario.getValidade(), HttpStatus.UNAUTHORIZED);

        String accessToken = tokenService.generateAccessToken(usuario);

        return ResponseEntity.ok(new AuthResponseDto(
                usuario.getNome(),
                usuario.getEmail(),
                accessToken,
                usuario.getRole().name()
        ));
    }

    public ResponseEntity<Map<String, String>> logout(HttpServletResponse response, String accessToken) {
        return ResponseEntity.ok(Map.of("successfully", "true", "message", "Logout Successfully"));
    }

    public void sendEmailRecoverPassword(RecoverDto dto) {
        Usuario usuario = usuarioService.buscarPorEmail(dto.email())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Usuario não encontrado!"));

        String token = tokenService.generateAccessToken(usuario, 0, 30); // 30 minutos de validade

        Context context = new Context();
        context.setVariable("name", usuario.getNome());
        context.setVariable("token", token);
        emailService.enviarEmail("Recuperação de senha", usuario.getEmail(), EmailTemplate.RECUPERAR_SENHA, context, Optional.empty());
    }

    public void updatePassword(RecoverPasswordDto dto, String token) {
        String email = tokenService.validateToken(token);

        Usuario usuario = usuarioService.buscarPorEmail(email)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Usuario não encontrado!"));

        usuarioService.updatePassword(usuario, dto.senha());
    }
}
