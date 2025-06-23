package br.com.findpark.service;


import br.com.findpark.entities.Notificacao;
import br.com.findpark.repositories.NotificacaoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class NotificacaoService {

    @Autowired
    private NotificacaoRepository notificacaoRepository;

    public void enviar(String usuarioId, String mensagem) {
        Notificacao notificacao = Notificacao.builder()
                .usuarioId(usuarioId)
                .mensagem(mensagem)
                .lida(false)
                .dataHora(LocalDateTime.now())
                .build();
        notificacaoRepository.save(notificacao);
    }

    public Page<Notificacao> listarDoUsuario(String usuarioId, Pageable pageable) {
        return notificacaoRepository.findByUsuarioId(usuarioId, pageable);
    }

    public void marcarComoLida(String notificacaoId) {
        notificacaoRepository.findById(notificacaoId).ifPresent(n -> {
            n.setLida(true);
            notificacaoRepository.save(n);
        });
    }

    public void marcarTodasComoLidas(String usuarioId) {
        List<Notificacao> notificacoes = notificacaoRepository.findByUsuarioIdOrderByDataHoraDesc(usuarioId);
        notificacoes.forEach(n -> n.setLida(true));
        notificacaoRepository.saveAll(notificacoes);
    }

    public long contarNaoLidas(String usuarioId) {
        return notificacaoRepository.countByUsuarioIdAndLidaFalse(usuarioId);
    }
}

