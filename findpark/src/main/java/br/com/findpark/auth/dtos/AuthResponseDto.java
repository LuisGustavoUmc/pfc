package br.com.findpark.auth.dtos;

public record AuthResponseDto(
        String nome,
        String email,
        String accessToken,
        String role
) {
}

