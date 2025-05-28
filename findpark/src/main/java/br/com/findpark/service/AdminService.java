package br.com.findpark.service;

import br.com.findpark.dtos.admin.DashboardAdminDto;
import br.com.findpark.entities.enums.usuarios.UserRole;
import br.com.findpark.repositories.EstacionamentoRepository;
import br.com.findpark.repositories.ReservaRepository;
import br.com.findpark.repositories.UsuarioRepository;
import br.com.findpark.repositories.VagaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AdminService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private EstacionamentoRepository estacionamentoRepository;

    @Autowired
    private VagaRepository vagaRepository;

    @Autowired
    private ReservaRepository reservaRepository;

    public DashboardAdminDto obterDadosDashboard() {
        long totalUsuarios = usuarioRepository.count();
        long totalClientes = usuarioRepository.countByRole(UserRole.CLIENTE);
        long totalProprietarios = usuarioRepository.countByRole(UserRole.PROPRIETARIO);

        long totalEstacionamentos = estacionamentoRepository.count();
        long totalVagas = vagaRepository.count();
        long totalReservas = reservaRepository.count();

        return new DashboardAdminDto(
                totalUsuarios,
                totalClientes,
                totalProprietarios,
                totalEstacionamentos,
                totalVagas,
                totalReservas
        );
    }
}
