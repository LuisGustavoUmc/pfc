package br.com.findpark.dtos.admin;

public record DashboardAdminDto(
        long totalUsuarios,
        long totalClientes,
        long totalProprietarios,
        long totalEstacionamentos,
        long totalVagas,
        long totalReservas
) {
}
