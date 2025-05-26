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

    public Estacionamento criarEstacionamento(Estacionamento estacionamento) {
        return estacionamentoRepository.save(estacionamento);
    }

    public Estacionamento buscarEstacionamentoPorId(String id) {
        return estacionamentoRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Estacionamento não encontrado com id " + id));
    }

    public Page<Estacionamento> buscarPorProprietario(Pageable pageable) {
        String idProprietario = SecurityUtils.getCurrentUsuario().getId();
        return estacionamentoRepository.findAllByIdProprietario(idProprietario, pageable);
    }

    public Page<Estacionamento> buscarTodosEstacionamentos(Pageable pageable) {
        return estacionamentoRepository.findAll(pageable);
    }

    public Page<DetalhesEstacionamentoDto> buscarComVagasDisponiveis(String id, Pageable pageable) {
        Page<Estacionamento> estacionamentosPage;

        if (id != null && !id.isEmpty()) {
            Estacionamento est = estacionamentoRepository.findById(id)
                    .orElseThrow(() -> new RecursoNaoEncontradoException("Estacionamento não encontrado"));

            estacionamentosPage = new PageImpl<>(List.of(est), pageable, 1);
        } else {
            estacionamentosPage = estacionamentoRepository.findAll(pageable);
        }

        if (estacionamentosPage.isEmpty()) {
            throw new RecursoNaoEncontradoException("Nenhum estacionamento encontrado.");
        }

        List<DetalhesEstacionamentoDto> estacionamentoDtos = estacionamentosPage.stream()
                .map(est -> {
                    Page<Vaga> vagasLivres = vagaRepository.findByEstacionamentoIdAndStatus(est.getId(), StatusVaga.LIVRE, pageable);

                    List<VagaDto> vagaDtos = vagasLivres.stream()
                            .map(v -> new VagaDto(v.getId(), v.getTipo(), v.getPreco()))
                            .collect(Collectors.toList());

                    return new DetalhesEstacionamentoDto(
                            est.getId(),
                            est.getNome(),
                            est.getEndereco(),
                            est.getTelefone(),
                            est.getCapacidade(),
                            vagasLivres.getNumberOfElements(),
                            est.getHoraAbertura().toString(),
                            est.getHoraFechamento().toString(),
                            vagaDtos
                    );
                })
                .collect(Collectors.toList());

        return new PageImpl<>(estacionamentoDtos, pageable, estacionamentosPage.getTotalElements());
    }


    public void atualizarEstacionamento(Estacionamento estacionamento, AtualizarEstacionamentoDto dto) {
        if (dto.nome() != null) estacionamento.setNome(dto.nome());
        if (dto.endereco() != null) estacionamento.setEndereco(dto.endereco());
        if (dto.telefone() != null) estacionamento.setTelefone(dto.telefone());
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
