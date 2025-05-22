package br.com.findpark.dtos.estacionamentos;

import br.com.findpark.entities.Endereco;

public record AtualizarEstacionamentoDto(
        String nome,
        Endereco endereco,
        String telefone,
        int capacidade,
        int vagasDisponiveis
) {
}
