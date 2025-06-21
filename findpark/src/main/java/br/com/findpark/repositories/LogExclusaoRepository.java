package br.com.findpark.repositories;

import br.com.findpark.entities.LogExclusao;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LogExclusaoRepository extends MongoRepository<LogExclusao, String> {
    Page<LogExclusao> findAll(Pageable pageable);
}
