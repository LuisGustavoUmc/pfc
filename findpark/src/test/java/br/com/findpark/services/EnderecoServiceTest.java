package br.com.findpark.services;

import br.com.findpark.entities.Endereco;
import br.com.findpark.service.EnderecoService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class EnderecoServiceTest {

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private EnderecoService enderecoService;

    private final String CEP = "01001000";
    private final String URL_BASE = "https://viacep.com.br/ws/";

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        enderecoService = new EnderecoService(restTemplate, objectMapper);
        try {
            // Injetando manualmente a URL simulada
            java.lang.reflect.Field field = EnderecoService.class.getDeclaredField("serviceUrl");
            field.setAccessible(true);
            field.set(enderecoService, URL_BASE);
        } catch (Exception e) {
            fail("Falha ao injetar serviceUrl no teste");
        }
    }

    @Test
    public void testBuscarEnderecoPorCep_Sucesso() throws JsonProcessingException {
        String jsonResposta = """
                {
                  "cep": "01001-000",
                  "logradouro": "Praça da Sé",
                  "complemento": "lado ímpar",
                  "bairro": "Sé",
                  "localidade": "São Paulo",
                  "uf": "SP",
                  "ibge": "3550308",
                  "gia": "1004",
                  "ddd": "11",
                  "siafi": "7107"
                }
                """;

        Endereco enderecoEsperado = new Endereco(
                "01001-000", "Praça da Sé", null, "lado ímpar", null, "Sé",
                "São Paulo", "SP", null, null, "3550308", "1004", "11", "7107"
        );

        String urlEsperada = URL_BASE + CEP + "/json/";

        when(restTemplate.getForEntity(urlEsperada, String.class))
                .thenReturn(new ResponseEntity<>(jsonResposta, HttpStatus.OK));

        when(objectMapper.readValue(jsonResposta, Endereco.class))
                .thenReturn(enderecoEsperado);

        Endereco endereco = enderecoService.buscarEnderecoPorCep(CEP);

        assertNotNull(endereco);
        assertEquals("01001-000", endereco.getCep());
        assertEquals("Praça da Sé", endereco.getLogradouro());
        assertEquals("São Paulo", endereco.getLocalidade());
        assertEquals("SP", endereco.getUf());
        assertEquals("Sé", endereco.getBairro());
    }
}

