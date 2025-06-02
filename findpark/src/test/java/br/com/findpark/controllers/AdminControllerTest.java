package br.com.findpark.controllers;

import br.com.findpark.dtos.admin.DashboardAdminDto;
import br.com.findpark.service.AdminService;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

public class AdminControllerTest {

    @Mock
    private AdminService adminService;

    @InjectMocks
    private AdminController adminController;

    public AdminControllerTest() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getDashboardData_retornaDadosCorretos() {
        // Dados simulados para o dashboard
        DashboardAdminDto dashboardDto = new DashboardAdminDto(
                100L,  // totalUsuarios
                70L,   // totalClientes
                30L,   // totalProprietarios
                50L,   // totalEstacionamentos
                200L,  // totalVagas
                150L   // totalReservas
        );

        when(adminService.obterDadosDashboard()).thenReturn(dashboardDto);

        ResponseEntity<DashboardAdminDto> response = adminController.getDashboardData();

        assertEquals(200, response.getStatusCodeValue());

        assertEquals(dashboardDto, response.getBody());
    }
}

