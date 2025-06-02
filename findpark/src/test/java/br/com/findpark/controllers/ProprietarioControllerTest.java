package br.com.findpark.controllers;

import br.com.findpark.entities.Proprietario;
import br.com.findpark.security.SecurityUtils;
import br.com.findpark.service.ProprietarioService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.data.domain.*;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ProprietarioControllerTest {

    @Mock
    private ProprietarioService proprietarioService;

    @InjectMocks
    private ProprietarioController proprietarioController;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void buscarPorId_deveRetornarProprietario() {
        String id = "prop-1";
        Proprietario prop = new Proprietario();
        prop.setId(id);
        prop.setNome("Nome Teste");

        when(proprietarioService.buscarPorId(id)).thenReturn(prop);

        ResponseEntity<Proprietario> response = proprietarioController.buscarPorId(id);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(prop, response.getBody());
        verify(proprietarioService).buscarPorId(id);
    }

    @Test
    void buscarTodas_deveRetornarPagina() {
        Pageable pageable = PageRequest.of(0, 12, Sort.by(Sort.Direction.ASC, "nome"));
        List<Proprietario> lista = List.of(new Proprietario(), new Proprietario());
        Page<Proprietario> page = new PageImpl<>(lista, pageable, lista.size());

        when(proprietarioService.buscarTodos(any(Pageable.class))).thenReturn(page);

        ResponseEntity<Page<Proprietario>> response = proprietarioController.buscarTodas(0, 12, "asc");

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(page, response.getBody());
        verify(proprietarioService).buscarTodos(any(Pageable.class));
    }

    @Test
    void deletar_deveChamarServicoERetornarNoContent() {
        // Mock SecurityUtils para retornar Proprietario fake
        try (MockedStatic<SecurityUtils> mockedSecurity = mockStatic(SecurityUtils.class)) {
            Proprietario usuarioAtual = new Proprietario();
            usuarioAtual.setId("prop-123");
            mockedSecurity.when(SecurityUtils::getCurrentUsuario).thenReturn(usuarioAtual);

            doNothing().when(proprietarioService).deletar("prop-123");

            ResponseEntity<Void> response = proprietarioController.deletar();

            assertEquals(204, response.getStatusCodeValue());
            verify(proprietarioService).deletar("prop-123");
        }
    }
}
