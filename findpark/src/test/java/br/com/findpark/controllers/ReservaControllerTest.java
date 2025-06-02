package br.com.findpark.controllers;

import br.com.findpark.dtos.RespostaDto;
import br.com.findpark.dtos.estacionamentos.DetalhesEstacionamentoDto;
import br.com.findpark.dtos.reservas.ReservaDetalhadaDto;
import br.com.findpark.dtos.reservas.StatusReserva;
import br.com.findpark.dtos.vagas.VagaDto;
import br.com.findpark.entities.Reserva;
import br.com.findpark.service.ReservaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ReservaControllerTest {

    @Mock
    private ReservaService reservaService;

    @InjectMocks
    private ReservaController reservaController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void criarReserva_deveChamarServiceERetornarReserva() {
        Reserva novaReserva = new Reserva();
        novaReserva.setId("res-1");

        when(reservaService.criarReserva(any(Reserva.class))).thenReturn(novaReserva);

        Reserva resultado = reservaController.criarReserva(new Reserva());

        assertNotNull(resultado);
        assertEquals("res-1", resultado.getId());
        verify(reservaService).criarReserva(any(Reserva.class));
    }

    @Test
    void buscarPorId_deveRetornarReserva() {
        Reserva reserva = new Reserva();
        reserva.setId("res-123");

        when(reservaService.buscarPorId("res-123")).thenReturn(reserva);

        Reserva resultado = reservaController.buscarPorId("res-123");

        assertNotNull(resultado);
        assertEquals("res-123", resultado.getId());
        verify(reservaService).buscarPorId("res-123");
    }

    @Test
    void cancelar_deveChamarServiceERetornarRespostaDto() {
        doNothing().when(reservaService).cancelarReserva("res-999");

        RespostaDto resposta = reservaController.cancelar("res-999");

        assertNotNull(resposta);
        assertTrue(resposta.sucesso());
        assertEquals("Reserva cancelada", resposta.mensagem());
        assertEquals(HttpStatus.OK, resposta.status());
        verify(reservaService).cancelarReserva("res-999");
    }
}
