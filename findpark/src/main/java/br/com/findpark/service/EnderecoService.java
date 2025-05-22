package br.com.findpark.service;

import br.com.findpark.entities.Endereco;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.List;

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

    // ✅ 2. Validar se o CEP existe (para formulário de estacionamento, por exemplo)
    public boolean validarCepExiste(String cep) {
        String url = serviceUrl + cep + "/json/";
        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
        return response.getBody() != null && !response.getBody().contains("\"erro\": true");
    }

    // ✅ 3. Buscar lista de endereços por logradouro (opcional para buscas futuras)
    public List<Endereco> buscarEnderecosPorLogradouro(String uf, String cidade, String logradouro) {
        String url = serviceUrl + uf + "/" + cidade + "/" + logradouro + "/json/";
        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
        try {
            return objectMapper.readValue(response.getBody(),
                    objectMapper.getTypeFactory().constructCollectionType(List.class, Endereco.class));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }
}
