package br.com.findpark;

import br.com.findpark.entities.Estacionamento;
import br.com.findpark.entities.Vaga;
import br.com.findpark.entities.enums.vagas.StatusVaga;
import br.com.findpark.entities.enums.vagas.TipoVaga;
import br.com.findpark.repositories.EstacionamentoRepository;
import br.com.findpark.repositories.VagaRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalTime;
import java.util.List;

@Configuration
public class MongoInit {

    @Bean
    CommandLineRunner initDatabase(EstacionamentoRepository estacionamentoRepository, VagaRepository vagaRepository) {
        return args -> {
            // Limpa os dados antigos
            vagaRepository.deleteAll();
            estacionamentoRepository.deleteAll();

            // Cria e salva os estacionamentos com horário de funcionamento
            Estacionamento est1 = new Estacionamento(
                    null,
                    "Estacionamento Central",
                    "Rua A, 123",
                    50,
                    50,
                    "proprietario1",
                    LocalTime.of(8, 0),
                    LocalTime.of(23, 0),
                    null
            );

            Estacionamento est2 = new Estacionamento(
                    null,
                    "Estacionamento Shopping",
                    "Av. B, 456",
                    100,
                    100,
                    "proprietario2",
                    LocalTime.of(10, 0),
                    LocalTime.of(22, 0),
                    null
            );// Cria e salva os estacionamentos com horário de funcionamento
            Estacionamento est3 = new Estacionamento(
                    null,
                    "Estacionamento Nutella",
                    "Rua F, 4231",
                    50,
                    50,
                    "Jubileu",
                    LocalTime.of(8, 0),
                    LocalTime.of(23, 0),
                    null
            );

            Estacionamento est4 = new Estacionamento(
                    null,
                    "Estacionamento Ubirajara",
                    "Av. TT, 23",
                    100,
                    100,
                    "Kazim",
                    LocalTime.of(10, 0),
                    LocalTime.of(22, 0),
                    null
            );

            est1 = estacionamentoRepository.save(est1);
            est2 = estacionamentoRepository.save(est2);
            est3 = estacionamentoRepository.save(est3);
            est4 = estacionamentoRepository.save(est4);

            // Cria vagas associadas aos estacionamentos
            Vaga vaga1 = new Vaga(null, StatusVaga.LIVRE, List.of(TipoVaga.COMUM), 5.0, est1.getId());
            Vaga vaga2 = new Vaga(null, StatusVaga.LIVRE, List.of(TipoVaga.DEFICIENTE), 5.0, est2.getId());
            Vaga vaga3 = new Vaga(null, StatusVaga.LIVRE, List.of(TipoVaga.COMUM, TipoVaga.MOTO), 7.5, est3.getId());
            Vaga vaga4 = new Vaga(null, StatusVaga.LIVRE, List.of(TipoVaga.ELETRICO), 7.5, est4.getId());
            Vaga vaga5 = new Vaga(null, StatusVaga.LIVRE, List.of(TipoVaga.MOTO), 5.0, est1.getId());
            Vaga vaga6 = new Vaga(null, StatusVaga.LIVRE, List.of(TipoVaga.DESCOBERTA), 5.0, est2.getId());
            Vaga vaga7 = new Vaga(null, StatusVaga.LIVRE, List.of(TipoVaga.COMUM, TipoVaga.DESCOBERTA), 7.5, est3.getId());
            Vaga vaga8 = new Vaga(null, StatusVaga.LIVRE, List.of(TipoVaga.ELETRICO), 7.5, est4.getId());

            vagaRepository.saveAll(List.of(vaga1, vaga2, vaga3, vaga4, vaga5, vaga6, vaga7, vaga8));
        };
    }
}


