package br.com.findpark.repositories;

import br.com.findpark.entities.Estacionamento;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface EstacionamentoRepository extends MongoRepository<Estacionamento, String> {
    List<Estacionamento> findAllByIdProprietario(String idProprietario);
    List<Estacionamento> findByEndereco_LocalidadeIgnoreCase(String cidade);
    List<Estacionamento> findByEndereco_BairroIgnoreCase(String bairro);
    List<Estacionamento> findByEndereco_UfIgnoreCase(String estado);
    List<Estacionamento> findByEndereco_LocalidadeIgnoreCaseAndEndereco_BairroIgnoreCase(String cidade, String bairro);

}
