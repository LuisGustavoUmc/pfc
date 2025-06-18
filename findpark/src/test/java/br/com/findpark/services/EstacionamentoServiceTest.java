package br.com.findpark.services;

import br.com.findpark.dtos.estacionamentos.AtualizarEstacionamentoDto;
import br.com.findpark.dtos.estacionamentos.DetalhesEstacionamentoDto;
import br.com.findpark.entities.Endereco;
import br.com.findpark.entities.Estacionamento;
import br.com.findpark.entities.Vaga;
import br.com.findpark.entities.Usuario;
import br.com.findpark.entities.enums.vagas.StatusVaga;
import br.com.findpark.exceptions.usuario.RecursoNaoEncontradoException;
import br.com.findpark.repositories.EstacionamentoRepository;
import br.com.findpark.repositories.VagaRepository;
import br.com.findpark.security.SecurityUtils;
import br.com.findpark.service.EstacionamentoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.time.LocalTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class EstacionamentoServiceTest {

    @InjectMocks
    private EstacionamentoService estacionamentoService;

    @Mock
    private EstacionamentoRepository estacionamentoRepository;

    @Mock
    private VagaRepository vagaRepository;

    private Estacionamento estacionamento;

    @BeforeEach
    public void setup() {
        estacionamento = new Estacionamento();
        estacionamento.setId("est1");
        estacionamento.setNome("Estacionamento A");
        estacionamento.setEndereco(criarEnderecoFake());
        estacionamento.setTelefone("123456789");
        estacionamento.setCapacidade(10);
        estacionamento.setHoraAbertura(LocalTime.of(8, 0));
        estacionamento.setHoraFechamento(LocalTime.of(18, 0));
    }

    @Test
    public void testBuscarEstacionamentoPorId_Encontrado() {
        when(estacionamentoRepository.findById("est1")).thenReturn(Optional.of(estacionamento));

        Estacionamento result = estacionamentoService.buscarEstacionamentoPorId("est1");

        assertNotNull(result);
        assertEquals("est1", result.getId());
    }

    @Test
    public void testBuscarEstacionamentoPorId_NaoEncontrado() {
        when(estacionamentoRepository.findById("est1")).thenReturn(Optional.empty());

        assertThrows(RecursoNaoEncontradoException.class, () -> estacionamentoService.buscarEstacionamentoPorId("est1"));
    }

    @Test
    public void testBuscarPorProprietario() {
        Usuario usuario = mock(Usuario.class);
        when(usuario.getId()).thenReturn("prop1");

        Pageable pageable = PageRequest.of(0, 10);
        Page<Estacionamento> page = new PageImpl<>(List.of(estacionamento));
        when(estacionamentoRepository.findAllByIdProprietario("prop1", pageable)).thenReturn(page);

        try (MockedStatic<SecurityUtils> mocked = mockStatic(SecurityUtils.class)) {
            mocked.when(SecurityUtils::getCurrentUsuario).thenReturn(usuario);

            Page<Estacionamento> result = estacionamentoService.buscarPorProprietario(pageable);

            assertEquals(1, result.getTotalElements());
            assertEquals("Estacionamento A", result.getContent().get(0).getNome());
        }
    }

    @Test
    public void testBuscarComVagasDisponiveis_ComId() {
        Pageable pageable = PageRequest.of(0, 10);
        Vaga vaga = new Vaga();
        vaga.setId("v1");
        vaga.setTipo(vaga.getTipo());
        vaga.setPreco(5.0);

        when(estacionamentoRepository.findById("est1")).thenReturn(Optional.of(estacionamento));
        Page<Vaga> vagasPage = new PageImpl<>(List.of(vaga));
        when(vagaRepository.findByEstacionamentoIdAndStatus(eq("est1"), eq(StatusVaga.LIVRE), any(Pageable.class))).thenReturn(vagasPage);

        Page<DetalhesEstacionamentoDto> result = estacionamentoService.buscarComVagasDisponiveis("est1", null, pageable);

        assertEquals(1, result.getTotalElements());
        DetalhesEstacionamentoDto dto = result.getContent().get(0);
        assertEquals("est1", dto.id());
        assertEquals(1, dto.vagasDisponiveis());
        assertEquals(1, dto.vagas().size());
    }

    @Test
    public void testAtualizarEstacionamento() {
        Endereco enderecoFake = criarEnderecoFake();
        AtualizarEstacionamentoDto dto = new AtualizarEstacionamentoDto(
                "Novo Nome", enderecoFake, "987654321", 20, 5
        );

        estacionamentoService.atualizarEstacionamento(estacionamento, dto);

        verify(estacionamentoRepository).save(estacionamento);
        assertEquals("Novo Nome", estacionamento.getNome());
        assertEquals(enderecoFake, estacionamento.getEndereco());
        assertEquals("987654321", estacionamento.getTelefone());
        assertEquals(20, estacionamento.getCapacidade());
        assertEquals(5, estacionamento.getVagasDisponiveis());
    }

    @Test
    public void testDelete_Encontrado() {
        when(estacionamentoRepository.findById("est1")).thenReturn(Optional.of(estacionamento));

        estacionamentoService.delete("est1");

        verify(estacionamentoRepository).delete(estacionamento);
    }

    @Test
    public void testDelete_NaoEncontrado() {
        when(estacionamentoRepository.findById("est1")).thenReturn(Optional.empty());

        assertThrows(RecursoNaoEncontradoException.class, () -> estacionamentoService.delete("est1"));
    }

    private Endereco criarEnderecoFake() {
        return new Endereco(
                "01001-000", "Praça da Sé", "123", "lado ímpar", null, "Sé",
                "São Paulo", "SP", "São Paulo", "Sudeste", "3550308", "1004", "11", "7107"
        );
    }
}
