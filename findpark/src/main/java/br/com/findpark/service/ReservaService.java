package br.com.findpark.service;

import br.com.findpark.dtos.estacionamentos.DetalhesEstacionamentoDto;
import br.com.findpark.dtos.reservas.ReservaDetalhadaDto;
import br.com.findpark.dtos.reservas.StatusReserva;
import br.com.findpark.dtos.vagas.VagaDto;
import br.com.findpark.entities.*;
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
        return estacionamentoRepository.findById(estacionamentoId).orElse(null);
    }

    // Busca vaga pelo ID, lançando exceção caso não encontrada.
    private Vaga buscarVaga(String vagaId) {
        return vagaRepository.findById(vagaId).orElse(null);
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

        List<Reserva> reservas = (status != null)
                ? reservaRepository.findByClienteIdAndStatus(clienteId, status, Pageable.unpaged()).getContent()
                : reservaRepository.findByClienteId(clienteId, Pageable.unpaged()).getContent();

        atualizarStatusReservasExpiradas(reservas);

        List<ReservaDetalhadaDto> dtos = reservas.stream()
                .map(reserva -> {
                    Estacionamento est = buscarEstacionamento(reserva.getEstacionamentoId());
                    Vaga vaga = buscarVaga(reserva.getVagaId());
                    return mapearParaDto(reserva, est, vaga);
                })
                .toList();

        int start = (int) pageable.getOffset();
        int end = Math.min(start + pageable.getPageSize(), dtos.size());
        List<ReservaDetalhadaDto> pageContent = dtos.subList(start, end);

        return new PageImpl<>(pageContent, pageable, dtos.size());
    }

    private ReservaDetalhadaDto mapearParaDto(Reserva reserva, Estacionamento estacionamento, Vaga vaga) {
        // Endereço
        Endereco endereco = (estacionamento != null)
                ? estacionamento.getEndereco()
                : new Endereco(
                "", "Endereço não disponível", "", "", "", "",
                "", "", "", "", "", "", "", "", ""
        );

        // DTO de estacionamento
        DetalhesEstacionamentoDto estacionamentoDto = (estacionamento != null)
                ? new DetalhesEstacionamentoDto(
                estacionamento.getId(),
                estacionamento.getNome(),
                endereco,
                estacionamento.getTelefone(),
                estacionamento.getCapacidade(),
                estacionamento.getVagasDisponiveis(),
                estacionamento.getHoraAbertura() != null ? estacionamento.getHoraAbertura().toString() : "N/A",
                estacionamento.getHoraFechamento() != null ? estacionamento.getHoraFechamento().toString() : "N/A",
                null
        )
                : new DetalhesEstacionamentoDto(
                reserva.getEstacionamentoId(),
                "[Estacionamento Removido]",
                endereco,
                "Telefone não disponível",
                0,
                0,
                "N/A",
                "N/A",
                null
        );

        // DTO de vaga
        VagaDto vagaDto = (vaga != null)
                ? new VagaDto(
                vaga.getId(),
                vaga.getTipo(),
                vaga.getPreco()
        )
                : new VagaDto(
                reserva.getVagaId(),
                List.of(), // Lista vazia ou pode criar um tipo específico "REMOVIDA"
                0.0
        );

        // Cliente
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
                .findAllByIdProprietario(proprietarioId, Pageable.unpaged())
                .getContent();

        if (estacionamentos.isEmpty()) {
            throw new RecursoNaoEncontradoException("Nenhum estacionamento cadastrado");
        }

        List<String> estacionamentoIds = estacionamentos.stream()
                .map(Estacionamento::getId)
                .toList();

        List<Reserva> reservas = (status != null)
                ? reservaRepository.findByEstacionamentoIdInAndStatus(estacionamentoIds, status, Pageable.unpaged()).getContent()
                : reservaRepository.findByEstacionamentoIdIn(estacionamentoIds, Pageable.unpaged()).getContent();

        List<ReservaDetalhadaDto> todasFiltradas = reservas.stream()
                .map(reserva -> {
                    Estacionamento est = estacionamentos.stream()
                            .filter(e -> e.getId().equals(reserva.getEstacionamentoId()))
                            .findFirst().orElse(null);
                    try {
                        Vaga vaga = buscarVaga(reserva.getVagaId());
                        return (est != null && vaga != null)
                                ? mapearParaDto(reserva, est, vaga)
                                : null;
                    } catch (Exception e) {
                        log.warn("Dados incompletos para reserva {}: {}", reserva.getId(), e.getMessage());
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .filter(dto -> placaVeiculo == null || dto.placaVeiculo().equalsIgnoreCase(placaVeiculo))
                .toList();

        // Paginação manual após filtro
        int start = (int) pageable.getOffset();
        int end = Math.min(start + pageable.getPageSize(), todasFiltradas.size());
        List<ReservaDetalhadaDto> pageContent = todasFiltradas.subList(start, end);

        return new PageImpl<>(pageContent, pageable, todasFiltradas.size());
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

    @Transactional
    public void cancelarReservasDoCliente(String clienteId) {
        List<Reserva> reservas = reservaRepository.findAllByClienteId(clienteId);
        for (Reserva reserva : reservas) {
            if (reserva.getStatus() == StatusReserva.ATIVA) {
                reserva.setStatus(StatusReserva.CANCELADA);
            }
        }
        reservaRepository.saveAll(reservas);
        log.info("Reservas do cliente {} foram canceladas", clienteId);
    }

}
