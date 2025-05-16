package br.com.findpark.exceptions.usuario;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class RecursoJaExisteException extends RuntimeException {

    public RecursoJaExisteException(String msg) {
        super(msg);
    }
}
