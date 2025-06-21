package br.com.findpark.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "logs_exclusoes")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LogExclusao {
    @Id
    private String id;

    private String entidade;
    private String entidadeId;
    private String usuarioResponsavelId;
    private String descricao;

    private LocalDateTime dataHora;
}
