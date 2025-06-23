package br.com.findpark.repositories;

import br.com.findpark.entities.Notificacao;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface NotificacaoRepository extends MongoRepository<Notificacao, String> {

    List<Notificacao> findByUsuarioIdOrderByDataHoraDesc(String usuarioId);

    Page<Notificacao> findByUsuarioId(String usuarioId, Pageable pageable);

    long countByUsuarioIdAndLidaFalse(String usuarioId);
}
