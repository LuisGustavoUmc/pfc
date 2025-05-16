package br.com.findpark.repositories;

import br.com.findpark.entities.Usuario;
import br.com.findpark.entities.enums.usuarios.Validade;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface UsuarioRepository extends MongoRepository<Usuario, String> {
    Optional<Usuario> findByEmail(String email);
    List<Usuario> findByValidadeAndCriadoEmBefore(Validade validade, LocalDateTime criadoEmLimite);
    Optional<Usuario> findByTokenConfirmacao(String tokenConfirmacao);
}
