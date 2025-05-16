package br.com.findpark.dtos.estacionamentos;

import br.com.findpark.dtos.vagas.VagaDto;

import java.util.List;

public record DetalhesEstacionamentoDto(
        String id,
        String nome,
        String endereco,
        int capacidade,
        int vagasDisponiveis,
        String horaAbertura,
        String horaFechamento,
        List<VagaDto> vagas
) {
}
