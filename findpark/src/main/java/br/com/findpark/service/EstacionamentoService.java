package br.com.findpark.service;

import br.com.findpark.dtos.estacionamentos.AtualizarEstacionamentoDto;
import br.com.findpark.dtos.estacionamentos.DetalhesEstacionamentoDto;
import br.com.findpark.dtos.vagas.VagaDto;
import br.com.findpark.entities.Estacionamento;
import br.com.findpark.entities.Vaga;
import br.com.findpark.entities.enums.vagas.StatusVaga;
import br.com.findpark.exceptions.usuario.RecursoNaoEncontradoException;
import br.com.findpark.repositories.EstacionamentoRepository;
import br.com.findpark.repositories.VagaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EstacionamentoService {

    @Autowired
    private EstacionamentoRepository estacionamentoRepository;

    @Autowired
    private VagaRepository vagaRepository;

    public Estacionamento criarEstacionamento(Estacionamento estacionamento) {
        return estacionamentoRepository.save(estacionamento);
    }

    public Estacionamento buscarEstacionamentoPorId(String id) {
        return estacionamentoRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Estacionamento não encontrado com id " + id));
    }

    public List<Estacionamento> buscarPorProprietario(String idProprietario) {
        return estacionamentoRepository.findAllByIdProprietario(idProprietario);
    }

    public List<Estacionamento> buscarTodosEstacionamentos() {
        return estacionamentoRepository.findAll();
    }

    public DetalhesEstacionamentoDto buscarComVagasDisponiveis(String id) {
        Estacionamento est = estacionamentoRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Estacionamento não encontrado"));

        List<Vaga> vagasLivres = vagaRepository.findByEstacionamentoIdAndStatus(id, StatusVaga.LIVRE);

        List<VagaDto> vagaDtos = vagasLivres.stream()
                .map(v -> new VagaDto(v.getId(), v.getTipo(), v.getPreco()))
                .toList();

        return new DetalhesEstacionamentoDto(
                est.getId(),
                est.getNome(),
                est.getEndereco(),
                est.getCapacidade(),
                vagasLivres.size(),
                est.getHoraAbertura().toString(),
                est.getHoraFechamento().toString(),
                vagaDtos
        );
    }

    public void atualizarEstacionamento(Estacionamento estacionamento, AtualizarEstacionamentoDto dto) {
        if (dto.nome() != null) estacionamento.setNome(dto.nome());
        if (dto.endereco() != null) estacionamento.setEndereco(dto.endereco());
        estacionamento.setCapacidade(dto.capacidade());
        estacionamento.setVagasDisponiveis(dto.vagasDisponiveis());

        estacionamentoRepository.save(estacionamento);
    }

    public void delete(String id) {
        Estacionamento entidade = estacionamentoRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Estacionamento não encontrado com id " + id));
        estacionamentoRepository.delete(entidade);
    }
}
