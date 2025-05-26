package br.com.findpark.repositories;

import br.com.findpark.entities.Estacionamento;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface EstacionamentoRepository extends MongoRepository<Estacionamento, String> {
    Page<Estacionamento> findAllByIdProprietario(String idProprietario, Pageable pageable);
    Page<Estacionamento> findByIdProprietario(String proprietarioId, Pageable pageable);
}
