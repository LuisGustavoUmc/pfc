package br.com.findpark.dtos.usuarios;

import br.com.findpark.entities.enums.usuarios.UserRole;

public record RegistrarUsuarioDto(
        String nome,
        String email,
        String senha,
        String telefone,
        UserRole role
) {
}
