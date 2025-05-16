package br.com.findpark.controllers;

import br.com.findpark.dtos.estacionamentos.AtualizarEstacionamentoDto;
import br.com.findpark.dtos.estacionamentos.DetalhesEstacionamentoDto;
import br.com.findpark.entities.Estacionamento;
import br.com.findpark.security.SecurityUtils;
import br.com.findpark.service.EstacionamentoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/estacionamentos")
public class EstacionamentoController {

    @Autowired
    private EstacionamentoService estacionamentoService;

    @PostMapping
    public ResponseEntity<Estacionamento> criarEstacionamento(@RequestBody Estacionamento estacionamento) {
        String idProprietario = SecurityUtils.getCurrentUsuario().getId();
        estacionamento.setIdProprietario(idProprietario);
        Estacionamento salvo = estacionamentoService.criarEstacionamento(estacionamento);
        return ResponseEntity.ok(salvo);
    }

    @GetMapping("/meus")
    public ResponseEntity<List<Estacionamento>> listarMeusEstacionamentos() {
        String idProprietario = SecurityUtils.getCurrentUsuario().getId();
        List<Estacionamento> lista = estacionamentoService.buscarPorProprietario(idProprietario);
        return ResponseEntity.ok(lista);
    }

    @GetMapping
    public ResponseEntity<List<Estacionamento>> listarTodos() {
        List<Estacionamento> estacionamentos = estacionamentoService.buscarTodosEstacionamentos();
        return ResponseEntity.ok(estacionamentos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Estacionamento> buscarPorId(@PathVariable String id) {
        Estacionamento estacionamento = estacionamentoService.buscarEstacionamentoPorId(id);
        return ResponseEntity.ok(estacionamento);
    }

    @GetMapping("/{id}/detalhes")
    public ResponseEntity<DetalhesEstacionamentoDto> buscarDetalhes(@PathVariable String id) {
        DetalhesEstacionamentoDto dto = estacionamentoService.buscarComVagasDisponiveis(id);
        return ResponseEntity.ok(dto);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> atualizar(@PathVariable String id, @RequestBody AtualizarEstacionamentoDto dto) {
        Estacionamento estacionamento = estacionamentoService.buscarEstacionamentoPorId(id);
        estacionamentoService.atualizarEstacionamento(estacionamento, dto);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable String id) {
        estacionamentoService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
