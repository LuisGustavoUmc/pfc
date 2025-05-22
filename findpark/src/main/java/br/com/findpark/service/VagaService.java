package br.com.findpark.service;

import br.com.findpark.dtos.estacionamentos.VagaComEstacionamentoDto;
import br.com.findpark.entities.Estacionamento;
import br.com.findpark.entities.Vaga;
import br.com.findpark.entities.enums.vagas.StatusVaga;
import br.com.findpark.exceptions.usuario.RecursoNaoEncontradoException;
import br.com.findpark.repositories.EstacionamentoRepository;
import br.com.findpark.repositories.VagaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.Normalizer;
import java.util.List;

@Service
public class VagaService {

    @Autowired
    private VagaRepository vagaRepository;

    @Autowired
    private EstacionamentoRepository estacionamentoRepository;

    public Vaga criar(Vaga vaga) {
        return vagaRepository.save(vaga);
    }

    public Vaga buscarPorId(String id) {
        return vagaRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Vaga não encontrada com id " + id));
    }

    public List<Vaga> buscarTodas() {
        return vagaRepository.findAll();
    }

    public List<Vaga> buscarPorEstacionamento(String estacionamentoId) {
        return vagaRepository.findByEstacionamentoId(estacionamentoId);
    }

    public List<Vaga> buscarPorEstacionamentoEStatus(String estacionamentoId, StatusVaga status) {
        return vagaRepository.findByEstacionamentoIdAndStatus(estacionamentoId, status);
    }

    public List<VagaComEstacionamentoDto> buscarVagasComEstacionamento() {
        List<Vaga> vagas = vagaRepository.findByStatus(StatusVaga.LIVRE);

        return vagas.stream().map(vaga -> {
            Estacionamento estacionamento = estacionamentoRepository.findById(vaga.getEstacionamentoId())
                    .orElseThrow(() -> new RecursoNaoEncontradoException("Estacionamento não encontrado"));
            return new VagaComEstacionamentoDto(
                    vaga.getId(),
                    vaga.getStatus(),
                    vaga.getTipo(),
                    vaga.getPreco(),
                    estacionamento
            );
        }).toList();
    }

    public List<Vaga> buscarPorTermo(String termo) {
        String termoNormalizado = normalize(termo);

        return vagaRepository.findAll().stream()
                .filter(vaga -> {
                    Estacionamento est = estacionamentoRepository.findById(vaga.getEstacionamentoId()).orElse(null);
                    if (est == null) return false;

                    String nome = normalize(est.getNome());
                    String cidade = normalize(est.getEndereco().getLocalidade());
                    String bairro = normalize(est.getEndereco().getBairro());
                    String estado = normalize(est.getEndereco().getUf());

                    return nome.contains(termoNormalizado)
                            || cidade.contains(termoNormalizado)
                            || bairro.contains(termoNormalizado)
                            || estado.contains(termoNormalizado);
                })
                .peek(vaga -> {
                    // atribui o estacionamento para a vaga
                    Estacionamento est = estacionamentoRepository.findById(vaga.getEstacionamentoId()).orElse(null);
                    vaga.setEstacionamento(est);
                })
                .toList();
    }

    private String normalize(String s) {
        if (s == null) return "";
        return Normalizer.normalize(s, Normalizer.Form.NFD)
                .replaceAll("[\\p{InCombiningDiacriticalMarks}]", "")
                .toLowerCase()
                .trim();
    }

    public VagaComEstacionamentoDto buscarDetalhesPorId(String id) {
        Vaga vaga = vagaRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Vaga não encontrada"));

        Estacionamento estacionamento = estacionamentoRepository.findById(vaga.getEstacionamentoId())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Estacionamento não encontrado"));

        return new VagaComEstacionamentoDto(
                vaga.getId(),
                vaga.getStatus(),
                vaga.getTipo(),
                vaga.getPreco(),
                estacionamento
        );
    }

    public Vaga atualizar(String id, Vaga vagaAtualizada) {
        Vaga vagaExistente = buscarPorId(id);

        vagaExistente.setStatus(vagaAtualizada.getStatus());
        vagaExistente.setTipo(vagaAtualizada.getTipo());
        vagaExistente.setPreco(vagaAtualizada.getPreco());
        vagaExistente.setEstacionamentoId(vagaAtualizada.getEstacionamentoId());

        return vagaRepository.save(vagaExistente);
    }

    public void deletar(String id) {
        Vaga vaga = buscarPorId(id);
        vagaRepository.delete(vaga);
    }
}
