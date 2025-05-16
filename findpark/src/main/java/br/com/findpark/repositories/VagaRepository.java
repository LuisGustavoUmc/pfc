package br.com.findpark.repositories;

import br.com.findpark.entities.Vaga;
import br.com.findpark.entities.enums.vagas.StatusVaga;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface VagaRepository extends MongoRepository<Vaga, String> {
    List<Vaga> findByEstacionamentoId(String estacionamentoId);
    List<Vaga> findByStatus(StatusVaga status);
    List<Vaga> findByEstacionamentoIdAndStatus(String estacionamentoId, StatusVaga status);
}
