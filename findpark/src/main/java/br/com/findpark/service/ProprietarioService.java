package br.com.findpark.service;

import br.com.findpark.entities.Proprietario;
import br.com.findpark.exceptions.usuario.RecursoNaoEncontradoException;
import br.com.findpark.repositories.ProprietarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProprietarioService {

    @Autowired
    private ProprietarioRepository proprietarioRepository;

    public Proprietario buscarPorId(String id) {
        return proprietarioRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Proprietário não encontrado com id: " + id));
    }

    public List<Proprietario> buscarTodos() {
        return proprietarioRepository.findAll();
    }

    public void deletar(String id) {
        Proprietario prop = buscarPorId(id);
        proprietarioRepository.delete(prop);
    }
}
