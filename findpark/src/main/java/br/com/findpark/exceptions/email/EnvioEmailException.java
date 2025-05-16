package br.com.findpark.exceptions.email;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class EnvioEmailException extends RuntimeException {

    public EnvioEmailException(String msg) {
        super(msg);
    }
}
