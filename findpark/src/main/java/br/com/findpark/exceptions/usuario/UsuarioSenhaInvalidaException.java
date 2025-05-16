package br.com.findpark.exceptions.usuario;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class UsuarioSenhaInvalidaException extends RuntimeException {

    public UsuarioSenhaInvalidaException() {
        super("Senha do Usuário inválida!");
    }

    public UsuarioSenhaInvalidaException(String msg) {
        super(msg);
    }
}
