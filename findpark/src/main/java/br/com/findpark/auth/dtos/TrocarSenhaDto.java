package br.com.findpark.auth.dtos;

public record TrocarSenhaDto(
        String senhaAtual,
        String novaSenha
) {
}
