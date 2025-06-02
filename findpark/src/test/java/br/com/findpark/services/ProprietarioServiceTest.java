package br.com.findpark.services;

import br.com.findpark.entities.Proprietario;
import br.com.findpark.exceptions.usuario.RecursoNaoEncontradoException;
import br.com.findpark.repositories.ProprietarioRepository;
import br.com.findpark.service.ProprietarioService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProprietarioServiceTest {

    @InjectMocks
    private ProprietarioService proprietarioService;

    @Mock
    private ProprietarioRepository proprietarioRepository;

    private Proprietario proprietario;

    @BeforeEach
    public void setUp() {
        proprietario = new Proprietario();
        proprietario.setId("prop123");
        proprietario.setNome("João Proprietário");
    }

    @Test
    public void testBuscarPorId_Encontrado() {
        when(proprietarioRepository.findById("prop123")).thenReturn(Optional.of(proprietario));

        Proprietario result = proprietarioService.buscarPorId("prop123");

        assertNotNull(result);
        assertEquals("prop123", result.getId());
        assertEquals("João Proprietário", result.getNome());
    }

    @Test
    public void testBuscarPorId_NaoEncontrado() {
        when(proprietarioRepository.findById("prop123")).thenReturn(Optional.empty());

        assertThrows(RecursoNaoEncontradoException.class, () -> {
            proprietarioService.buscarPorId("prop123");
        });
    }

    @Test
    public void testBuscarTodos() {
        Page<Proprietario> page = new PageImpl<>(List.of(proprietario));
        Pageable pageable = PageRequest.of(0, 10);
        when(proprietarioRepository.findAll(pageable)).thenReturn(page);

        Page<Proprietario> result = proprietarioService.buscarTodos(pageable);

        assertEquals(1, result.getTotalElements());
        assertEquals("prop123", result.getContent().get(0).getId());
    }

    @Test
    public void testDeletar() {
        when(proprietarioRepository.findById("prop123")).thenReturn(Optional.of(proprietario));

        proprietarioService.deletar("prop123");

        verify(proprietarioRepository).delete(proprietario);
    }

    @Test
    public void testDeletar_NaoEncontrado() {
        when(proprietarioRepository.findById("prop123")).thenReturn(Optional.empty());

        assertThrows(RecursoNaoEncontradoException.class, () -> {
            proprietarioService.deletar("prop123");
        });

        verify(proprietarioRepository, never()).delete(any());
    }
}
