package br.com.findpark.services;

import br.com.findpark.dtos.reservas.ReservaDetalhadaDto;
import br.com.findpark.dtos.reservas.StatusReserva;
import br.com.findpark.entities.*;
import br.com.findpark.entities.enums.vagas.TipoVaga;
import br.com.findpark.exceptions.reserva.ReservaConflitanteException;
import br.com.findpark.repositories.EstacionamentoRepository;
import br.com.findpark.repositories.ReservaRepository;
import br.com.findpark.repositories.UsuarioRepository;
import br.com.findpark.repositories.VagaRepository;
import br.com.findpark.security.SecurityUtils;
import br.com.findpark.service.ReservaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReservaServiceTest {

    @InjectMocks
    private ReservaService reservaService;

    @Mock
    private ReservaRepository reservaRepository;

    @Mock
    private VagaRepository vagaRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private EstacionamentoRepository estacionamentoRepository;

    private Usuario usuarioFake;

    @BeforeEach
    void setUp() {
        usuarioFake = new Usuario();
        usuarioFake.setId("user-1");
    }

    @Test
    void testCriarReservaComSucesso() {
        try (MockedStatic<SecurityUtils> securityUtilsMockedStatic = mockStatic(SecurityUtils.class)) {
            securityUtilsMockedStatic.when(SecurityUtils::getCurrentUsuario).thenReturn(usuarioFake);

            Reserva reserva = criarReservaBase();

            Estacionamento estacionamento = criarEstacionamentoBase();
            Vaga vaga = criarVagaBase();

            when(estacionamentoRepository.findById("est-1")).thenReturn(Optional.of(estacionamento));
            when(vagaRepository.findById("vaga-1")).thenReturn(Optional.of(vaga));
            when(reservaRepository.existsByVagaIdAndStatusAndDataHoraFimAfterAndDataHoraInicioBefore(any(), any(), any(), any())).thenReturn(false);
            when(reservaRepository.existsByPlacaVeiculoAndStatusAndDataHoraFimAfterAndDataHoraInicioBefore(any(), any(), any(), any())).thenReturn(false);
            when(reservaRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

            Reserva resultado = reservaService.criarReserva(reserva);

            assertEquals(StatusReserva.ATIVA, resultado.getStatus());
            assertEquals("user-1", resultado.getClienteId());
        }
    }

    @Test
    void testCriarReservaForaHorarioFuncionamento() {
        try (MockedStatic<SecurityUtils> securityUtilsMockedStatic = mockStatic(SecurityUtils.class)) {
            securityUtilsMockedStatic.when(SecurityUtils::getCurrentUsuario).thenReturn(usuarioFake);

            Reserva reserva = criarReservaBase();
            reserva.setDataHoraInicio(LocalDateTime.of(2025, 6, 2, 5, 0));
            reserva.setDataHoraFim(LocalDateTime.of(2025, 6, 2, 6, 0));

            Estacionamento estacionamento = criarEstacionamentoBase();
            Vaga vaga = criarVagaBase();

            when(estacionamentoRepository.findById("est-1")).thenReturn(Optional.of(estacionamento));
            when(vagaRepository.findById("vaga-1")).thenReturn(Optional.of(vaga));

            assertThrows(IllegalArgumentException.class, () -> reservaService.criarReserva(reserva));
        }
    }


    @Test
    void testCriarReservaComConflitoDeVaga() {
        try (MockedStatic<SecurityUtils> securityUtilsMockedStatic = mockStatic(SecurityUtils.class)) {
            securityUtilsMockedStatic.when(SecurityUtils::getCurrentUsuario).thenReturn(usuarioFake);

            Reserva reserva = criarReservaBase();

            Estacionamento estacionamento = criarEstacionamentoBase();
            Vaga vaga = criarVagaBase();

            when(estacionamentoRepository.findById("est-1")).thenReturn(Optional.of(estacionamento));
            when(vagaRepository.findById("vaga-1")).thenReturn(Optional.of(vaga));
            when(reservaRepository.existsByVagaIdAndStatusAndDataHoraFimAfterAndDataHoraInicioBefore(any(), any(), any(), any())).thenReturn(true);

            assertThrows(ReservaConflitanteException.class, () -> reservaService.criarReserva(reserva));
        }
    }

    // --- Métodos utilitários para criação de mocks base ---

    private Reserva criarReservaBase() {
        Reserva reserva = new Reserva();
        reserva.setEstacionamentoId("est-1");
        reserva.setVagaId("vaga-1");
        reserva.setPlacaVeiculo("ABC1234");
        reserva.setDataHoraInicio(LocalDateTime.now().plusHours(1));
        reserva.setDataHoraFim(LocalDateTime.now().plusHours(2));
        return reserva;
    }

    private Estacionamento criarEstacionamentoBase() {
        Estacionamento estacionamento = new Estacionamento();
        estacionamento.setId("est-1");
        estacionamento.setNome("Estacionamento Central");
        estacionamento.setEndereco(new Endereco());
        estacionamento.setTelefone("123456789");
        estacionamento.setHoraAbertura(LocalTime.of(8, 0));
        estacionamento.setHoraFechamento(LocalTime.of(22, 0));
        estacionamento.setCapacidade(10);
        estacionamento.setVagasDisponiveis(5);
        return estacionamento;
    }

    private Vaga criarVagaBase() {
        Vaga vaga = new Vaga();
        vaga.setId("vaga-1");
        vaga.setEstacionamentoId("est-1");
        vaga.setPreco(10.0);
        vaga.setTipo(List.of(TipoVaga.COBERTA));
        return vaga;
    }
}
