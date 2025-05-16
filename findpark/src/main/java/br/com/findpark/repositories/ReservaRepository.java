package br.com.findpark.repositories;

import br.com.findpark.entities.Reserva;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.LocalDateTime;
import java.util.List;


public interface ReservaRepository extends MongoRepository<Reserva, String> {
    List<Reserva> findByClienteId(String clienteId);

    boolean existsByVagaIdAndDataHoraFimAfterAndDataHoraInicioBefore(
            String vagaId,
            LocalDateTime dataHoraInicio,
            LocalDateTime dataHoraFim
    );
}
