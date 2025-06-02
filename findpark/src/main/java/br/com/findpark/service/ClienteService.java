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

    /**
     * Retorna o cliente atualmente logado.
     * @throws RecursoNaoEncontradoException se o cliente não for encontrado.
     */
    public Cliente getClienteLogado() {
        String id = SecurityUtils.getCurrentUsuario().getId();
        return clienteRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Cliente não encontrado"));
    }

    /**
     * Lista as placas de veículos do cliente logado.
     * @return lista de placas.
     */
    public List<String> listarPlacas() {
        return getClienteLogado().getPlacas();
    }

    /**
     * Adiciona uma nova placa ao cliente logado, se ainda não existir (ignora case).
     * @param novaPlaca a placa a ser adicionada.
     */
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

    /**
     * Remove uma placa do cliente logado, ignorando case.
     * @param placa a placa a ser removida.
     */
    public void removerPlaca(String placa) {
        Cliente cliente = getClienteLogado();
        cliente.getPlacas().removeIf(p -> p.equalsIgnoreCase(placa));
        clienteRepository.save(cliente);
    }

    /**
     * Atualiza uma placa existente do cliente logado para uma nova placa (case-insensitive).
     * @param placaAntiga a placa a ser substituída.
     * @param placaNova a nova placa que substituirá a antiga.
     */
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
