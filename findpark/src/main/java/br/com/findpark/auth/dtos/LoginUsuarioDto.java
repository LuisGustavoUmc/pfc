package br.com.findpark.auth.dtos;

public record LoginUsuarioDto(
        String email,
        String senha
) {
}
