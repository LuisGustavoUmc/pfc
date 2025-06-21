package br.com.findpark.service;

import br.com.findpark.dtos.estacionamentos.AtualizarEstacionamentoDto;
import br.com.findpark.dtos.estacionamentos.DetalhesEstacionamentoDto;
import br.com.findpark.dtos.reservas.StatusReserva;
import br.com.findpark.dtos.vagas.VagaDto;
import br.com.findpark.entities.Estacionamento;
import br.com.findpark.entities.Vaga;
import br.com.findpark.entities.enums.vagas.StatusVaga;
import br.com.findpark.exceptions.estacionamento.EstacionamentoComReservaAtivaException;
import br.com.findpark.exceptions.usuario.RecursoNaoEncontradoException;
import br.com.findpark.repositories.EstacionamentoRepository;
import br.com.findpark.repositories.ReservaRepository;
import br.com.findpark.repositories.VagaRepository;
import br.com.findpark.security.SecurityUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class EstacionamentoService {

    @Autowired
    private EstacionamentoRepository estacionamentoRepository;

    @Autowired
    private ReservaRepository reservaRepository;

    @Autowired
    private VagaRepository vagaRepository;

    @Autowired
    private LogExclusaoService logExclusaoService;

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
     * @param // id opcional, filtro por ID do estacionamento.
     * @param pageable dados de paginação.
     * @return página de detalhes dos estacionamentos com vagas livres.
     * @throws RecursoNaoEncontradoException se não encontrar estacionamentos.
     */
    public Page<DetalhesEstacionamentoDto> buscarComVagasDisponiveis(String id, String termo, Pageable pageable) {
        List<Estacionamento> estacionamentos;

        if (id != null && !id.isEmpty()) {
            Estacionamento est = estacionamentoRepository.findById(id)
                    .orElseThrow(() -> new RecursoNaoEncontradoException("Estacionamento não encontrado"));
            estacionamentos = List.of(est);
        } else {
            estacionamentos = estacionamentoRepository.findAll();
        }

        // Filtro por termo de busca (nome, localidade ou bairro)
        if (termo != null && !termo.trim().isEmpty()) {
            String termoLower = termo.toLowerCase();
            estacionamentos = estacionamentos.stream()
                    .filter(e ->
                            (e.getNome() != null && e.getNome().toLowerCase().contains(termoLower)) ||
                                    (e.getEndereco() != null && (
                                            (e.getEndereco().getLocalidade() != null && e.getEndereco().getLocalidade().toLowerCase().contains(termoLower)) ||
                                                    (e.getEndereco().getBairro() != null && e.getEndereco().getBairro().toLowerCase().contains(termoLower))
                                    ))
                    )
                    .collect(Collectors.toList());
        }

        // Filtra somente os que possuem pelo menos uma vaga livre
        List<Estacionamento> comVagasLivres = estacionamentos.stream()
                .filter(est -> vagaRepository.countByEstacionamentoIdAndStatus(est.getId(), StatusVaga.LIVRE) > 0)
                .collect(Collectors.toList());

        if (comVagasLivres.isEmpty()) {
            throw new RecursoNaoEncontradoException("Nenhum estacionamento com vagas disponíveis.");
        }

        // Paginação manual
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
     * Remove um estacionamento pelo seu ID, se não possuir reservas ativas.
     *
     * @param id identificador do estacionamento a ser removido.
     * @throws RecursoNaoEncontradoException se o estacionamento não for encontrado.
     * @throws IllegalStateException se houver reservas ativas vinculadas ao estacionamento.
     */
    public void delete(String id) {
        Estacionamento estacionamento = estacionamentoRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException(
                        "Estacionamento não encontrado com id " + id
                ));

        boolean possuiReservasAtivas = reservaRepository.existsByEstacionamentoIdAndStatus(id, StatusReserva.ATIVA);

        if (possuiReservasAtivas) {
            throw new EstacionamentoComReservaAtivaException(
                    "Não é possível deletar o estacionamento. Existem reservas ativas vinculadas."
            );
        }

        vagaRepository.deleteByEstacionamentoId(id);

        estacionamentoRepository.delete(estacionamento);

        log.info("Estacionamento {} foi deletado pelo usuário {}", id, SecurityUtils.getCurrentUsuario().getId());

        logExclusaoService.registrar(
                "Estacionamento", // Nome da entidade
                id,               // ID do estacionamento deletado
                "Estacionamento '" + estacionamento.getNome() + "' removido."
        );
    }
}
