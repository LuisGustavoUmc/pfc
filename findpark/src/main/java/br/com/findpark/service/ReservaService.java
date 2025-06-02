package br.com.findpark.service;

import br.com.findpark.dtos.estacionamentos.DetalhesEstacionamentoDto;
import br.com.findpark.dtos.reservas.ReservaDetalhadaDto;
import br.com.findpark.dtos.reservas.StatusReserva;
import br.com.findpark.dtos.vagas.VagaDto;
import br.com.findpark.entities.Estacionamento;
import br.com.findpark.entities.Reserva;
import br.com.findpark.entities.Usuario;
import br.com.findpark.entities.Vaga;
import br.com.findpark.exceptions.reserva.ReservaConflitanteException;
import br.com.findpark.exceptions.usuario.RecursoNaoEncontradoException;
import br.com.findpark.repositories.EstacionamentoRepository;
import br.com.findpark.repositories.ReservaRepository;
import br.com.findpark.repositories.UsuarioRepository;
import br.com.findpark.repositories.VagaRepository;
import br.com.findpark.security.SecurityUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ReservaService {

    @Autowired
    private ReservaRepository reservaRepository;

    @Autowired
    private VagaRepository vagaRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private EstacionamentoRepository estacionamentoRepository;

    // Cria uma nova reserva após validação de horários, vaga, estacionamento e conflitos.
    public Reserva criarReserva(Reserva reserva) {
        log.info("Tentando criar reserva: início={} fim={} vaga={} estacionamento={}",
                reserva.getDataHoraInicio(),
                reserva.getDataHoraFim(),
                reserva.getVagaId(),
                reserva.getEstacionamentoId());

        reserva.setClienteId(SecurityUtils.getCurrentUsuario().getId());
        reserva.setStatus(StatusReserva.ATIVA);

        // Buscar o estacionamento e a vaga ANTES de validar
        Estacionamento estacionamento = buscarEstacionamento(reserva.getEstacionamentoId());
        Vaga vaga = buscarVaga(reserva.getVagaId());

        validarReserva(estacionamento, reserva);
        validarVagaPertenceAoEstacionamento(vaga, estacionamento);

        boolean vagaOcupada = reservaRepository.existsByVagaIdAndStatusAndDataHoraFimAfterAndDataHoraInicioBefore(
                reserva.getVagaId(),
                StatusReserva.ATIVA,
                reserva.getDataHoraInicio(),
                reserva.getDataHoraFim()
        );

        if (vagaOcupada) {
            throw new ReservaConflitanteException("Já existe uma reserva para essa vaga no período selecionado.");
        }

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

    // Valida os dados da reserva e regras de horários conforme funcionamento do estacionamento.
    private void validarReserva(Estacionamento estacionamento, Reserva reserva) {
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

        LocalTime abertura = estacionamento.getHoraAbertura();
        LocalTime fechamento = estacionamento.getHoraFechamento();

        if (abertura == null || fechamento == null) {
            throw new IllegalArgumentException("Horário de funcionamento do estacionamento não está configurado.");
        }

        boolean is24Horas = abertura.equals(LocalTime.MIDNIGHT) &&
                (fechamento.equals(LocalTime.MIDNIGHT) || fechamento.equals(LocalTime.of(23, 59)));

        LocalTime horaInicio = reserva.getDataHoraInicio().toLocalTime();
        LocalTime horaFim = reserva.getDataHoraFim().toLocalTime();

        if (!is24Horas) {
            if (horaInicio.isBefore(abertura) || horaInicio.isAfter(fechamento)) {
                throw new IllegalArgumentException("O horário de início está fora do horário de funcionamento do estacionamento.");
            }

            if (horaFim.isBefore(abertura) || horaFim.isAfter(fechamento)) {
                throw new IllegalArgumentException("O horário de fim está fora do horário de funcionamento do estacionamento.");
            }

            if (!reserva.getDataHoraInicio().toLocalDate().equals(reserva.getDataHoraFim().toLocalDate())) {
                throw new IllegalArgumentException("Reservas não podem ultrapassar o fechamento diário do estacionamento.");
            }
        }
    }

    // Busca estacionamento pelo ID, lançando exceção caso não encontrado.
    private Estacionamento buscarEstacionamento(String estacionamentoId) {
        return estacionamentoRepository.findById(estacionamentoId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Estacionamento não encontrado"));
    }

    // Busca vaga pelo ID, lançando exceção caso não encontrada.
    private Vaga buscarVaga(String vagaId) {
        return vagaRepository.findById(vagaId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Vaga não encontrado"));
    }

    // Valida se a vaga pertence ao estacionamento informado.
    private void validarVagaPertenceAoEstacionamento(Vaga vaga, Estacionamento estacionamento) {
        if (!vaga.getEstacionamentoId().equals(estacionamento.getId())) {
            throw new IllegalArgumentException("A vaga não pertence ao estacionamento informado.");
        }
    }

    // Atualiza o status para FINALIZADA das reservas ativas que já passaram do horário final.
    private void atualizarStatusReservasExpiradas(List<Reserva> reservas) {
        LocalDateTime agora = LocalDateTime.now();

        reservas.stream()
                .filter(r -> r.getStatus() == StatusReserva.ATIVA && r.getDataHoraFim().isBefore(agora))
                .forEach(reserva -> {
                    reserva.setStatus(StatusReserva.FINALIZADA);
                    reservaRepository.save(reserva);
                });
    }

    // Lista as reservas do cliente logado, podendo filtrar por status.
    public Page<ReservaDetalhadaDto> listarMinhasReservas(Pageable pageable, StatusReserva status) {
        String clienteId = SecurityUtils.getCurrentUsuario().getId();

        Page<Reserva> reservasPage;
        if (status != null) {
            reservasPage = reservaRepository.findByClienteIdAndStatus(clienteId, status, pageable);
        } else {
            reservasPage = reservaRepository.findByClienteId(clienteId, pageable);
        }

        atualizarStatusReservasExpiradas(reservasPage.getContent());

        List<ReservaDetalhadaDto> dtos = reservasPage.stream()
                .map(reserva -> {
                    try {
                        Estacionamento estacionamento = buscarEstacionamento(reserva.getEstacionamentoId());
                        Vaga vaga = buscarVaga(reserva.getVagaId());

                        if (estacionamento != null && vaga != null) {
                            return mapearParaDto(reserva, estacionamento, vaga);
                        }
                    } catch (Exception e) {
                        log.warn("Dados incompletos para reserva {}, ignorada.", reserva.getId());
                    }
                    return null;
                })
                .filter(Objects::nonNull)
                .toList();

        if (dtos.isEmpty() && reservasPage.hasNext()) {
            Pageable proximaPagina = pageable.next();
            return listarMinhasReservas(proximaPagina, status);
        }

        return new PageImpl<>(dtos, pageable, reservasPage.getTotalElements());
    }

    // Mapeia as entidades Reserva, Estacionamento e Vaga para o DTO detalhado usado na API.
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

        Usuario cliente = usuarioRepository.findById(reserva.getClienteId())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Cliente não encontrado para a reserva"));

        return new ReservaDetalhadaDto(
                reserva.getId(),
                reserva.getPlacaVeiculo(),
                reserva.getDataHoraInicio(),
                reserva.getDataHoraFim(),
                reserva.getStatus(),
                estacionamentoDto,
                vagaDto,
                cliente.getNome()
        );
    }

    // Lista as reservas dos estacionamentos do proprietário logado, com filtro opcional por status e placa.
    public Page<ReservaDetalhadaDto> listarReservasDosMeusEstacionamentos(Pageable pageable, StatusReserva status, String placaVeiculo) {
        String proprietarioId = SecurityUtils.getCurrentUsuario().getId();

        List<Estacionamento> estacionamentos = estacionamentoRepository
                .findAllByIdProprietario(proprietarioId, Pageable.unpaged())  // OU crie um método que retorne List<>
                .getContent();

        if (estacionamentos.isEmpty()) {
            throw new RecursoNaoEncontradoException("Nenhum estacionamento cadastrado");
        }

        List<String> estacionamentoIds = estacionamentos.stream()
                .map(Estacionamento::getId)
                .toList();

        Page<Reserva> reservasPage;

        if (status != null) {
            reservasPage = reservaRepository.findByEstacionamentoIdInAndStatus(estacionamentoIds, status, pageable);
        } else {
            reservasPage = reservaRepository.findByEstacionamentoIdIn(estacionamentoIds, pageable);
        }

        if (reservasPage.isEmpty()) {
            throw new RecursoNaoEncontradoException("Reserva não encontrada");
        }

        List<ReservaDetalhadaDto> reservasDetalhadasList = reservasPage.stream()
                .map(reserva -> {
                    Estacionamento estacionamento = estacionamentos.stream()
                            .filter(e -> e.getId().equals(reserva.getEstacionamentoId()))
                            .findFirst()
                            .orElse(null);

                    Vaga vaga = null;
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

        if (placaVeiculo != null && !placaVeiculo.isBlank()) {
            reservasDetalhadasList = reservasDetalhadasList.stream()
                    .filter(dto -> dto.placaVeiculo().equalsIgnoreCase(placaVeiculo))
                    .toList();
        }

        return new PageImpl<>(reservasDetalhadasList, pageable, reservasPage.getTotalElements());
    }

    // Busca uma reserva pelo ID, lança exceção se não encontrada.
    public Reserva buscarPorId(String id) {
        return reservaRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Reserva não encontrada"));
    }

    // Cancela a reserva pelo ID, garantindo que o usuário é o dono e a reserva está ativa.
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
