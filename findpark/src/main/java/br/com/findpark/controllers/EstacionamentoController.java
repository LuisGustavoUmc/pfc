package br.com.findpark.controllers;

import br.com.findpark.dtos.estacionamentos.AtualizarEstacionamentoDto;
import br.com.findpark.dtos.estacionamentos.DetalhesEstacionamentoDto;
import br.com.findpark.entities.Estacionamento;
import br.com.findpark.security.SecurityUtils;
import br.com.findpark.service.EstacionamentoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/estacionamentos")
public class EstacionamentoController {

    @Autowired
    private EstacionamentoService estacionamentoService;

    // Cria um novo estacionamento associado ao usuário logado
    @PostMapping
    public ResponseEntity<Estacionamento> criarEstacionamento(@RequestBody Estacionamento estacionamento) {
        String idProprietario = SecurityUtils.getCurrentUsuario().getId();
        estacionamento.setIdProprietario(idProprietario);
        Estacionamento salvo = estacionamentoService.criarEstacionamento(estacionamento);
        return ResponseEntity.ok(salvo);
    }

    // Lista os estacionamentos do usuário proprietario autenticado com paginação e ordenação
    @GetMapping("/meus")
    public ResponseEntity<Page<Estacionamento>> listarMeusEstacionamentos(
            @RequestParam(value = "page", defaultValue = "0") Integer page,
            @RequestParam(value = "size", defaultValue = "12") Integer size,
            @RequestParam(value = "direction", defaultValue = "asc") String direction
    ) {
        var sortDirection = "desc".equalsIgnoreCase(direction) ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, "nome"));
        Page<Estacionamento> lista = estacionamentoService.buscarPorProprietario(pageable);

        return ResponseEntity.ok(lista);
    }

    // Lista todos os estacionamentos com paginação e ordenação
    @GetMapping
    public ResponseEntity<Page<Estacionamento>> listarTodos (
            @RequestParam(value = "page", defaultValue = "0") Integer page,
            @RequestParam(value = "size", defaultValue = "12") Integer size,
            @RequestParam(value = "direction", defaultValue = "asc") String direction
    ) {
        var sortDirection = "desc".equalsIgnoreCase(direction) ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, "nome"));
        return ResponseEntity.ok(estacionamentoService.buscarTodosEstacionamentos(pageable));
    }

    // Busca estacionamento pelo ID
    @GetMapping("/{id}")
    public ResponseEntity<Estacionamento> buscarPorId(@PathVariable String id) {
        Estacionamento estacionamento = estacionamentoService.buscarEstacionamentoPorId(id);
        return ResponseEntity.ok(estacionamento);
    }

    //listar com vagas disponíveis para visualização do cliente
    @GetMapping("/disponiveis")
    public ResponseEntity<Page<DetalhesEstacionamentoDto>> listarDisponiveis(
            @RequestParam(value = "page", defaultValue = "0") Integer page,
            @RequestParam(value = "size", defaultValue = "12") Integer size,
            @RequestParam(value = "direction", defaultValue = "asc") String direction,
            @RequestParam(value = "termo", required = false) String termo
    ) {
        Sort.Direction sortDirection = "desc".equalsIgnoreCase(direction) ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, "nome"));

        Page<DetalhesEstacionamentoDto> resultado = estacionamentoService.buscarComVagasDisponiveis(null, termo, pageable);
        return ResponseEntity.ok(resultado);
    }

    // Busca detalhes do estacionamento com vagas disponíveis, com paginação e ordenação
    @GetMapping("/{id}/detalhes")
    public ResponseEntity<Page<DetalhesEstacionamentoDto>> buscarDetalhes (
            @PathVariable String id,
            @RequestParam(value = "page", defaultValue = "0") Integer page,
            @RequestParam(value = "size", defaultValue = "12") Integer size,
            @RequestParam(value = "direction", defaultValue = "asc") String direction
    ) {
        var sortDirection = "desc".equalsIgnoreCase(direction) ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, "nome"));
        return ResponseEntity.ok(estacionamentoService.buscarComVagasDisponiveis(id, null, pageable));
    }

    // Atualiza os dados do estacionamento informado pelo ID
    @PutMapping("/{id}")
    public ResponseEntity<Void> atualizar(@PathVariable String id, @RequestBody AtualizarEstacionamentoDto dto) {
        Estacionamento estacionamento = estacionamentoService.buscarEstacionamentoPorId(id);
        estacionamentoService.atualizarEstacionamento(estacionamento, dto);
        return ResponseEntity.noContent().build();
    }

    // Remove o estacionamento pelo ID
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable String id) {
        estacionamentoService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
