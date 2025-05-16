package br.com.findpark.entities;

import br.com.findpark.entities.enums.vagas.StatusVaga;
import br.com.findpark.entities.enums.vagas.TipoVaga;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document(collection = "vagas")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Vaga {

    @Id
    private String id;
    private StatusVaga status;
    private List<TipoVaga> tipo;
    private double preco;
    private String estacionamentoId;
}