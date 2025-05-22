package br.com.findpark.dtos.estacionamentos;

import br.com.findpark.dtos.vagas.VagaDto;
import br.com.findpark.entities.Endereco;

import java.util.List;

public record DetalhesEstacionamentoDto(
        String id,
        String nome,
        Endereco endereco,
        String telefone,
        int capacidade,
        int vagasDisponiveis,
        String horaAbertura,
        String horaFechamento,
        List<VagaDto> vagas
) {
}
