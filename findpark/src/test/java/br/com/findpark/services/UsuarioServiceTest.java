package br.com.findpark.services;

import br.com.findpark.dtos.usuarios.AtualizarUsuarioDto;
import br.com.findpark.dtos.usuarios.RegistrarUsuarioDto;
import br.com.findpark.email.EmailService;
import br.com.findpark.email.EmailTemplate;
import br.com.findpark.entities.Usuario;
import br.com.findpark.entities.enums.usuarios.UserRole;
import br.com.findpark.entities.enums.usuarios.Validade;
import br.com.findpark.exceptions.usuario.RecursoJaExisteException;
import br.com.findpark.repositories.UsuarioRepository;
import br.com.findpark.service.UsuarioService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.thymeleaf.context.Context;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceTest {

    @InjectMocks
    private UsuarioService usuarioService;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private EmailService emailService;

    private RegistrarUsuarioDto registrarUsuarioDto;

    @BeforeEach
    void setup() {
        registrarUsuarioDto = new RegistrarUsuarioDto(
                "Fulano",
                "fulano@email.com",
                "senha123",
                "11999999999",
                UserRole.CLIENTE
        );
    }

    @Test
    void criarUsuario_comSucesso() {
        when(usuarioRepository.findByEmail(registrarUsuarioDto.email())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(registrarUsuarioDto.senha())).thenReturn("senhaHash");
        when(usuarioRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        Usuario usuario = usuarioService.criarUsuario(registrarUsuarioDto);

        assertNotNull(usuario);
        assertEquals("Fulano", usuario.getNome());
        assertEquals("fulano@email.com", usuario.getEmail());
        assertEquals("senhaHash", usuario.getSenha());
        assertEquals(Validade.PENDENTE, usuario.getValidade());
        assertNotNull(usuario.getTokenConfirmacao());

        verify(usuarioRepository).save(usuario);
        verify(emailService).enviarEmail(
                contains("Confirmação de Cadastro"),
                eq(usuario.getEmail()),
                eq(EmailTemplate.VALIDAR_USUARIO),
                any(Context.class),
                eq(Optional.empty())
        );
    }

    @Test
    void criarUsuario_emailJaRegistrado_lançaExcecao() {
        when(usuarioRepository.findByEmail(registrarUsuarioDto.email()))
                .thenReturn(Optional.of(new Usuario()));

        assertThrows(RecursoJaExisteException.class, () -> {
            usuarioService.criarUsuario(registrarUsuarioDto);
        });
    }

    @Test
    void buscarPorId_existente() {
        Usuario usuario = new Usuario();
        usuario.setId("id1");
        when(usuarioRepository.findById("id1")).thenReturn(Optional.of(usuario));

        Optional<Usuario> resultado = usuarioService.buscarPorId("id1");

        assertTrue(resultado.isPresent());
        assertEquals("id1", resultado.get().getId());
    }

    @Test
    void buscarPorId_inexistente() {
        when(usuarioRepository.findById("id2")).thenReturn(Optional.empty());

        Optional<Usuario> resultado = usuarioService.buscarPorId("id2");

        assertFalse(resultado.isPresent());
    }

    @Test
    void updatePassword_deveAtualizarSenha() {
        Usuario usuario = new Usuario();
        usuario.setSenha("oldPassword");

        when(passwordEncoder.encode("novaSenha")).thenReturn("hashNovaSenha");

        usuarioService.updatePassword(usuario, "novaSenha");

        assertEquals("hashNovaSenha", usuario.getSenha());
        verify(usuarioRepository).save(usuario);
    }

    @Test
    void excluirUsuariosNaoValidados_deveExcluirUsuariosPendentesAntigos() {
        Usuario u1 = new Usuario();
        u1.setId("1");
        Usuario u2 = new Usuario();
        u2.setId("2");
        List<Usuario> pendentes = List.of(u1, u2);

        when(usuarioRepository.findByValidadeAndCriadoEmBefore(eq(Validade.PENDENTE), any(LocalDateTime.class)))
                .thenReturn(pendentes);

        usuarioService.excluirUsuariosNaoValidados();

        verify(usuarioRepository).deleteAll(pendentes);
    }
}
