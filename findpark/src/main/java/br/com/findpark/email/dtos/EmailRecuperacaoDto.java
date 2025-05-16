package br.com.findpark.email.dtos;

import br.com.findpark.entities.Usuario;

public record EmailRecuperacaoDto(
        Usuario usuario,
        String assunto,
        String token
) {
}
