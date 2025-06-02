package br.com.findpark.service;

import br.com.findpark.entities.Endereco;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class EnderecoService {

    /**
     * Serviço para buscar endereço a partir do CEP utilizando a API ViaCEP.
     */

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @Value("${viacep.url}")
    private String serviceUrl;

    public EnderecoService(RestTemplate restTemplate, ObjectMapper objectMapper) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
    }

    /**
     * Consulta o endereço correspondente ao CEP informado usando a API externa ViaCEP.
     *
     * @param cep o código postal a ser consultado.
     * @return o objeto Endereco mapeado da resposta JSON, ou null em caso de erro no parsing.
     */
    public Endereco buscarEnderecoPorCep(String cep) {
        String url = serviceUrl + cep + "/json/";
        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
        try {
            return objectMapper.readValue(response.getBody(), Endereco.class);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return null;
        }
    }
}

