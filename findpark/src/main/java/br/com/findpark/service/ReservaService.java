package br.com.findpark.service;

import br.com.findpark.dtos.reservas.StatusReserva;
import br.com.findpark.entities.Estacionamento;
import br.com.findpark.entities.Reserva;
import br.com.findpark.entities.Vaga;
import br.com.findpark.exceptions.usuario.RecursoJaExisteException;
import br.com.findpark.exceptions.usuario.RecursoNaoEncontradoException;
import br.com.findpark.repositories.EstacionamentoRepository;
import br.com.findpark.repositories.ReservaRepository;
import br.com.findpark.repositories.VagaRepository;
import br.com.findpark.security.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Service
public class ReservaService {

    @Autowired
    private ReservaRepository reservaRepository;

    @Autowired
    private VagaRepository vagaRepository;

    @Autowired
    private EstacionamentoRepository estacionamentoRepository;

    public Reserva criarReserva(Reserva reserva) {
        reserva.setClienteId(SecurityUtils.getCurrentUsuario().getId());
        reserva.setStatus(StatusReserva.ATIVA);

        if (reserva.getDataHoraInicio() == null) {
            reserva.setDataHoraInicio(LocalDateTime.now());
        }

        if (reserva.getDataHoraFim() == null || reserva.getDataHoraFim().isBefore(reserva.getDataHoraInicio())) {
            throw new IllegalArgumentException("Horário final inválido ou não informado.");
        }

        if (reserva.getPlacaVeiculo() == null || reserva.getPlacaVeiculo().isBlank()) {
            throw new IllegalArgumentException("A placa do veículo é obrigatória.");
        }

        Estacionamento estacionamento = estacionamentoRepository.findById(reserva.getEstacionamentoId())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Estacionamento não encontrado"));

        Vaga vaga = vagaRepository.findById(reserva.getVagaId())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Vaga não encontrada"));

        if (!vaga.getEstacionamentoId().equals(estacionamento.getId())) {
            throw new IllegalArgumentException("A vaga não pertence ao estacionamento informado.");
        }

        validarHorarioDeFuncionamento(estacionamento, reserva.getDataHoraInicio());
        validarHorarioDeFuncionamento(estacionamento, reserva.getDataHoraFim());

        boolean vagaReservada = reservaRepository
                .existsByVagaIdAndDataHoraFimAfterAndDataHoraInicioBefore(
                        reserva.getVagaId(), reserva.getDataHoraInicio(), reserva.getDataHoraFim());

        if (vagaReservada) {
            throw new RecursoJaExisteException("Esta vaga já está reservada nesse horário.");
        }

        return reservaRepository.save(reserva);
    }


    private void validarHorarioDeFuncionamento(Estacionamento estacionamento, LocalDateTime horarioInicio) {
        LocalTime hora = horarioInicio.toLocalTime();
        if (hora.isBefore(estacionamento.getHoraAbertura()) || hora.isAfter(estacionamento.getHoraFechamento())) {
            throw new IllegalArgumentException("O estacionamento estará fechado nesse horário.");
        }
    }

    public List<Reserva> listarMinhasReservas() {
        String clienteId = SecurityUtils.getCurrentUsuario().getId();
        return reservaRepository.findByClienteId(clienteId);
    }

    public Reserva buscarPorId(String id) {
        return reservaRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Reserva não encontrada"));
    }

    public void cancelarReserva(String id) {
        Reserva reserva = buscarPorId(id);
        if (!reserva.getClienteId().equals(SecurityUtils.getCurrentUsuario().getId())) {
            throw new SecurityException("Reserva não pertence ao usuário logado");
        }
        reserva.setStatus(StatusReserva.CANCELADA);
        reservaRepository.save(reserva);
    }
}
