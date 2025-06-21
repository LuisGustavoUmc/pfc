package br.com.findpark.exceptions.estacionamento;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class EstacionamentoComReservaAtivaException extends RuntimeException {

    public EstacionamentoComReservaAtivaException(String msg) {
        super(msg);
    }
}
