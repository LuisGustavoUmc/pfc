package br.com.findpark.dtos.estacionamentos;

import br.com.findpark.entities.Estacionamento;
import br.com.findpark.entities.enums.vagas.StatusVaga;
import br.com.findpark.entities.enums.vagas.TipoVaga;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VagaComEstacionamentoDto {
    private String id;
    private StatusVaga status;
    private List<TipoVaga> tipo;
    private double preco;
    private Estacionamento estacionamento;
}
