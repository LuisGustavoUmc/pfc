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

    /**
     * Cria uma nova vaga vinculada a um estacionamento existente,
     * respeitando a capacidade máxima do estacionamento.
     * @param vaga vaga a ser criada
     * @return vaga salva
     * @throws RecursoNaoEncontradoException se estacionamento não existir
     * @throws RequisicaoInvalidaException se limite de vagas for atingido
     */
    public Vaga criar(Vaga vaga) {
        Estacionamento estacionamento = estacionamentoRepository.findById(vaga.getEstacionamentoId())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Estacionamento não encontrado com id " + vaga.getEstacionamentoId()));

        long totalVagas = vagaRepository.countByEstacionamentoId(estacionamento.getId());

        if (totalVagas >= estacionamento.getCapacidade()) {
            throw new RequisicaoInvalidaException("Número máximo de vagas atingido para este estacionamento.");
        }

        return vagaRepository.save(vaga);
    }

    /**
     * Busca vaga por ID, lança exceção se não encontrada.
     * @param id ID da vaga
     * @return vaga encontrada
     */
    public Vaga buscarPorId(String id) {
        return vagaRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Vaga não encontrada com id " + id));
    }

    /**
     * Busca todas as vagas com paginação.
     * @param pageable paginação
     * @return página de vagas
     */
    public Page<Vaga> buscarTodas(Pageable pageable) {
        return vagaRepository.findAll(pageable);
    }

    /**
     * Busca vagas filtradas por estacionamento com paginação.
     * @param estacionamentoId ID do estacionamento
     * @param pageable paginação
     * @return página de vagas
     */
    public Page<Vaga> buscarPorEstacionamento(String estacionamentoId, Pageable pageable) {
        return vagaRepository.findByEstacionamentoId(estacionamentoId, pageable);
    }

    /**
     * Busca vagas filtradas por estacionamento e status com paginação.
     * @param estacionamentoId ID do estacionamento
     * @param status status da vaga
     * @param pageable paginação
     * @return página de vagas
     */
    public Page<Vaga> buscarPorEstacionamentoEStatus(String estacionamentoId, StatusVaga status, Pageable pageable) {
        return vagaRepository.findByEstacionamentoIdAndStatus(estacionamentoId, status, pageable);
    }

    /**
     * Busca vagas com status LIVRE e adiciona dados do estacionamento
     * para compor DTO de retorno.
     * @param pageable paginação
     * @return página de DTOs com vaga e estacionamento
     */
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

    /**
     * Busca vagas filtrando pelo termo nos campos do estacionamento
     * (nome, cidade, bairro, estado), normalizando texto para busca
     * case-insensitive e sem acentos.
     * @param termo termo de busca
     * @param pageable paginação
     * @return página de DTOs com vaga e estacionamento
     */
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

    /**
     * Normaliza texto removendo acentos e convertendo para minúsculas.
     * @param s texto a normalizar
     * @return texto normalizado
     */
    private String normalize(String s) {
        if (s == null) return "";
        return Normalizer.normalize(s, Normalizer.Form.NFD)
                .replaceAll("[\\p{InCombiningDiacriticalMarks}]", "")
                .toLowerCase()
                .trim();
    }

    /**
     * Busca detalhes de uma vaga por ID incluindo dados do estacionamento.
     * @param id ID da vaga
     * @return DTO com dados completos da vaga e estacionamento
     * @throws RecursoNaoEncontradoException se vaga ou estacionamento não encontrados
     */
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

    /**
     * Atualiza dados de uma vaga existente.
     * @param id ID da vaga a ser atualizada
     * @param vagaAtualizada dados atualizados
     * @return vaga atualizada
     */
    public Vaga atualizar(String id, Vaga vagaAtualizada) {
        Vaga vagaExistente = buscarPorId(id);

        vagaExistente.setStatus(vagaAtualizada.getStatus());
        vagaExistente.setTipo(vagaAtualizada.getTipo());
        vagaExistente.setPreco(vagaAtualizada.getPreco());
        vagaExistente.setEstacionamentoId(vagaAtualizada.getEstacionamentoId());

        return vagaRepository.save(vagaExistente);
    }

    /**
     * Remove uma vaga pelo ID.
     * @param id ID da vaga
     */
    public void deletar(String id) {
        Vaga vaga = buscarPorId(id);
        vagaRepository.delete(vaga);
    }
}
