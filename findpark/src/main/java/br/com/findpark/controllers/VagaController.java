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

    // Cria uma nova vaga com os dados recebidos no corpo da requisição
    @PostMapping
    public Vaga criar(@RequestBody Vaga vaga) {
        return vagaService.criar(vaga);
    }

    // Busca uma vaga pelo seu ID
    @GetMapping("/{id}")
    public Vaga buscarPorId(@PathVariable String id) {
        return vagaService.buscarPorId(id);
    }

    // Busca todas as vagas paginadas e ordenadas pelo campo "tipo"
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

    // Busca vagas disponíveis junto com dados do estacionamento, paginadas e ordenadas por "tipo"
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

    // Busca detalhes de uma vaga específica por ID (incluindo dados do estacionamento)
    @GetMapping("/detalhes/{id}")
    public VagaComEstacionamentoDto buscarDetalhesPorId(@PathVariable String id) {
        return vagaService.buscarDetalhesPorId(id);
    }

    // Busca vagas filtradas pelo termo informado (ex: nome, descrição, etc.) com paginação
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

    // Busca vagas por estacionamento específico com paginação e ordenação
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

    // Busca vagas por estacionamento e status (ex: disponível, ocupada), com paginação
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

    // Atualiza os dados de uma vaga pelo ID
    @PutMapping("/{id}")
    public Vaga atualizar(@PathVariable String id, @RequestBody Vaga vaga) {
        return vagaService.atualizar(id, vaga);
    }

    // Deleta uma vaga pelo ID
    @DeleteMapping("/{id}")
    public void deletar(@PathVariable String id) {
        vagaService.deletar(id);
    }
}
