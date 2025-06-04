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
import br.com.findpark.security.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class EstacionamentoService {

    @Autowired
    private EstacionamentoRepository estacionamentoRepository;

    @Autowired
    private VagaRepository vagaRepository;

    /**
     * Cria e salva um novo estacionamento no repositório.
     * @param estacionamento o estacionamento a ser criado.
     * @return o estacionamento salvo.
     */
    public Estacionamento criarEstacionamento(Estacionamento estacionamento) {
        return estacionamentoRepository.save(estacionamento);
    }

    /**
     * Busca um estacionamento pelo seu ID.
     * @param id o identificador do estacionamento.
     * @return o estacionamento encontrado.
     * @throws RecursoNaoEncontradoException se não encontrar o estacionamento.
     */
    public Estacionamento buscarEstacionamentoPorId(String id) {
        return estacionamentoRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Estacionamento não encontrado com id " + id));
    }

    /**
     * Busca estacionamentos associados ao proprietário logado, paginados.
     * @param pageable dados de paginação.
     * @return página de estacionamentos do proprietário.
     */
    public Page<Estacionamento> buscarPorProprietario(Pageable pageable) {
        String idProprietario = SecurityUtils.getCurrentUsuario().getId();
        return estacionamentoRepository.findAllByIdProprietario(idProprietario, pageable);
    }

    /**
     * Busca todos os estacionamentos paginados.
     * @param pageable dados de paginação.
     * @return página de todos os estacionamentos.
     */
    public Page<Estacionamento> buscarTodosEstacionamentos(Pageable pageable) {
        return estacionamentoRepository.findAll(pageable);
    }

    /**
     * Busca estacionamentos com vagas disponíveis, opcionalmente filtrando por ID.
     * @param id opcional, filtro por ID do estacionamento.
     * @param pageable dados de paginação.
     * @return página de detalhes dos estacionamentos com vagas livres.
     * @throws RecursoNaoEncontradoException se não encontrar estacionamentos.
     */
    public Page<DetalhesEstacionamentoDto> buscarComVagasDisponiveis(String id, Pageable pageable) {
        List<Estacionamento> estacionamentos;

        if (id != null && !id.isEmpty()) {
            Estacionamento est = estacionamentoRepository.findById(id)
                    .orElseThrow(() -> new RecursoNaoEncontradoException("Estacionamento não encontrado"));
            estacionamentos = List.of(est);
        } else {
            estacionamentos = estacionamentoRepository.findAll();
        }

        // Filtra somente os que possuem pelo menos uma vaga livre
        List<Estacionamento> comVagasLivres = estacionamentos.stream()
                .filter(est -> vagaRepository.countByEstacionamentoIdAndStatus(est.getId(), StatusVaga.LIVRE) > 0)
                .collect(Collectors.toList());

        if (comVagasLivres.isEmpty()) {
            throw new RecursoNaoEncontradoException("Nenhum estacionamento com vagas disponíveis.");
        }

        // Aplica a paginação manualmente
        int start = (int) pageable.getOffset();
        int end = Math.min(start + pageable.getPageSize(), comVagasLivres.size());
        List<Estacionamento> pagina = comVagasLivres.subList(start, end);

        List<DetalhesEstacionamentoDto> dtos = pagina.stream()
                .map(est -> {
                    long vagasLivres = vagaRepository.countByEstacionamentoIdAndStatus(est.getId(), StatusVaga.LIVRE);

                    List<VagaDto> vagaDtos = vagaRepository
                            .findByEstacionamentoIdAndStatus(est.getId(), StatusVaga.LIVRE, Pageable.ofSize(3))
                            .stream()
                            .map(v -> new VagaDto(v.getId(), v.getTipo(), v.getPreco()))
                            .collect(Collectors.toList());

                    return new DetalhesEstacionamentoDto(
                            est.getId(),
                            est.getNome(),
                            est.getEndereco(),
                            est.getTelefone(),
                            est.getCapacidade(),
                            (int) vagasLivres,
                            est.getHoraAbertura().toString(),
                            est.getHoraFechamento().toString(),
                            vagaDtos
                    );
                })
                .collect(Collectors.toList());

        return new PageImpl<>(dtos, pageable, comVagasLivres.size());
    }

    /**
     * Atualiza os dados de um estacionamento com base no DTO recebido.
     * @param estacionamento a entidade a ser atualizada.
     * @param dto dados para atualização.
     */
    public void atualizarEstacionamento(Estacionamento estacionamento, AtualizarEstacionamentoDto dto) {
        if (dto.nome() != null) estacionamento.setNome(dto.nome());
        if (dto.endereco() != null) estacionamento.setEndereco(dto.endereco());
        if (dto.telefone() != null) estacionamento.setTelefone(dto.telefone());
        estacionamento.setCapacidade(dto.capacidade());
        estacionamento.setVagasDisponiveis(dto.vagasDisponiveis());

        estacionamentoRepository.save(estacionamento);
    }

    /**
     * Remove um estacionamento pelo seu ID.
     * @param id identificador do estacionamento a ser removido.
     * @throws RecursoNaoEncontradoException se o estacionamento não for encontrado.
     */
    public void delete(String id) {
        Estacionamento entidade = estacionamentoRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Estacionamento não encontrado com id " + id));

        vagaRepository.deleteByEstacionamentoId(id);

        estacionamentoRepository.delete(entidade);
    }
}
