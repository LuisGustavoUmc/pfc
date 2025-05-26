package br.com.findpark.controllers;

import br.com.findpark.entities.Proprietario;
import br.com.findpark.entities.Vaga;
import br.com.findpark.security.SecurityUtils;
import br.com.findpark.service.ProprietarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
    public ResponseEntity<Page<Proprietario>> buscarTodas(
            @RequestParam(value = "page", defaultValue = "0") Integer page,
            @RequestParam(value = "size", defaultValue = "12") Integer size,
            @RequestParam(value = "direction", defaultValue = "asc") String direction
    ) {
        var sortDirection = "desc".equalsIgnoreCase(direction) ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, "nome"));
        return ResponseEntity.ok(proprietarioService.buscarTodos(pageable));
    }

    @DeleteMapping
    public ResponseEntity<Void> deletar() {
        Proprietario usuarioAtual = (Proprietario) SecurityUtils.getCurrentUsuario();
        proprietarioService.deletar(usuarioAtual.getId());
        return ResponseEntity.noContent().build();
    }
}
