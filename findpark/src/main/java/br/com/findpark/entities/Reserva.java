package br.com.findpark.entities;

import br.com.findpark.dtos.reservas.StatusReserva;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

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

    private String placaVeiculo;
    private LocalDateTime dataHoraInicio;
    private LocalDateTime dataHoraFim;
    private StatusReserva status;

    // 🔥 Snapshot dos dados do estacionamento no momento da reserva
    private String nomeEstacionamento;
    private String enderecoEstacionamento;      // ✅ Texto formatado
    private String telefoneEstacionamento;
    private String horaAberturaEstacionamento;  // ✅ Texto (ex.: "08:00")
    private String horaFechamentoEstacionamento;

    // 🔥 Snapshot dos dados da vaga no momento da reserva
    private List<String> vagaTipo;              // Ex.: ["COMUM", "COBERTA"]
    private Double vagaPreco;
}

