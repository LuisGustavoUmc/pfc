package br.com.findpark.controllers;

import br.com.findpark.dtos.admin.DashboardAdminDto;
import br.com.findpark.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @Autowired
    private AdminService adminService;

    @GetMapping("/dashboard")
    public ResponseEntity<DashboardAdminDto> getDashboardData() {
        return ResponseEntity.ok(adminService.obterDadosDashboard());
    }
}
