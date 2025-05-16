package br.com.findpark.repositories;

import br.com.findpark.entities.Proprietario;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface ProprietarioRepository extends MongoRepository<Proprietario, String> {
    Optional<Proprietario> findByEmail(String email);
}
