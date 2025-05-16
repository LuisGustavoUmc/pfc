package br.com.findpark.dtos.estacionamentos;

import br.com.findpark.entities.Vaga;

import java.util.List;

public record AtualizarEstacionamentoDto(
        String nome,
        String endereco,
        int capacidade,
        int vagasDisponiveis
) {
}
