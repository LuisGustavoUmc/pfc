package br.com.findpark.exceptions.usuario;

import br.com.findpark.entities.enums.usuarios.Validade;
import org.springframework.http.HttpStatus;

public class UsuarioValidacaoException extends RuntimeException {
    private HttpStatus status;

    public UsuarioValidacaoException(String message, Validade validade, HttpStatus status) {
        super("Validation: " + validade + ": " + message);
        this.status = status;
    }

    public HttpStatus getStatus() {
        return status;
    }
}
