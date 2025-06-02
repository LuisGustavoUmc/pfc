package br.com.findpark.controllers;

import br.com.findpark.entities.Endereco;
import br.com.findpark.service.EnderecoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class EnderecoControllerTest {

    @Mock
    private EnderecoService enderecoService;

    @InjectMocks
    private EnderecoController enderecoController;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void buscarEndereco_quandoEnderecoExiste_retornaOkComEndereco() {
        String cep = "12345678";
        Endereco endereco = new Endereco();
        endereco.setCep(cep);
        endereco.setLocalidade("Cidade Teste");
        endereco.setBairro("Bairro Teste");
        endereco.setUf("SP");
        endereco.setLogradouro("Rua Teste");

        when(enderecoService.buscarEnderecoPorCep(cep)).thenReturn(endereco);

        ResponseEntity<Endereco> response = enderecoController.buscarEndereco(cep);

        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals(cep, response.getBody().getCep());
        verify(enderecoService, times(1)).buscarEnderecoPorCep(cep);
    }

    @Test
    void buscarEndereco_quandoEnderecoNaoExiste_retornaNotFound() {
        String cep = "00000000";

        when(enderecoService.buscarEnderecoPorCep(cep)).thenReturn(null);

        ResponseEntity<Endereco> response = enderecoController.buscarEndereco(cep);

        assertEquals(404, response.getStatusCodeValue());
        assertNull(response.getBody());
        verify(enderecoService, times(1)).buscarEnderecoPorCep(cep);
    }
}
