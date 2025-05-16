package br.com.findpark.controllers;

import br.com.findpark.auth.AuthService;
import br.com.findpark.dtos.RespostaDto;

import br.com.findpark.dtos.usuarios.AtualizarUsuarioDto;
import br.com.findpark.dtos.usuarios.RegistrarUsuarioDto;
import br.com.findpark.entities.Usuario;

import br.com.findpark.exceptions.usuario.RecursoNaoEncontradoException;
import br.com.findpark.security.SecurityUtils;
import br.com.findpark.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {
    
    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private AuthService authService;

    @GetMapping("/me")
    public ResponseEntity<Usuario> usuarioLogged(@RequestHeader("Authorization") String tokenAuthorization) {
        String accessToken = tokenAuthorization.replace("Bearer ", "");

        return authService.usuarioLogged(accessToken);
    }

    @PostMapping("/registrar")
    public ResponseEntity<Usuario> create(@RequestBody RegistrarUsuarioDto usuarioDto) {
        Usuario usuarioCriado = usuarioService.criarUsuario(usuarioDto);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(usuarioCriado);
    }

    @PatchMapping()
    public ResponseEntity<RespostaDto> update(@RequestBody AtualizarUsuarioDto atualizarUsuarioDto) {
        Usuario usuario = SecurityUtils.getCurrentUsuario();

        usuarioService.update(usuario, atualizarUsuarioDto);

        RespostaDto res = new RespostaDto(HttpStatus.OK, "Usuario updated", true, Optional.empty());
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(res);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Usuario> findById(@PathVariable String id) {
        Usuario usuario = usuarioService.buscarPorId(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Usuário não encontrado!"));

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(usuario);
    }

    @DeleteMapping()
    public ResponseEntity<RespostaDto> delete() {
        Usuario usuario = SecurityUtils.getCurrentUsuario();

        usuarioService.delete(usuario);
        RespostaDto res = new RespostaDto(HttpStatus.OK, "Usuario deleted", true, Optional.empty());

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(res);
    }
}
