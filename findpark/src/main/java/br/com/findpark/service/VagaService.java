package br.com.findpark.service;

import br.com.findpark.dtos.estacionamentos.VagaComEstacionamentoDto;
import br.com.findpark.entities.Estacionamento;
import br.com.findpark.entities.Vaga;
import br.com.findpark.entities.enums.vagas.StatusVaga;
import br.com.findpark.exceptions.RequisicaoInvalidaException;
import br.com.findpark.exceptions.usuario.RecursoNaoEncontradoException;
import br.com.findpark.repositories.EstacionamentoRepository;
import br.com.findpark.repositories.VagaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.text.Normalizer;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class VagaService {

    @Autowired
    private VagaRepository vagaRepository;

    @Autowired
    private EstacionamentoRepository estacionamentoRepository;

    public Vaga criar(Vaga vaga) {
        Estacionamento estacionamento = estacionamentoRepository.findById(vaga.getEstacionamentoId())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Estacionamento não encontrado com id " + vaga.getEstacionamentoId()));

        long totalVagas = vagaRepository.countByEstacionamentoId(estacionamento.getId());

        if (totalVagas >= estacionamento.getCapacidade()) {
            throw new RequisicaoInvalidaException("Número máximo de vagas atingido para este estacionamento.");
        }

        return vagaRepository.save(vaga);
    }

    public Vaga buscarPorId(String id) {
        return vagaRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Vaga não encontrada com id " + id));
    }

    public Page<Vaga> buscarTodas(Pageable pageable) {
        return vagaRepository.findAll(pageable);
    }

    public Page<Vaga> buscarPorEstacionamento(String estacionamentoId, Pageable pageable) {
        return vagaRepository.findByEstacionamentoId(estacionamentoId, pageable);
    }

    public Page<Vaga> buscarPorEstacionamentoEStatus(String estacionamentoId, StatusVaga status, Pageable pageable) {
        return vagaRepository.findByEstacionamentoIdAndStatus(estacionamentoId, status, pageable);
    }

    public Page<VagaComEstacionamentoDto> buscarVagasComEstacionamento(Pageable pageable) {
        Page<Vaga> vagas = vagaRepository.findByStatus(StatusVaga.LIVRE, pageable);

        return vagas.map(vaga -> {
            Estacionamento estacionamento = estacionamentoRepository.findById(vaga.getEstacionamentoId())
                    .orElseThrow(() -> new RecursoNaoEncontradoException("Estacionamento não encontrado"));
            return new VagaComEstacionamentoDto(
                    vaga.getId(),
                    vaga.getStatus(),
                    vaga.getTipo(),
                    vaga.getPreco(),
                    estacionamento
            );
        });
    }

    public Page<VagaComEstacionamentoDto> buscarPorTermo(String termo, Pageable pageable) {
        String termoNormalizado = normalize(termo);

        // Buscando todas as vagas paginadas
        Page<Vaga> vagasPage = vagaRepository.findAll(pageable);

        // Filtrando as vagas após carregar os dados
        List<Vaga> filteredVagas = vagasPage.stream()
                .filter(vaga -> {
                    Estacionamento est = estacionamentoRepository.findById(vaga.getEstacionamentoId()).orElse(null);
                    if (est == null) return false;

                    String nome = normalize(est.getNome());
                    String cidade = normalize(est.getEndereco().getLocalidade());
                    String bairro = normalize(est.getEndereco().getBairro());
                    String estado = normalize(est.getEndereco().getUf());

                    // Verificando se algum dos campos do estacionamento contém o termo pesquisado
                    return nome.contains(termoNormalizado)
                            || cidade.contains(termoNormalizado)
                            || bairro.contains(termoNormalizado)
                            || estado.contains(termoNormalizado);
                })
                .collect(Collectors.toList());

        // Mapear as Vagas para VagaComEstacionamentoDto
        List<VagaComEstacionamentoDto> vagaDtos = filteredVagas.stream()
                .map(vaga -> {
                    Estacionamento est = estacionamentoRepository.findById(vaga.getEstacionamentoId()).orElse(null);
                    return new VagaComEstacionamentoDto(
                            vaga.getId(),
                            vaga.getStatus(),
                            vaga.getTipo(),
                            vaga.getPreco(),
                            est
                    );
                })
                .collect(Collectors.toList());

        // Criando um novo Page<VagaComEstacionamentoDto>
        return new PageImpl<>(vagaDtos, pageable, vagasPage.getTotalElements());
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
