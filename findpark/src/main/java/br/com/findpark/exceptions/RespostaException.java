package br.com.findpark.exceptions;

import java.util.Date;

public record RespostaException(Date timestamp, String message, String detail) {}
