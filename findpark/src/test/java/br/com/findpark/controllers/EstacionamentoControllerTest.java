package br.com.findpark.controllers;

import br.com.findpark.dtos.estacionamentos.AtualizarEstacionamentoDto;
import br.com.findpark.dtos.estacionamentos.DetalhesEstacionamentoDto;
import br.com.findpark.entities.Estacionamento;
import br.com.findpark.security.SecurityUtils;
import br.com.findpark.entities.Usuario; // ajuste conforme sua entidade real
import br.com.findpark.service.EstacionamentoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.data.domain.*;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class EstacionamentoControllerTest {

    @Mock
    private EstacionamentoService estacionamentoService;

    @InjectMocks
    private EstacionamentoController estacionamentoController;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void criarEstacionamento_deveChamarServiceERetornarOk() {
        Estacionamento input = new Estacionamento();
        input.setNome("Estacionamento Teste");

        // mock do usuário logado, tipo Usuario ou Proprietario conforme sua modelagem
        Usuario usuarioFake = new Usuario();
        usuarioFake.setId("user-123");
        usuarioFake.setNome("Teste");
        usuarioFake.setEmail("teste@email.com");

        try (MockedStatic<SecurityUtils> utilities = mockStatic(SecurityUtils.class)) {
            utilities.when(SecurityUtils::getCurrentUsuario).thenReturn(usuarioFake);

            Estacionamento salvo = new Estacionamento();
            salvo.setId("est-1");
            salvo.setNome(input.getNome());
            salvo.setIdProprietario("user-123");

            when(estacionamentoService.criarEstacionamento(any())).thenReturn(salvo);

            ResponseEntity<Estacionamento> response = estacionamentoController.criarEstacionamento(input);

            assertEquals(200, response.getStatusCodeValue());
            assertEquals("user-123", response.getBody().getIdProprietario());
            verify(estacionamentoService).criarEstacionamento(any());
        }
    }

    @Test
    void listarMeusEstacionamentos_retornaPagina() {
        Pageable pageable = PageRequest.of(0, 12, Sort.by(Sort.Direction.ASC, "nome"));
        Page<Estacionamento> page = new PageImpl<>(List.of(new Estacionamento()));

        when(estacionamentoService.buscarPorProprietario(any(Pageable.class))).thenReturn(page);

        ResponseEntity<Page<Estacionamento>> response = estacionamentoController.listarMeusEstacionamentos(0, 12, "asc");

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(page, response.getBody());
    }

    @Test
    void listarTodos_retornaPagina() {
        Pageable pageable = PageRequest.of(0, 12, Sort.by(Sort.Direction.ASC, "nome"));
        Page<Estacionamento> page = new PageImpl<>(List.of(new Estacionamento()));

        when(estacionamentoService.buscarTodosEstacionamentos(any(Pageable.class))).thenReturn(page);

        ResponseEntity<Page<Estacionamento>> response = estacionamentoController.listarTodos(0, 12, "asc");

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(page, response.getBody());
    }

    @Test
    void buscarPorId_retornaEstacionamento() {
        Estacionamento est = new Estacionamento();
        est.setId("est-1");

        when(estacionamentoService.buscarEstacionamentoPorId("est-1")).thenReturn(est);

        ResponseEntity<Estacionamento> response = estacionamentoController.buscarPorId("est-1");

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(est, response.getBody());
    }

    @Test
    void buscarDetalhes_retornaPagina() {
        Pageable pageable = PageRequest.of(0, 12, Sort.by(Sort.Direction.ASC, "nome"));
        Page<DetalhesEstacionamentoDto> page = new PageImpl<>(List.of());

        when(estacionamentoService.buscarComVagasDisponiveis(eq("est-1"), any(Pageable.class))).thenReturn(page);

        ResponseEntity<Page<DetalhesEstacionamentoDto>> response = estacionamentoController.buscarDetalhes("est-1", 0, 12, "asc");

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(page, response.getBody());
    }

    @Test
    void atualizar_deveChamarServiceERetornarNoContent() {
        String id = "est-1";
        AtualizarEstacionamentoDto dto = new AtualizarEstacionamentoDto(
                "Novo nome",
                null,
                null,
                0,
                0
        );


        Estacionamento est = new Estacionamento();
        est.setId(id);

        when(estacionamentoService.buscarEstacionamentoPorId(id)).thenReturn(est);
        doNothing().when(estacionamentoService).atualizarEstacionamento(est, dto);

        ResponseEntity<Void> response = estacionamentoController.atualizar(id, dto);

        assertEquals(204, response.getStatusCodeValue());
        verify(estacionamentoService).buscarEstacionamentoPorId(id);
        verify(estacionamentoService).atualizarEstacionamento(est, dto);
    }

    @Test
    void deletar_deveChamarServiceERetornarNoContent() {
        String id = "est-1";

        doNothing().when(estacionamentoService).delete(id);

        ResponseEntity<Void> response = estacionamentoController.deletar(id);

        assertEquals(204, response.getStatusCodeValue());
        verify(estacionamentoService).delete(id);
    }
}
