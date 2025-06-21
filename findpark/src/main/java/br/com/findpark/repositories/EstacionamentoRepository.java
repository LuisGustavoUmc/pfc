package br.com.findpark.repositories;

import br.com.findpark.entities.Estacionamento;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface EstacionamentoRepository extends MongoRepository<Estacionamento, String> {
    Page<Estacionamento> findAllByIdProprietario(String idProprietario, Pageable pageable);
    List<Estacionamento> findByIdProprietario(String idProprietario);
}
