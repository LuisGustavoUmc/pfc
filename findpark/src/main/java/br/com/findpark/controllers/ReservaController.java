package br.com.findpark.controllers;

import br.com.findpark.dtos.RespostaDto;
import br.com.findpark.dtos.reservas.ReservaDetalhadaDto;
import br.com.findpark.dtos.reservas.StatusReserva;
import br.com.findpark.entities.Reserva;
import br.com.findpark.entities.Vaga;
import br.com.findpark.service.ReservaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/reservas")
public class ReservaController {

    @Autowired
    private ReservaService reservaService;


    // Cria uma nova reserva a partir do objeto Reserva recebido no corpo da requisição
    @PostMapping
    public Reserva criarReserva(@RequestBody Reserva reserva) {
        return reservaService.criarReserva(reserva);
    }

    // Lista reservas dos estacionamentos do proprietário autenticado, com paginação, ordenação e filtros opcionais (status, placa)
    @GetMapping("/proprietario")
    public ResponseEntity<Page<ReservaDetalhadaDto>> listarReservasDosMeusEstacionamentos(
            @RequestParam(value = "page", defaultValue = "0") Integer page,
            @RequestParam(value = "size", defaultValue = "12") Integer size,
            @RequestParam(value = "direction", defaultValue = "asc") String direction,
            @RequestParam(value = "status", required = false) StatusReserva status,
            @RequestParam(value = "placa", required = false) String placa
    ) {
        var sortDirection = "desc".equalsIgnoreCase(direction) ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, "dataHoraInicio"));
        return ResponseEntity.ok(reservaService.listarReservasDosMeusEstacionamentos(pageable, status, placa));
    }

    // Lista as reservas do usuário autenticado, com paginação, ordenação e filtro opcional por status
    @GetMapping
    public ResponseEntity<Page<ReservaDetalhadaDto>> listarMinhasReservas(
            @RequestParam(value = "page", defaultValue = "0") Integer page,
            @RequestParam(value = "size", defaultValue = "12") Integer size,
            @RequestParam(value = "direction", defaultValue = "asc") String direction,
            @RequestParam(value = "status", required = false) StatusReserva status
    ) {
        var sortDirection = "desc".equalsIgnoreCase(direction) ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, "dataHoraInicio"));
        return ResponseEntity.ok(reservaService.listarMinhasReservas(pageable, status));
    }

    // Busca uma reserva pelo ID informado
    @GetMapping("/{id}")
    public Reserva buscarPorId(@PathVariable String id) {
        return reservaService.buscarPorId(id);
    }

    // Cancela uma reserva pelo ID informado e retorna resposta com status e mensagem
    @DeleteMapping("/{id}")
    public RespostaDto cancelar(@PathVariable String id) {
        reservaService.cancelarReserva(id);
        return new RespostaDto(HttpStatus.OK, "Reserva cancelada", true, Optional.empty());
    }

    // Cancela uma reserva pelo ID informado e retorna resposta com status e mensagem
    @DeleteMapping("/proprietario/{id}")
    public RespostaDto cancelarReservaComoProprietario(@PathVariable String id) {
        reservaService.cancelarReservaComoProprietario(id);
        return new RespostaDto(HttpStatus.OK, "Reserva cancelada com sucesso", true, Optional.empty());
    }
}
