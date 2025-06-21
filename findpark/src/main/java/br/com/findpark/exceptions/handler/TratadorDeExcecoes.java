package br.com.findpark.exceptions.handler;

import br.com.findpark.exceptions.RequisicaoInvalidaException;
import br.com.findpark.exceptions.RespostaException;
import br.com.findpark.exceptions.email.EnvioEmailException;
import br.com.findpark.exceptions.estacionamento.EstacionamentoComReservaAtivaException;
import br.com.findpark.exceptions.jwt.TokenValidationException;
import br.com.findpark.exceptions.reserva.ReservaConflitanteException;
import br.com.findpark.exceptions.usuario.RecursoJaExisteException;
import br.com.findpark.exceptions.usuario.RecursoNaoEncontradoException;
import br.com.findpark.exceptions.usuario.UsuarioSenhaInvalidaException;
import br.com.findpark.exceptions.usuario.UsuarioValidacaoException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.Date;

@ControllerAdvice
@RestController
public class TratadorDeExcecoes extends ResponseEntityExceptionHandler {

    @ExceptionHandler(Exception.class)
    public final ResponseEntity<RespostaException> tratarTodasExceptions(Exception ex, WebRequest requisicao) {
        RespostaException resposta = new RespostaException(
                new Date(),
                ex.getMessage(),
                requisicao.getDescription(false));
        return new ResponseEntity<>(resposta, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(EnvioEmailException.class)
    public final ResponseEntity<RespostaException> tratarEnvioEmailException(Exception ex, WebRequest requisicao) {
        RespostaException resposta = new RespostaException(
                new Date(),
                ex.getMessage(),
                requisicao.getDescription(false));
        return new ResponseEntity<>(resposta, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(RecursoJaExisteException.class)
    public final ResponseEntity<RespostaException> tratarRecursoJaExisteException(Exception ex, WebRequest requisicao) {
        RespostaException resposta = new RespostaException(
                new Date(),
                ex.getMessage(),
                requisicao.getDescription(false));
        return new ResponseEntity<>(resposta, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(RecursoNaoEncontradoException.class)
    public final ResponseEntity<RespostaException> tratarRecursoNaoEncontradoException(Exception ex, WebRequest requisicao) {
        RespostaException resposta = new RespostaException(
                new Date(),
                ex.getMessage(),
                requisicao.getDescription(false));
        return new ResponseEntity<>(resposta, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(UsuarioSenhaInvalidaException.class)
    public final ResponseEntity<RespostaException> tratarUsuarioSenhaInvalidaoException(Exception ex, WebRequest requisicao) {
        RespostaException resposta = new RespostaException(
                new Date(),
                ex.getMessage(),
                requisicao.getDescription(false));
        return new ResponseEntity<>(resposta, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(UsuarioValidacaoException.class)
    public final ResponseEntity<RespostaException> tratarUsuarioValidacaoException(UsuarioValidacaoException ex, WebRequest requisicao) {
        RespostaException resposta = new RespostaException(
                new Date(),
                ex.getMessage(),
                requisicao.getDescription(false));
        return new ResponseEntity<>(resposta, ex.getStatus());
    }

    @ExceptionHandler(TokenValidationException.class)
    public final ResponseEntity<RespostaException> tratarTokenValidationException(Exception ex, WebRequest requisicao) {
        RespostaException resposta = new RespostaException(
                new Date(),
                ex.getMessage(),
                requisicao.getDescription(false));
        return new ResponseEntity<>(resposta, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(ReservaConflitanteException.class)
    public final ResponseEntity<RespostaException> tratarReservaConflitanteException(Exception ex, WebRequest requisicao) {
        RespostaException resposta = new RespostaException(
                new Date(),
                ex.getMessage(),
                requisicao.getDescription(false));
        return new ResponseEntity<>(resposta, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(EstacionamentoComReservaAtivaException.class)
    public final ResponseEntity<RespostaException> tratarEstacionamentoComReservaAtivaException(Exception ex, WebRequest requisicao) {
        RespostaException resposta = new RespostaException(
                new Date(),
                ex.getMessage(),
                requisicao.getDescription(false));
        return new ResponseEntity<>(resposta, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(RequisicaoInvalidaException.class)
    public final ResponseEntity<RespostaException> tratarRequisicaoInvalidaException(Exception ex, WebRequest requisicao) {
        RespostaException resposta = new RespostaException(
                new Date(),
                ex.getMessage(),
                requisicao.getDescription(false));
        return new ResponseEntity<>(resposta, HttpStatus.BAD_REQUEST);
    }
}
