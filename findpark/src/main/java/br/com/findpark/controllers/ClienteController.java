package br.com.findpark.controllers;

import br.com.findpark.dtos.RespostaDto;
import br.com.findpark.dtos.cliente.AtualizarPlacaDto;
import br.com.findpark.dtos.cliente.PlacaDto;
import br.com.findpark.service.ClienteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/clientes/placas")
public class ClienteController {

    @Autowired
    private ClienteService clienteService;

    // GET /api/clientes/placas - retorna lista de placas do cliente
    @GetMapping
    public List<String> listarPlacas() {
        return clienteService.listarPlacas();
    }

    // POST /api/clientes/placas - adiciona uma nova placa
    @PostMapping
    public RespostaDto adicionarPlaca(@RequestBody PlacaDto dto) {
        clienteService.adicionarPlaca(dto.placa());
        return new RespostaDto(HttpStatus.CREATED, "Placa adicionada", true, Optional.empty());
    }

    // PUT /api/clientes/placas - atualiza uma placa existente
    @PutMapping
    public RespostaDto atualizarPlaca(@RequestBody AtualizarPlacaDto dto) {
        clienteService.atualizarPlaca(dto.antiga(), dto.nova());
        return new RespostaDto(HttpStatus.OK, "Placa atualizada", true, Optional.empty());
    }

    // DELETE /api/clientes/placas?placa=xxx - remove uma placa pelo parâmetro
    @DeleteMapping
    public RespostaDto removerPlaca(@RequestParam String placa) {
        clienteService.removerPlaca(placa);
        return new RespostaDto(HttpStatus.OK, "Placa removida", true, Optional.empty());
    }
}
