package br.com.findpark.auth;

import br.com.findpark.auth.dtos.AuthResponseDto;
import br.com.findpark.auth.dtos.LoginUsuarioDto;
import br.com.findpark.auth.dtos.RecoverDto;
import br.com.findpark.auth.dtos.RecoverPasswordDto;
import br.com.findpark.dtos.RespostaDto;
import br.com.findpark.entities.Usuario;
import br.com.findpark.entities.enums.usuarios.Validade;
import br.com.findpark.service.UsuarioService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @Autowired
    private UsuarioService usuarioService;

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDto> login(@RequestBody LoginUsuarioDto loginUsuarioDto, HttpServletResponse response) {
        return authService.login(loginUsuarioDto, response);
    }

    @GetMapping("/logout")
    public ResponseEntity<Map<String, String>> logout(
            HttpServletResponse response,
            @RequestHeader("Authorization") String tokenAuthorization
    ) {
        String accessToken = tokenAuthorization.replace("Bearer ", "");

        return authService.logout(response, accessToken);
    }

    @PostMapping("/recuperar")
    public ResponseEntity<RespostaDto> recover(@RequestBody RecoverDto dto) {
        authService.sendEmailRecoverPassword(dto);

        RespostaDto res = new RespostaDto(HttpStatus.ACCEPTED, "Email sending", true, Optional.empty());
        return ResponseEntity
                .status(HttpStatus.ACCEPTED)
                .body(res);
    }

    @GetMapping("/confirmar-cadastro/{token}")
    public ResponseEntity<RespostaDto> confirmarCadastro(@PathVariable String token) {

        Optional<Usuario> optUsuario = usuarioService.buscarPorTokenConfirmacao(token);

        if (optUsuario.isEmpty()) {
            System.out.println("Token inválido");
            return ResponseEntity.ok(
                    new RespostaDto(HttpStatus.OK, "Token inválido ou expirado.", false, Optional.empty()));
        }

        Usuario usuario = optUsuario.get();

        if (usuario.getValidade() != Validade.PENDENTE) {
            System.out.println("Usuário já validado");
            return ResponseEntity.ok(
                    new RespostaDto(HttpStatus.OK, "Cadastro já confirmado ou inválido.", false, Optional.empty()));
        }

        usuario.setValidade(Validade.VALIDADO);
        usuario.setTokenConfirmacao(null);
        usuarioService.salvar(usuario);

        System.out.println("Cadastro confirmado com sucesso");
        return ResponseEntity.ok(
                new RespostaDto(HttpStatus.OK, "Cadastro confirmado com sucesso!", true, Optional.empty()));
    }

    @PatchMapping("/atualizar-senha/{token}")
    public ResponseEntity<RespostaDto> updatePassword(
            @PathVariable String token,
            @RequestBody RecoverPasswordDto recoverPasswordDto

    ) {
        authService.updatePassword(recoverPasswordDto, token);

        var res = new RespostaDto(HttpStatus.ACCEPTED, "Senha Atualizada", true, Optional.empty());

        return ResponseEntity
                .status(HttpStatus.ACCEPTED)
                .body(res);
    }
}
