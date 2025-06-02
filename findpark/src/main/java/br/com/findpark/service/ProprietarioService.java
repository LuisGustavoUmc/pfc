package br.com.findpark.service;

import br.com.findpark.entities.Proprietario;
import br.com.findpark.exceptions.usuario.RecursoNaoEncontradoException;
import br.com.findpark.repositories.ProprietarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class ProprietarioService {

    @Autowired
    private ProprietarioRepository proprietarioRepository;

    /**
     * Busca um proprietário pelo seu ID.
     * @param id o identificador do proprietário.
     * @return o proprietário encontrado.
     * @throws RecursoNaoEncontradoException se o proprietário não for encontrado.
     */
    public Proprietario buscarPorId(String id) {
        return proprietarioRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Proprietário não encontrado com id: " + id));
    }


    /**
     * Busca todos os proprietários com paginação.
     * @param pageable dados de paginação.
     * @return página de proprietários.
     */
    public Page<Proprietario> buscarTodos(Pageable pageable) {
        return proprietarioRepository.findAll(pageable);
    }

    /**
     * Deleta um proprietário pelo seu ID.
     * @param id identificador do proprietário a ser deletado.
     * @throws RecursoNaoEncontradoException se o proprietário não for encontrado.
     */
    public void deletar(String id) {
        Proprietario prop = buscarPorId(id);
        proprietarioRepository.delete(prop);
    }
}
