package br.com.findpark.controllers;

import br.com.findpark.entities.LogExclusao;
import br.com.findpark.service.LogExclusaoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/logs")
public class LogExclusaoController {

    @Autowired
    private LogExclusaoService service;

    @GetMapping
    public ResponseEntity<Page<LogExclusao>> listarLogs(
            @RequestParam(value = "page", defaultValue = "0") Integer page,
            @RequestParam(value = "size", defaultValue = "10") Integer size,
            @RequestParam(value = "direction", defaultValue = "desc") String direction,
            @RequestParam(value = "sort", defaultValue = "dataHora") String sort
    ) {
        Sort.Direction sortDirection = "desc".equalsIgnoreCase(direction) ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sort));

        Page<LogExclusao> resultado = service.listarLogs(pageable);
        return ResponseEntity.ok(resultado);
    }
}
