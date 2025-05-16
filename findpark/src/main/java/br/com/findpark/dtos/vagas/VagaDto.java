package br.com.findpark.dtos.vagas;

import br.com.findpark.entities.enums.vagas.TipoVaga;

import java.util.List;

public record VagaDto(
        String id,
        List<TipoVaga> tipo,
        double preco
) {
}
