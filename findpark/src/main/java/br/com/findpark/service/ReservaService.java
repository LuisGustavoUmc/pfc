package br.com.findpark.service;

import br.com.findpark.dtos.estacionamentos.DetalhesEstacionamentoDto;
import br.com.findpark.dtos.reservas.ReservaDetalhadaDto;
import br.com.findpark.dtos.reservas.StatusReserva;
import br.com.findpark.dtos.vagas.VagaDto;
import br.com.findpark.entities.Estacionamento;
import br.com.findpark.entities.Reserva;
import br.com.findpark.entities.Vaga;
import br.com.findpark.exceptions.reserva.ReservaConflitanteException;
import br.com.findpark.exceptions.usuario.RecursoNaoEncontradoException;
import br.com.findpark.repositories.EstacionamentoRepository;
import br.com.findpark.repositories.ReservaRepository;
import br.com.findpark.repositories.VagaRepository;
import br.com.findpark.security.SecurityUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Objects;

@Slf4j
@Service
public class ReservaService {

    @Autowired
    private ReservaRepository reservaRepository;

    @Autowired
    private VagaRepository vagaRepository;

    @Autowired
    private VagaService vagaService;

    @Autowired
    private EstacionamentoService estacionamentoService;

    @Autowired
    private EstacionamentoRepository estacionamentoRepository;

    public Reserva criarReserva(Reserva reserva) {
        log.info("Tentando criar reserva: início={} fim={} vaga={} estacionamento={}",
                reserva.getDataHoraInicio(),
                reserva.getDataHoraFim(),
                reserva.getVagaId(),
                reserva.getEstacionamentoId());

        reserva.setClienteId(SecurityUtils.getCurrentUsuario().getId());
        reserva.setStatus(StatusReserva.ATIVA);

        validarDadosIniciais(reserva);

        Estacionamento estacionamento = buscarEstacionamento(reserva.getEstacionamentoId());

        Vaga vaga = buscarVaga(reserva.getVagaId());
        validarVagaPertenceAoEstacionamento(vaga, estacionamento);
        validarHorarioFuncionamento(estacionamento, reserva.getDataHoraInicio());
        validarHorarioFuncionamento(estacionamento, reserva.getDataHoraFim());

        // ✅ Validação de conflito para a mesma vaga no mesmo período
        boolean vagaOcupada = reservaRepository.existsByVagaIdAndStatusAndDataHoraFimAfterAndDataHoraInicioBefore(
                reserva.getVagaId(),
                StatusReserva.ATIVA,
                reserva.getDataHoraInicio(),
                reserva.getDataHoraFim()
        );

        if (vagaOcupada) {
            throw new ReservaConflitanteException("Já existe uma reserva para essa vaga no período selecionado.");
        }

        // ✅ Validação de placa com reserva no mesmo horário (evita duplicidade por veículo)
        boolean placaComReserva = reservaRepository.existsByPlacaVeiculoAndStatusAndDataHoraFimAfterAndDataHoraInicioBefore(
                reserva.getPlacaVeiculo(),
                StatusReserva.ATIVA,
                reserva.getDataHoraInicio(),
                reserva.getDataHoraFim()
        );

        if (placaComReserva) {
            throw new ReservaConflitanteException("Essa placa já possui uma reserva ativa no período selecionado.");
        }

        Reserva reservaSalva = reservaRepository.save(reserva);
        log.info("Reserva criada com sucesso para o cliente {} na vaga {}", reserva.getClienteId(), reserva.getVagaId());
        return reservaSalva;
    }

    private void validarDadosIniciais(Reserva reserva) {
        if (reserva.getDataHoraInicio() == null) {
            reserva.setDataHoraInicio(LocalDateTime.now());
        }

        if (reserva.getDataHoraFim() == null || reserva.getDataHoraFim().isBefore(reserva.getDataHoraInicio())) {
            throw new IllegalArgumentException("Horário final inválido ou não informado.");
        }

        if (reserva.getDataHoraInicio().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Não é possível criar reserva com início no passado.");
        }

        if (reserva.getPlacaVeiculo() == null || reserva.getPlacaVeiculo().isBlank()) {
            throw new IllegalArgumentException("A placa do veículo é obrigatória.");
        }
    }

