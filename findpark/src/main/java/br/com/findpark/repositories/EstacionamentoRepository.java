package br.com.findpark.repositories;

import br.com.findpark.entities.Estacionamento;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface EstacionamentoRepository extends MongoRepository<Estacionamento, String> {
    List<Estacionamento> findAllByIdProprietario(String idProprietario);
}
