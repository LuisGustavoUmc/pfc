package br.com.findpark.dtos.usuarios;

public record AtualizarUsuarioDto(
        String nome,
        String email,
        String telefone
) {
}