    private Estacionamento buscarEstacionamento(String estacionamentoId) {
        return estacionamentoRepository.findById(estacionamentoId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Estacionamento não encontrado"));
    }

    private Vaga buscarVaga(String vagaId) {
        return vagaRepository.findById(vagaId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Vaga não encontrado"));
    }

    private void validarVagaPertenceAoEstacionamento(Vaga vaga, Estacionamento estacionamento) {
        if (!vaga.getEstacionamentoId().equals(estacionamento.getId())) {
            throw new IllegalArgumentException("A vaga não pertence ao estacionamento informado.");
        }
    }

    private void atualizarStatusReservasExpiradas(List<Reserva> reservas) {
        LocalDateTime agora = LocalDateTime.now();

        reservas.stream()
                .filter(r -> r.getStatus() == StatusReserva.ATIVA && r.getDataHoraFim().isBefore(agora))
                .forEach(reserva -> {
                    reserva.setStatus(StatusReserva.FINALIZADA);
                    reservaRepository.save(reserva);
                });
    }

    private void validarHorarioFuncionamento(Estacionamento estacionamento, LocalDateTime dataHora) {
        LocalTime hora = dataHora.toLocalTime();
        LocalTime abertura = estacionamento.getHoraAbertura();
        LocalTime fechamento = estacionamento.getHoraFechamento();

        log.info("Validando horário: hora={}, abertura={}, fechamento={}", hora, abertura, fechamento);

        if (hora.isBefore(abertura) || hora.isAfter(fechamento)) {
            if (!hora.equals(fechamento)) {
                throw new IllegalArgumentException("O estacionamento estará fechado nesse horário.");
            }
        }
    }

    public List<ReservaDetalhadaDto> listarMinhasReservas() {
        String clienteId = SecurityUtils.getCurrentUsuario().getId();
        List<Reserva> reservas = reservaRepository.findByClienteId(clienteId);

        atualizarStatusReservasExpiradas(reservas);

        return reservas.stream()
                .map(reserva -> {
                    Estacionamento estacionamento = null;
                    Vaga vaga = null;

                    try {
                        estacionamento = buscarEstacionamento(reserva.getEstacionamentoId());
                    } catch (Exception e) {
                        log.warn("Estacionamento não encontrado para ID: {}", reserva.getEstacionamentoId());
                    }

                    try {
                        vaga = buscarVaga(reserva.getVagaId());
                    } catch (Exception e) {
                        log.warn("Vaga não encontrada para ID: {}", reserva.getVagaId());
                    }

                    if (estacionamento == null || vaga == null) {
                        log.warn("Reserva ignorada devido a dados incompletos. Reserva ID: {}", reserva.getId());
                        return null;
                    }

                    return mapearParaDto(reserva, estacionamento, vaga);
                })
                .filter(Objects::nonNull)
                .toList();
    }

    private ReservaDetalhadaDto mapearParaDto(Reserva reserva, Estacionamento estacionamento, Vaga vaga) {
        DetalhesEstacionamentoDto estacionamentoDto = new DetalhesEstacionamentoDto(
                estacionamento.getId(),
                estacionamento.getNome(),
                estacionamento.getEndereco(),
                estacionamento.getTelefone(),
                estacionamento.getCapacidade(),
                estacionamento.getVagasDisponiveis(),
                estacionamento.getHoraAbertura().toString(),
                estacionamento.getHoraFechamento().toString(),
                null
        );

        VagaDto vagaDto = new VagaDto(
                vaga.getId(),
                vaga.getTipo(),
                vaga.getPreco()
        );

        return new ReservaDetalhadaDto(
                reserva.getId(),
                reserva.getPlacaVeiculo(),
                reserva.getDataHoraInicio(),
                reserva.getDataHoraFim(),
                reserva.getStatus(),
                estacionamentoDto,
                vagaDto
        );
    }

    public Reserva buscarPorId(String id) {
        return reservaRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Reserva não encontrada"));
    }

    @Transactional
    public void cancelarReserva(String id) {
        Reserva reserva = buscarPorId(id);
        String usuarioId = SecurityUtils.getCurrentUsuario().getId();

        if (!reserva.getClienteId().equals(usuarioId)) {
            throw new SecurityException("Reserva não pertence ao usuário logado.");
        }

        if (!reserva.getStatus().equals(StatusReserva.ATIVA)) {
            throw new IllegalStateException("Apenas reservas ativas podem ser canceladas.");
        }

        reserva.setStatus(StatusReserva.CANCELADA);
        reservaRepository.save(reserva);
        log.info("Reserva {} cancelada pelo usuário {}", id, usuarioId);
    }
}
