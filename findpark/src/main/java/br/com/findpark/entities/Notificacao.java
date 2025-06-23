package br.com.findpark.entities;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "notificacoes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Notificacao {

    @Id
    private String id;

    private String usuarioId;
    private String mensagem;
    private boolean lida;
    private LocalDateTime dataHora;
}

