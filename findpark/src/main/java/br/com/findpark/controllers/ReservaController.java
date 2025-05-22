package br.com.findpark.controllers;

import br.com.findpark.dtos.RespostaDto;
import br.com.findpark.dtos.reservas.ReservaDetalhadaDto;
import br.com.findpark.entities.Reserva;
import br.com.findpark.service.ReservaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/reservas")
public class ReservaController {

    @Autowired
    private ReservaService reservaService;

    @PostMapping
    public Reserva criarReserva(@RequestBody Reserva reserva) {
        return reservaService.criarReserva(reserva);
    }

    @GetMapping
    public List<ReservaDetalhadaDto> listarMinhasReservas() {
        return reservaService.listarMinhasReservas();
    }

    @GetMapping("/{id}")
    public Reserva buscarPorId(@PathVariable String id) {
        return reservaService.buscarPorId(id);
    }

    @DeleteMapping("/{id}")
    public RespostaDto cancelar(@PathVariable String id) {
        reservaService.cancelarReserva(id);
        return new RespostaDto(HttpStatus.OK, "Reserva cancelada", true, Optional.empty());
    }
}
