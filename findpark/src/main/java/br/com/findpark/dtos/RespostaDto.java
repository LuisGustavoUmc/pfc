package br.com.findpark.dtos;

import org.springframework.http.HttpStatus;

import java.util.Optional;

public record RespostaDto(
        HttpStatus status,
        String mensagem,
        boolean sucesso,
        Optional<String> nome
) {
}
