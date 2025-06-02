package br.com.findpark.services;

import br.com.findpark.dtos.admin.DashboardAdminDto;
import br.com.findpark.entities.enums.usuarios.UserRole;
import br.com.findpark.repositories.EstacionamentoRepository;
import br.com.findpark.repositories.ReservaRepository;
import br.com.findpark.repositories.UsuarioRepository;
import br.com.findpark.repositories.VagaRepository;
import br.com.findpark.service.AdminService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

public class AdminServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private EstacionamentoRepository estacionamentoRepository;

    @Mock
    private VagaRepository vagaRepository;

    @Mock
    private ReservaRepository reservaRepository;

    @InjectMocks
    private AdminService adminService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testObterDadosDashboard() {
        when(usuarioRepository.count()).thenReturn(100L);
        when(usuarioRepository.countByRole(UserRole.CLIENTE)).thenReturn(60L);
        when(usuarioRepository.countByRole(UserRole.PROPRIETARIO)).thenReturn(40L);
        when(estacionamentoRepository.count()).thenReturn(10L);
        when(vagaRepository.count()).thenReturn(50L);
        when(reservaRepository.count()).thenReturn(200L);

        DashboardAdminDto dto = adminService.obterDadosDashboard();

        assertEquals(100L, dto.totalUsuarios());
        assertEquals(60L, dto.totalClientes());
        assertEquals(40L, dto.totalProprietarios());
        assertEquals(10L, dto.totalEstacionamentos());
        assertEquals(50L, dto.totalVagas());
        assertEquals(200L, dto.totalReservas());

        verify(usuarioRepository).count();
        verify(usuarioRepository).countByRole(UserRole.CLIENTE);
        verify(usuarioRepository).countByRole(UserRole.PROPRIETARIO);
        verify(estacionamentoRepository).count();
        verify(vagaRepository).count();
        verify(reservaRepository).count();
    }
}
