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

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @Value("${viacep.url}")
    private String serviceUrl;

    public EnderecoService(RestTemplate restTemplate, ObjectMapper objectMapper) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
    }

    // ✅ 1. Obter endereço completo pelo CEP
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
