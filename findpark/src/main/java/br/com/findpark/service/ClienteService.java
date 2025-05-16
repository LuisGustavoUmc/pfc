package br.com.findpark.service;

import br.com.findpark.entities.Cliente;
import br.com.findpark.exceptions.usuario.RecursoNaoEncontradoException;
import br.com.findpark.repositories.ClienteRepository;
import br.com.findpark.security.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ClienteService {

    @Autowired
    private ClienteRepository clienteRepository;

    public Cliente getClienteLogado() {
        String id = SecurityUtils.getCurrentUsuario().getId();
        return clienteRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Cliente não encontrado"));
    }

    public List<String> listarPlacas() {
        return getClienteLogado().getPlacas();
    }

    public void adicionarPlaca(String novaPlaca) {
        Cliente cliente = getClienteLogado();
        String novaPlacaUpper = novaPlaca.toUpperCase();

        boolean placaJaExiste = cliente.getPlacas().stream()
                .anyMatch(placa -> placa.equalsIgnoreCase(novaPlaca));

        if (!placaJaExiste) {
            cliente.getPlacas().add(novaPlacaUpper);
            clienteRepository.save(cliente);
        }
    }

    public void removerPlaca(String placa) {
        Cliente cliente = getClienteLogado();
        cliente.getPlacas().removeIf(p -> p.equalsIgnoreCase(placa));
        clienteRepository.save(cliente);
    }

    public void atualizarPlaca(String placaAntiga, String placaNova) {
        Cliente cliente = getClienteLogado();
        List<String> placas = cliente.getPlacas();

        int index = -1;
        for (int i = 0; i < placas.size(); i++) {
            if (placas.get(i).equalsIgnoreCase(placaAntiga)) {
                index = i;
                break;
            }
        }

        if (index != -1) {
            placas.set(index, placaNova.toUpperCase());
            clienteRepository.save(cliente);
        }
    }
}
