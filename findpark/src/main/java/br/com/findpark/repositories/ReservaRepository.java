package br.com.findpark.repositories;

import br.com.findpark.dtos.reservas.StatusReserva;
import br.com.findpark.entities.Reserva;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.LocalDateTime;
import java.util.List;


public interface ReservaRepository extends MongoRepository<Reserva, String> {
    Page<Reserva> findByClienteIdAndStatus(String clienteId, StatusReserva status, Pageable pageable);
    Page<Reserva> findByClienteId(String clienteId, Pageable pageable);
    List<Reserva> findAllByClienteId(String clienteId);
    boolean existsByEstacionamentoIdAndStatus(String estacionamentoId, StatusReserva status);

    boolean existsByVagaIdAndStatusAndDataHoraFimAfterAndDataHoraInicioBefore(
            String vagaId,
            StatusReserva status,
            LocalDateTime dataHoraInicio,
            LocalDateTime dataHoraFim
    );

    boolean existsByPlacaVeiculoAndStatusAndDataHoraFimAfterAndDataHoraInicioBefore(
            String placaVeiculo,
            StatusReserva status,
            LocalDateTime dataHoraInicio,
            LocalDateTime dataHoraFim
    );

    Page<Reserva> findByEstacionamentoIdIn(List<String> estacionamentoIds, Pageable pageable);

    Page<Reserva> findByEstacionamentoIdInAndStatus(List<String> estacionamentoIds, StatusReserva status, Pageable pageable);
}
