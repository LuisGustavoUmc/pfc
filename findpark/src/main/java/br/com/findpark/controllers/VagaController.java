package br.com.findpark.controllers;

import br.com.findpark.dtos.estacionamentos.VagaComEstacionamentoDto;
import br.com.findpark.entities.Vaga;
import br.com.findpark.entities.enums.vagas.StatusVaga;
import br.com.findpark.service.VagaService;
import org.springframework.beans.factory.annotation.Autowired;
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
    public List<Vaga> buscarTodas() {
        return vagaService.buscarTodas();
    }

    @GetMapping("/disponiveis")
    public List<VagaComEstacionamentoDto> buscarVagasDisponiveisComEstacionamento() {
        return vagaService.buscarVagasComEstacionamento();
    }

    @GetMapping("/detalhes/{id}")
    public VagaComEstacionamentoDto buscarDetalhesPorId(@PathVariable String id) {
        return vagaService.buscarDetalhesPorId(id);
    }

    @GetMapping("/estacionamento/{estacionamentoId}")
    public List<Vaga> buscarPorEstacionamento(@PathVariable String estacionamentoId) {
        return vagaService.buscarPorEstacionamento(estacionamentoId);
    }

    @GetMapping("/estacionamento/{estacionamentoId}/status/{status}")
    public List<Vaga> buscarPorEstacionamentoEStatus(@PathVariable String estacionamentoId, @PathVariable StatusVaga status) {
        return vagaService.buscarPorEstacionamentoEStatus(estacionamentoId, status);
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
