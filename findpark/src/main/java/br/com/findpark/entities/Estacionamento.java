package br.com.findpark.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Document(collection = "estacionamentos")
@Data
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Estacionamento {

    @Id
    private String id;
    private String nome;
    private Endereco endereco;
    private String telefone;

    @Min(1)
    private int capacidade;
    private int vagasDisponiveis;
    private String idProprietario;

    private LocalTime horaAbertura;
    private LocalTime horaFechamento;
}

