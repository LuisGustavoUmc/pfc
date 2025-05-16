package br.com.findpark.controllers;

import br.com.findpark.entities.Proprietario;
import br.com.findpark.security.SecurityUtils;
import br.com.findpark.service.ProprietarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/proprietarios")
public class ProprietarioController {

    @Autowired
    private ProprietarioService proprietarioService;

    @GetMapping("/{id}")
    public ResponseEntity<Proprietario> buscarPorId(@PathVariable String id) {
        Proprietario proprietario = proprietarioService.buscarPorId(id);
        return ResponseEntity.ok(proprietario);
    }

    @GetMapping
    public ResponseEntity<List<Proprietario>> buscarTodos() {
        return ResponseEntity.ok(proprietarioService.buscarTodos());
    }

    @DeleteMapping
    public ResponseEntity<Void> deletar() {
        Proprietario usuarioAtual = (Proprietario) SecurityUtils.getCurrentUsuario();
        proprietarioService.deletar(usuarioAtual.getId());
        return ResponseEntity.noContent().build();
    }
}
