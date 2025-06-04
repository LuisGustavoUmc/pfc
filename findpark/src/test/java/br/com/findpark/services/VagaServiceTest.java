package br.com.findpark.services;

import br.com.findpark.dtos.estacionamentos.VagaComEstacionamentoDto;
import br.com.findpark.entities.Estacionamento;
import br.com.findpark.entities.Endereco;
import br.com.findpark.entities.Vaga;
import br.com.findpark.entities.enums.vagas.StatusVaga;
import br.com.findpark.entities.enums.vagas.TipoVaga;
import br.com.findpark.exceptions.RequisicaoInvalidaException;
import br.com.findpark.exceptions.usuario.RecursoNaoEncontradoException;
import br.com.findpark.repositories.EstacionamentoRepository;
import br.com.findpark.repositories.VagaRepository;
import br.com.findpark.service.VagaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VagaServiceTest {

    @InjectMocks
    private VagaService vagaService;

    @Mock
    private VagaRepository vagaRepository;

    @Mock
    private EstacionamentoRepository estacionamentoRepository;

    private Estacionamento estacionamento;
    private Vaga vaga;

    @BeforeEach
    void setup() {
        estacionamento = new Estacionamento();
        estacionamento.setId("est-1");
        estacionamento.setNome("Estacionamento Central");
        estacionamento.setCapacidade(2);
        Endereco endereco = new Endereco();
        endereco.setLocalidade("CidadeX");
        endereco.setBairro("Centro");
        endereco.setUf("SP");
        estacionamento.setEndereco(endereco);

        vaga = new Vaga();
        vaga.setId("vaga-1");
        vaga.setEstacionamentoId(estacionamento.getId());
        vaga.setStatus(StatusVaga.LIVRE);
        vaga.setPreco(10);
        vaga.setTipo(List.of(TipoVaga.DESCOBERTA));
    }

    @Test
    void criar_comSucesso() {
        when(estacionamentoRepository.findById("est-1")).thenReturn(Optional.of(estacionamento));
        when(vagaRepository.countByEstacionamentoId("est-1")).thenReturn(1L);
        when(vagaRepository.save(vaga)).thenReturn(vaga);

        Vaga resultado = vagaService.criar(vaga);

        assertNotNull(resultado);
        assertEquals("vaga-1", resultado.getId());
        verify(vagaRepository).save(vaga);
    }

    @Test
    void criar_quandoCapacidadeExcedida_deveLancarExcecao() {
        when(estacionamentoRepository.findById("est-1")).thenReturn(Optional.of(estacionamento));
        when(vagaRepository.countByEstacionamentoId("est-1")).thenReturn(2L); // já atingiu capacidade

        RequisicaoInvalidaException ex = assertThrows(RequisicaoInvalidaException.class, () -> {
            vagaService.criar(vaga);
        });
        assertTrue(ex.getMessage().contains("Número máximo de vagas atingido"));
    }

    @Test
    void criar_quandoEstacionamentoNaoExiste_deveLancarExcecao() {
        when(estacionamentoRepository.findById("est-1")).thenReturn(Optional.empty());

        RecursoNaoEncontradoException ex = assertThrows(RecursoNaoEncontradoException.class, () -> {
            vagaService.criar(vaga);
        });
        assertTrue(ex.getMessage().contains("Estacionamento não encontrado"));
    }

    @Test
    void buscarPorId_existente() {
        when(vagaRepository.findById("vaga-1")).thenReturn(Optional.of(vaga));

        Vaga resultado = vagaService.buscarPorId("vaga-1");

        assertEquals("vaga-1", resultado.getId());
    }

    @Test
    void buscarPorId_inexistente_deveLancarExcecao() {
        when(vagaRepository.findById("vaga-999")).thenReturn(Optional.empty());

        RecursoNaoEncontradoException ex = assertThrows(RecursoNaoEncontradoException.class, () -> {
            vagaService.buscarPorId("vaga-999");
        });
        assertTrue(ex.getMessage().contains("Vaga não encontrada"));
    }

    @Test
    void buscarTodas_retornaPagina() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Vaga> page = new PageImpl<>(List.of(vaga), pageable, 1);
        when(vagaRepository.findAll(pageable)).thenReturn(page);

        Page<Vaga> resultado = vagaService.buscarTodas(pageable);

        assertEquals(1, resultado.getTotalElements());
        assertEquals("vaga-1", resultado.getContent().get(0).getId());
    }

    @Test
    void buscarPorEstacionamento_retornaPagina() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Vaga> page = new PageImpl<>(List.of(vaga), pageable, 1);
        when(vagaRepository.findByEstacionamentoId("est-1", pageable)).thenReturn(page);

        Page<Vaga> resultado = vagaService.buscarPorEstacionamento("est-1", pageable);

        assertEquals(1, resultado.getTotalElements());
    }

    @Test
    void buscarPorEstacionamentoEStatus_retornaPagina() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Vaga> page = new PageImpl<>(List.of(vaga), pageable, 1);
        when(vagaRepository.findByEstacionamentoIdAndStatus("est-1", StatusVaga.LIVRE, pageable)).thenReturn(page);

        Page<Vaga> resultado = vagaService.buscarPorEstacionamentoEStatus("est-1", StatusVaga.LIVRE, pageable);

        assertEquals(1, resultado.getTotalElements());
        assertEquals(StatusVaga.LIVRE, resultado.getContent().get(0).getStatus());
    }

    @Test
    void buscarVagasComEstacionamento_retornaDtoPage() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Vaga> vagasPage = new PageImpl<>(List.of(vaga), pageable, 1);

        when(vagaRepository.findByStatus(StatusVaga.LIVRE, pageable)).thenReturn(vagasPage);
        when(estacionamentoRepository.findById("est-1")).thenReturn(Optional.of(estacionamento));

        Page<VagaComEstacionamentoDto> resultado = vagaService.buscarVagasComEstacionamento(pageable);

        assertEquals(1, resultado.getTotalElements());
        assertEquals("Estacionamento Central", resultado.getContent().get(0).getEstacionamento().getNome());
    }

    @Test
    void buscarPorTermo_filtraResultadosCorretamente() {
        Pageable pageable = PageRequest.of(0, 10);
        Vaga vaga2 = new Vaga();
        vaga2.setId("vaga-2");
        vaga2.setEstacionamentoId("est-2");
        vaga2.setStatus(StatusVaga.LIVRE);
        vaga2.setPreco(15);
        vaga2.setTipo(List.of(TipoVaga.IDOSO));

        Estacionamento est2 = new Estacionamento();
        est2.setId("est-2");
        est2.setNome("Outro Estacionamento");
        Endereco endereco2 = new Endereco();
        endereco2.setLocalidade("OutraCidade");
        endereco2.setBairro("BairroX");
        endereco2.setUf("RJ");
        est2.setEndereco(endereco2);

        Page<Vaga> vagasPage = new PageImpl<>(List.of(vaga, vaga2), pageable, 2);

        when(vagaRepository.findAll(pageable)).thenReturn(vagasPage);
        when(estacionamentoRepository.findById("est-1")).thenReturn(Optional.of(estacionamento));
        when(estacionamentoRepository.findById("est-2")).thenReturn(Optional.of(est2));

        Page<VagaComEstacionamentoDto> resultado = vagaService.buscarPorTermo("Central", pageable);

        assertEquals(1, resultado.getTotalElements());
        assertEquals("vaga-1", resultado.getContent().get(0).getId());
    }

    @Test
    void buscarDetalhesPorId_sucesso() {
        when(vagaRepository.findById("vaga-1")).thenReturn(Optional.of(vaga));
        when(estacionamentoRepository.findById("est-1")).thenReturn(Optional.of(estacionamento));

        VagaComEstacionamentoDto dto = vagaService.buscarDetalhesPorId("vaga-1");

        assertEquals("vaga-1", dto.getId());
        assertEquals("Estacionamento Central", dto.getEstacionamento().getNome());
    }

    @Test
    void buscarDetalhesPorId_vagaNaoEncontrada() {
        when(vagaRepository.findById("vaga-999")).thenReturn(Optional.empty());

        assertThrows(RecursoNaoEncontradoException.class, () -> {
            vagaService.buscarDetalhesPorId("vaga-999");
        });
    }

    @Test
    void buscarDetalhesPorId_estacionamentoNaoEncontrado() {
        when(vagaRepository.findById("vaga-1")).thenReturn(Optional.of(vaga));
        when(estacionamentoRepository.findById("est-1")).thenReturn(Optional.empty());

        assertThrows(RecursoNaoEncontradoException.class, () -> {
            vagaService.buscarDetalhesPorId("vaga-1");
        });
    }

    @Test
    void atualizar_sucesso() {
        Vaga atualizacao = new Vaga();
        atualizacao.setTipo(List.of(TipoVaga.DEFICIENTE));
        atualizacao.setPreco(20);
        atualizacao.setEstacionamentoId("est-1");

        when(vagaRepository.findById("vaga-1")).thenReturn(Optional.of(vaga));
        when(vagaRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        Vaga resultado = vagaService.atualizar("vaga-1", atualizacao);

        assertEquals(List.of(TipoVaga.DEFICIENTE), resultado.getTipo());
        assertEquals((20), resultado.getPreco());
    }

    @Test
    void deletar_sucesso() {
        when(vagaRepository.findById("vaga-1")).thenReturn(Optional.of(vaga));

        vagaService.deletar("vaga-1");

        verify(vagaRepository).delete(vaga);
    }
}
