package br.com.findpark.controllers;

import br.com.findpark.dtos.estacionamentos.VagaComEstacionamentoDto;
import br.com.findpark.entities.Vaga;
import br.com.findpark.entities.enums.vagas.StatusVaga;
import br.com.findpark.service.VagaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/vagas")
public class VagaController {

    @Autowired
    private VagaService vagaService;

    @PostMapping
    public Vaga criar(@RequestBody Vaga vaga) {
        return vagaService.criar(vaga);
    }

    @GetMapping("/{id}")
    public Vaga buscarPorId(@PathVariable String id) {
        return vagaService.buscarPorId(id);
    }

    @GetMapping
    public ResponseEntity<Page<Vaga>> buscarTodas(
            @RequestParam(value = "page", defaultValue = "0") Integer page,
            @RequestParam(value = "size", defaultValue = "12") Integer size,
            @RequestParam(value = "direction", defaultValue = "asc") String direction
    ) {
        var sortDirection = "desc".equalsIgnoreCase(direction) ? Direction.DESC : Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, "tipo"));
        return ResponseEntity.ok(vagaService.buscarTodas(pageable));
    }

    @GetMapping("/disponiveis")
    public ResponseEntity<Page<VagaComEstacionamentoDto>> buscarVagasDisponiveisComEstacionamento(
            @RequestParam(value = "page", defaultValue = "0") Integer page,
            @RequestParam(value = "size", defaultValue = "12") Integer size,
            @RequestParam(value = "direction", defaultValue = "asc") String direction
    ) {
        var sortDirection = "desc".equalsIgnoreCase(direction) ? Direction.DESC : Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, "tipo"));
        return ResponseEntity.ok(vagaService.buscarVagasComEstacionamento(pageable));
    }

    @GetMapping("/detalhes/{id}")
    public VagaComEstacionamentoDto buscarDetalhesPorId(@PathVariable String id) {
        return vagaService.buscarDetalhesPorId(id);
    }

    @GetMapping("/filtrar")
    public ResponseEntity<Page<VagaComEstacionamentoDto>> buscarVagasFiltradas(
            @RequestParam String termo,
            @RequestParam(value = "page", defaultValue = "0") Integer page,
            @RequestParam(value = "size", defaultValue = "12") Integer size,
            @RequestParam(value = "direction", defaultValue = "asc") String direction
    ) {
        var sortDirection = "desc".equalsIgnoreCase(direction) ? Direction.DESC : Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, "tipo"));
        return ResponseEntity.ok(vagaService.buscarPorTermo(termo, pageable));
    }

    @GetMapping("/estacionamento/{estacionamentoId}")
    public ResponseEntity<Page<Vaga>> buscarPorEstacionamento(
            @PathVariable String estacionamentoId,
            @RequestParam(value = "page", defaultValue = "0") Integer page,
            @RequestParam(value = "size", defaultValue = "12") Integer size,
            @RequestParam(value = "direction", defaultValue = "asc") String direction
    ) {
        var sortDirection = "desc".equalsIgnoreCase(direction) ? Direction.DESC : Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, "tipo"));
        return ResponseEntity.ok(vagaService.buscarPorEstacionamento(estacionamentoId, pageable));
    }

    @GetMapping("/estacionamento/{estacionamentoId}/status/{status}")
    public Page<Vaga> buscarPorEstacionamentoEStatus(
            @PathVariable String estacionamentoId,
            @PathVariable StatusVaga status,
            @RequestParam(value = "page", defaultValue = "0") Integer page,
            @RequestParam(value = "size", defaultValue = "12") Integer size,
            @RequestParam(value = "direction", defaultValue = "asc") String direction) {
        var sortDirection = "desc".equalsIgnoreCase(direction) ? Direction.DESC : Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, "tipo"));
        return vagaService.buscarPorEstacionamentoEStatus(estacionamentoId, status, pageable);
    }

    @PutMapping("/{id}")
    public Vaga atualizar(@PathVariable String id, @RequestBody Vaga vaga) {
        return vagaService.atualizar(id, vaga);
    }

    @DeleteMapping("/{id}")
    public void deletar(@PathVariable String id) {
        vagaService.deletar(id);
    }
}
