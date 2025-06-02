package br.com.findpark.auth;

import br.com.findpark.auth.dtos.*;
import br.com.findpark.dtos.RespostaDto;
import br.com.findpark.entities.Usuario;
import br.com.findpark.entities.enums.usuarios.Validade;
import br.com.findpark.service.UsuarioService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService; // Serviço que implementa a lógica de autenticação

    @Autowired
    private UsuarioService usuarioService; // Serviço para operações com usuários

    /**
     * Endpoint para login do usuário.
     * Recebe email e senha, retorna dados do usuário e token JWT.
     */
    @PostMapping("/login")
    public ResponseEntity<AuthResponseDto> login(@RequestBody LoginUsuarioDto loginUsuarioDto, HttpServletResponse response) {
        return authService.login(loginUsuarioDto, response);
    }

    /**
     * Endpoint para logout.
     * Recebe o token JWT via header Authorization e retorna mensagem de sucesso.
     * Não invalida token no backend (stateless JWT).
     */
    @GetMapping("/logout")
    public ResponseEntity<Map<String, String>> logout(
            HttpServletResponse response,
            @RequestHeader("Authorization") String tokenAuthorization
    ) {
        // Remove prefixo "Bearer " para extrair token puro
        String accessToken = tokenAuthorization.replace("Bearer ", "");
        return authService.logout(response, accessToken);
    }

    /**
     * Endpoint para solicitar recuperação de senha.
     * Envia email com token para reset de senha.
     */
    @PostMapping("/recuperar")
    public ResponseEntity<RespostaDto> recover(@RequestBody RecoverDto dto) {
        authService.sendEmailRecoverPassword(dto);

        RespostaDto res = new RespostaDto(HttpStatus.ACCEPTED, "Email sending", true, Optional.empty());
        return ResponseEntity
                .status(HttpStatus.ACCEPTED)
                .body(res);
    }

    /**
     * Endpoint para confirmar cadastro do usuário via token.
     * Atualiza o status do usuário de pendente para validado.
     */
    @GetMapping("/confirmar-cadastro/{token}")
    public ResponseEntity<RespostaDto> confirmarCadastro(@PathVariable String token) {

        Optional<Usuario> optUsuario = usuarioService.buscarPorTokenConfirmacao(token);

        if (optUsuario.isEmpty()) {
            System.out.println("Token inválido");
            return ResponseEntity.ok(
                    new RespostaDto(HttpStatus.OK, "Token inválido ou expirado.", false, Optional.empty()));
        }

        Usuario usuario = optUsuario.get();

        // Verifica se usuário já foi validado
        if (usuario.getValidade() != Validade.PENDENTE) {
            System.out.println("Usuário já validado");
            return ResponseEntity.ok(
                    new RespostaDto(HttpStatus.OK, "Cadastro já confirmado ou inválido.", false, Optional.empty()));
        }

        // Atualiza status do usuário para validado e remove token de confirmação
        usuario.setValidade(Validade.VALIDADO);
        usuario.setTokenConfirmacao(null);
        usuarioService.salvar(usuario);

        System.out.println("Cadastro confirmado com sucesso");
        return ResponseEntity.ok(
                new RespostaDto(HttpStatus.OK, "Cadastro confirmado com sucesso!", true, Optional.empty()));
    }

    /**
     * Endpoint para atualizar senha após recuperação via token.
     */
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

    /**
     * Endpoint para trocar a senha do usuário logado.
     * Recebe senha atual e nova senha para alteração.
     */
    @PatchMapping("/trocar-senha")
    public ResponseEntity<RespostaDto> trocarSenha(
            @RequestBody TrocarSenhaDto dto,
            @AuthenticationPrincipal Usuario usuario) {

        authService.trocarSenha(usuario, dto);

        var resposta = new RespostaDto(HttpStatus.OK, "Senha alterada com sucesso", true, Optional.empty());
        return ResponseEntity.ok(resposta);
    }
}
