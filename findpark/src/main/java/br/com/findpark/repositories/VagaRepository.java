package br.com.findpark.repositories;

import br.com.findpark.entities.Vaga;
import br.com.findpark.entities.enums.vagas.StatusVaga;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface VagaRepository extends MongoRepository<Vaga, String> {
    Page<Vaga> findByEstacionamentoId(String estacionamentoId, Pageable pageable);
    Page<Vaga> findByStatus(StatusVaga status, Pageable pageable);
    Page<Vaga> findByEstacionamentoIdAndStatus(String estacionamentoId, StatusVaga status, Pageable pageable);
    long countByEstacionamentoId(String estacionamentoId);
}
