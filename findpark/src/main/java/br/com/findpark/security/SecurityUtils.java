package br.com.findpark.security;

import br.com.findpark.entities.Usuario;
import org.springframework.security.core.context.SecurityContextHolder;

public class SecurityUtils {

    public static Usuario getCurrentUsuario() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            throw new IllegalStateException("Nenhuma autenticação encontrada.");
        }

        Object principal = authentication.getPrincipal();
        if (principal instanceof Usuario) {
            return (Usuario) principal;
        } else {
            throw new IllegalStateException("O usuário autenticado não é um Usuário");
        }
    }
}
