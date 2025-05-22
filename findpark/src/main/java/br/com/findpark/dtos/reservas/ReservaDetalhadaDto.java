package br.com.findpark.dtos.reservas;

import br.com.findpark.dtos.estacionamentos.DetalhesEstacionamentoDto;
import br.com.findpark.dtos.vagas.VagaDto;

import java.time.LocalDateTime;

public record ReservaDetalhadaDto(
        String id,
        String placaVeiculo,
        LocalDateTime dataHoraInicio,
        LocalDateTime dataHoraFim,
        StatusReserva status,
        DetalhesEstacionamentoDto estacionamento,
        VagaDto vaga
) {}

