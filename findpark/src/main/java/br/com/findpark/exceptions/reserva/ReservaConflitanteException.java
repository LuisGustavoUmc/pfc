package br.com.findpark.exceptions.reserva;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class ReservaConflitanteException extends RuntimeException {

    public ReservaConflitanteException(String msg) {
        super(msg);
    }
}
