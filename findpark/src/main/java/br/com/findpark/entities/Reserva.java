package br.com.findpark.entities;

import br.com.findpark.dtos.reservas.StatusReserva;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "reservas")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Reserva {
    @Id
    private String id;
    private String clienteId;
    private String estacionamentoId;
    private String vagaId;

    @Indexed(unique = true)
    private String placaVeiculo;
    private LocalDateTime dataHoraInicio;
    private LocalDateTime dataHoraFim;
    private StatusReserva status;
}


