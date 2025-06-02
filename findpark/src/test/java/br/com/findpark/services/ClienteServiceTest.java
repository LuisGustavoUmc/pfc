package br.com.findpark.services;

import br.com.findpark.entities.Cliente;
import br.com.findpark.entities.Usuario;
import br.com.findpark.exceptions.usuario.RecursoNaoEncontradoException;
import br.com.findpark.repositories.ClienteRepository;
import br.com.findpark.security.SecurityUtils;
import br.com.findpark.service.ClienteService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.MockedStatic;

@ExtendWith(MockitoExtension.class)
public class ClienteServiceTest {

    @InjectMocks
    private ClienteService clienteService;

    @Mock
    private ClienteRepository clienteRepository;

    private Cliente clienteMock;

    private final String CLIENTE_ID = "123";

    @BeforeEach
    public void setUp() {
        clienteMock = new Cliente();
        clienteMock.setId(CLIENTE_ID);
        clienteMock.setPlacas(new ArrayList<>(List.of("ABC1234", "XYZ5678")));
    }

    @Test
    public void testGetClienteLogado_Encontrado() {
        Usuario usuario = mock(Usuario.class);
        when(usuario.getId()).thenReturn(CLIENTE_ID);

        try (MockedStatic<SecurityUtils> mocked = mockStatic(SecurityUtils.class)) {
            mocked.when(SecurityUtils::getCurrentUsuario).thenReturn(usuario);
            when(clienteRepository.findById(CLIENTE_ID)).thenReturn(Optional.of(clienteMock));

            Cliente cliente = clienteService.getClienteLogado();
            assertNotNull(cliente);
            assertEquals(CLIENTE_ID, cliente.getId());
        }
    }

    @Test
    public void testGetClienteLogado_NaoEncontrado() {
        Usuario usuario = mock(Usuario.class);
        when(usuario.getId()).thenReturn(CLIENTE_ID);

        try (MockedStatic<SecurityUtils> mocked = mockStatic(SecurityUtils.class)) {
            mocked.when(SecurityUtils::getCurrentUsuario).thenReturn(usuario);
            when(clienteRepository.findById(CLIENTE_ID)).thenReturn(Optional.empty());

            assertThrows(RecursoNaoEncontradoException.class, () -> clienteService.getClienteLogado());
        }
    }

    @Test
    public void testListarPlacas() {
        Usuario usuario = mock(Usuario.class);
        when(usuario.getId()).thenReturn(CLIENTE_ID);

        try (MockedStatic<SecurityUtils> mocked = mockStatic(SecurityUtils.class)) {
            mocked.when(SecurityUtils::getCurrentUsuario).thenReturn(usuario);
            when(clienteRepository.findById(CLIENTE_ID)).thenReturn(Optional.of(clienteMock));

            List<String> placas = clienteService.listarPlacas();
            assertEquals(2, placas.size());
            assertTrue(placas.contains("ABC1234"));
        }
    }

    @Test
    public void testAdicionarPlaca_Nova() {
        Usuario usuario = mock(Usuario.class);
        when(usuario.getId()).thenReturn(CLIENTE_ID);

        try (MockedStatic<SecurityUtils> mocked = mockStatic(SecurityUtils.class)) {
            mocked.when(SecurityUtils::getCurrentUsuario).thenReturn(usuario);
            when(clienteRepository.findById(CLIENTE_ID)).thenReturn(Optional.of(clienteMock));

            clienteService.adicionarPlaca("new1234");

            assertEquals(3, clienteMock.getPlacas().size());
            assertTrue(clienteMock.getPlacas().contains("NEW1234"));
            verify(clienteRepository).save(clienteMock);
        }
    }

    @Test
    public void testAdicionarPlaca_JaExiste() {
        Usuario usuario = mock(Usuario.class);
        when(usuario.getId()).thenReturn(CLIENTE_ID);

        try (MockedStatic<SecurityUtils> mocked = mockStatic(SecurityUtils.class)) {
            mocked.when(SecurityUtils::getCurrentUsuario).thenReturn(usuario);
            when(clienteRepository.findById(CLIENTE_ID)).thenReturn(Optional.of(clienteMock));

            clienteService.adicionarPlaca("abc1234");

            assertEquals(2, clienteMock.getPlacas().size());
            verify(clienteRepository, never()).save(any());
        }
    }

    @Test
    public void testRemoverPlaca() {
        Usuario usuario = mock(Usuario.class);
        when(usuario.getId()).thenReturn(CLIENTE_ID);

        try (MockedStatic<SecurityUtils> mocked = mockStatic(SecurityUtils.class)) {
            mocked.when(SecurityUtils::getCurrentUsuario).thenReturn(usuario);
            when(clienteRepository.findById(CLIENTE_ID)).thenReturn(Optional.of(clienteMock));

            clienteService.removerPlaca("abc1234");

            assertEquals(1, clienteMock.getPlacas().size());
            assertFalse(clienteMock.getPlacas().contains("ABC1234"));
            verify(clienteRepository).save(clienteMock);
        }
    }

    @Test
    public void testAtualizarPlaca() {
        Usuario usuario = mock(Usuario.class);
        when(usuario.getId()).thenReturn(CLIENTE_ID);

        try (MockedStatic<SecurityUtils> mocked = mockStatic(SecurityUtils.class)) {
            mocked.when(SecurityUtils::getCurrentUsuario).thenReturn(usuario);
            when(clienteRepository.findById(CLIENTE_ID)).thenReturn(Optional.of(clienteMock));

            clienteService.atualizarPlaca("abc1234", "nova9999");

            assertEquals(2, clienteMock.getPlacas().size());
            assertTrue(clienteMock.getPlacas().contains("NOVA9999"));
            assertFalse(clienteMock.getPlacas().contains("ABC1234"));
            verify(clienteRepository).save(clienteMock);
        }
    }

    @Test
    public void testAtualizarPlaca_NaoEncontrada() {
        Usuario usuario = mock(Usuario.class);
        when(usuario.getId()).thenReturn(CLIENTE_ID);

        try (MockedStatic<SecurityUtils> mocked = mockStatic(SecurityUtils.class)) {
            mocked.when(SecurityUtils::getCurrentUsuario).thenReturn(usuario);
            when(clienteRepository.findById(CLIENTE_ID)).thenReturn(Optional.of(clienteMock));

            clienteService.atualizarPlaca("inexistente", "nova123");

            assertEquals(2, clienteMock.getPlacas().size());
            verify(clienteRepository, never()).save(any());
        }
    }
}
