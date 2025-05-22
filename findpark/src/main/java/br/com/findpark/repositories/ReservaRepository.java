package br.com.findpark.repositories;

import br.com.findpark.dtos.reservas.StatusReserva;
import br.com.findpark.entities.Reserva;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.LocalDateTime;
import java.util.List;


public interface ReservaRepository extends MongoRepository<Reserva, String> {
    List<Reserva> findByClienteId(String clienteId);

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
}
