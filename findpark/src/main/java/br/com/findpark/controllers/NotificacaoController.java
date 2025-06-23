package br.com.findpark.controllers;

import br.com.findpark.entities.Notificacao;
import br.com.findpark.security.SecurityUtils;
import br.com.findpark.service.NotificacaoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/notificacoes")
public class NotificacaoController {

    @Autowired
    private NotificacaoService notificacaoService;

    @GetMapping
    public Page<Notificacao> listarMinhasNotificacoes(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "dataHora") String sort,
            @RequestParam(defaultValue = "desc") String direction
    ) {
        Sort.Direction sortDirection = Sort.Direction.fromString(direction);
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sort));

        String usuarioId = SecurityUtils.getCurrentUsuario().getId();
        return notificacaoService.listarDoUsuario(usuarioId, pageable);
    }

    @PostMapping("/{id}/ler")
    public void marcarComoLida(@PathVariable String id) {
        notificacaoService.marcarComoLida(id);
    }

    @PostMapping("/ler-todas")
    public void marcarTodasComoLidas() {
        String usuarioId = SecurityUtils.getCurrentUsuario().getId();
        notificacaoService.marcarTodasComoLidas(usuarioId);
    }

    @GetMapping("/nao-lidas")
    public long contarNaoLidas() {
        String usuarioId = SecurityUtils.getCurrentUsuario().getId();
        return notificacaoService.contarNaoLidas(usuarioId);
    }
}
