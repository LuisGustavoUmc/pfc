//package br.com.findpark;
//
//import br.com.findpark.entities.Estacionamento;
//import br.com.findpark.entities.Endereco;
//import br.com.findpark.entities.Vaga;
//import br.com.findpark.entities.enums.vagas.StatusVaga;
//import br.com.findpark.entities.enums.vagas.TipoVaga;
//import br.com.findpark.repositories.EstacionamentoRepository;
//import br.com.findpark.repositories.VagaRepository;
//import org.springframework.boot.CommandLineRunner;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
//import java.time.LocalTime;
//import java.util.List;
//
//@Configuration
//public class MongoInit {
//
//    @Bean
//    CommandLineRunner initDatabase(EstacionamentoRepository estacionamentoRepository, VagaRepository vagaRepository) {
//        return args -> {
//            vagaRepository.deleteAll();
//            estacionamentoRepository.deleteAll();
//
//            // Criação dos endereços
//            Endereco endereco1 = new Endereco(
//                    "01234-567",
//                    "Rua A",
//                    "Complemento 1",
//                    null,
//                    "Bairro Central",
//                    "São Paulo",
//                    "SP",
//                    "São Paulo",
//                    "Sudeste",
//                    "3550308",
//                    "1234",
//                    "11",
//                    "6789"
//            );
//
//            Endereco endereco2 = new Endereco(
//                    "02345-678",
//                    "Av. B",
//                    "Loja 2",
//                    null,
//                    "Bairro Shopping",
//                    "São Paulo",
//                    "SP",
//                    "São Paulo",
//                    "Sudeste",
//                    "3550308",
//                    "5678",
//                    "11",
//                    "1234"
//            );
//
//            Endereco endereco3 = new Endereco(
//                    "03456-789",
//                    "Rua F",
//                    "Fundos",
//                    null,
//                    "Bairro Nutella",
//                    "São Paulo",
//                    "SP",
//                    "São Paulo",
//                    "Sudeste",
//                    "3550308",
//                    "9101",
//                    "11",
//                    "4321"
//            );
//
//            Endereco endereco4 = new Endereco(
//                    "04567-890",
//                    "Av. TT",
//                    "Sala 10",
//                    null,
//                    "Bairro Ubirajara",
//                    "São Paulo",
//                    "SP",
//                    "São Paulo",
//                    "Sudeste",
//                    "3550308",
//                    "1112",
//                    "11",
//                    "8765"
//            );
//
//            // Cria e salva os estacionamentos usando Endereco
//            Estacionamento est1 = new Estacionamento(
//                    null,
//                    "Estacionamento Central",
//                    "(11)987450462",
//                    50,
//                    50,
//                    "proprietario1",
//                    LocalTime.of(8, 0),
//                    LocalTime.of(23, 0),
//                    endereco1
//            );
//
//            Estacionamento est2 = new Estacionamento(
//                    null,
//                    "Estacionamento Shopping",
//                    "(11)965487023",
//                    100,
//                    100,
//                    "proprietario2",
//                    LocalTime.of(10, 0),
//                    LocalTime.of(22, 0),
//                    endereco2
//            );
//
//            Estacionamento est3 = new Estacionamento(
//                    null,
//                    "Estacionamento Nutella",
//                    "(11)987204123",
//                    50,
//                    50,
//                    "Jubileu",
//                    LocalTime.of(8, 0),
//                    LocalTime.of(23, 0),
//                    endereco3
//            );
//
//            Estacionamento est4 = new Estacionamento(
//                    null,
//                    "Estacionamento Ubirajara",
//                    "(11)952545687",
//                    100,
//                    100,
//                    "Kazim",
//                    LocalTime.of(10, 0),
//                    LocalTime.of(22, 0),
//                    endereco4
//            );
//
//            est1 = estacionamentoRepository.save(est1);
//            est2 = estacionamentoRepository.save(est2);
//            est3 = estacionamentoRepository.save(est3);
//            est4 = estacionamentoRepository.save(est4);
//
//            // Cria vagas associadas aos estacionamentos
//            Vaga vaga1 = new Vaga(null, StatusVaga.LIVRE, List.of(TipoVaga.COMUM), 5.0, est1.getId());
//            Vaga vaga2 = new Vaga(null, StatusVaga.LIVRE, List.of(TipoVaga.DEFICIENTE), 5.0, est2.getId());
//            Vaga vaga3 = new Vaga(null, StatusVaga.LIVRE, List.of(TipoVaga.COMUM, TipoVaga.MOTO), 7.5, est3.getId());
//            Vaga vaga4 = new Vaga(null, StatusVaga.LIVRE, List.of(TipoVaga.ELETRICO), 7.5, est4.getId());
//            Vaga vaga5 = new Vaga(null, StatusVaga.LIVRE, List.of(TipoVaga.MOTO), 5.0, est1.getId());
//            Vaga vaga6 = new Vaga(null, StatusVaga.LIVRE, List.of(TipoVaga.DESCOBERTA), 5.0, est2.getId());
//            Vaga vaga7 = new Vaga(null, StatusVaga.LIVRE, List.of(TipoVaga.COMUM, TipoVaga.DESCOBERTA), 7.5, est3.getId());
//            Vaga vaga8 = new Vaga(null, StatusVaga.LIVRE, List.of(TipoVaga.ELETRICO), 7.5, est4.getId());
//
//            vagaRepository.saveAll(List.of(vaga1, vaga2, vaga3, vaga4, vaga5, vaga6, vaga7, vaga8));
//        };
//    }
//}
