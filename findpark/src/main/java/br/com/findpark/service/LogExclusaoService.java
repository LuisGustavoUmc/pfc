package br.com.findpark.service;

import br.com.findpark.entities.LogExclusao;
import br.com.findpark.repositories.LogExclusaoRepository;
import br.com.findpark.security.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class LogExclusaoService {

    @Autowired
    private LogExclusaoRepository logExclusaoRepository;

    /**
     * Registra um log de exclusão.
     *
     * @param entidade Nome da entidade (ex.: "Estacionamento", "Vaga", "Usuario", "Reserva")
     * @param entidadeId ID da entidade que foi deletada
     * @param descricao Descrição opcional sobre a exclusão
     */
    public void registrar(String entidade, String entidadeId, String descricao) {
        String usuarioId = SecurityUtils.getCurrentUsuario().getEmail();

        LogExclusao log = LogExclusao.builder()
                .entidade(entidade)
                .entidadeId(entidadeId)
                .usuarioResponsavelId(usuarioId)
                .descricao(descricao)
                .dataHora(LocalDateTime.now())
                .build();

        logExclusaoRepository.save(log);
    }

    public Page<LogExclusao> listarLogs(Pageable pageable) {
        return logExclusaoRepository.findAll(pageable);
    }
}
