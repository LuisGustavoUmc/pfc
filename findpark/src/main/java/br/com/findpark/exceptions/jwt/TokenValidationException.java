package br.com.findpark.exceptions.jwt;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.UNAUTHORIZED)
public class TokenValidationException extends RuntimeException {

    public TokenValidationException(String msg) {
        super(msg);
    }
}
