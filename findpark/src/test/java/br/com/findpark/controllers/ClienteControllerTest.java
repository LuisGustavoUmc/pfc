package br.com.findpark.controllers;

import br.com.findpark.dtos.RespostaDto;
import br.com.findpark.dtos.cliente.AtualizarPlacaDto;
import br.com.findpark.dtos.cliente.PlacaDto;
import br.com.findpark.service.ClienteService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

public class ClienteControllerTest {

    @Mock
    private ClienteService clienteService;

    @InjectMocks
    private ClienteController clienteController;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void listarPlacas_retornaListaPlacas() {
        List<String> placas = List.of("ABC1234", "XYZ5678");
        when(clienteService.listarPlacas()).thenReturn(placas);

        List<String> resultado = clienteController.listarPlacas();

        assertEquals(2, resultado.size());
        assertEquals("ABC1234", resultado.get(0));
        verify(clienteService, times(1)).listarPlacas();
    }

    @Test
    void adicionarPlaca_retornaRespostaCriada() {
        PlacaDto dto = new PlacaDto("ABC1234");

        doNothing().when(clienteService).adicionarPlaca("ABC1234");

        RespostaDto resposta = clienteController.adicionarPlaca(dto);

        assertEquals(HttpStatus.CREATED, resposta.status());
        assertTrue(resposta.sucesso());
        assertEquals("Placa adicionada", resposta.mensagem());
        verify(clienteService, times(1)).adicionarPlaca("ABC1234");
    }

    @Test
    void atualizarPlaca_retornaRespostaOk() {
        AtualizarPlacaDto dto = new AtualizarPlacaDto("ABC1234", "XYZ5678");

        doNothing().when(clienteService).atualizarPlaca("ABC1234", "XYZ5678");

        RespostaDto resposta = clienteController.atualizarPlaca(dto);

        assertEquals(HttpStatus.OK, resposta.status());
        assertTrue(resposta.sucesso());
        assertEquals("Placa atualizada", resposta.mensagem());
        verify(clienteService, times(1)).atualizarPlaca("ABC1234", "XYZ5678");
    }

    @Test
    void removerPlaca_retornaRespostaOk() {
        String placa = "ABC1234";

        doNothing().when(clienteService).removerPlaca(placa);

        RespostaDto resposta = clienteController.removerPlaca(placa);

        assertEquals(HttpStatus.OK, resposta.status());
        assertTrue(resposta.sucesso());
        assertEquals("Placa removida", resposta.mensagem());
        verify(clienteService, times(1)).removerPlaca(placa);
    }
}
