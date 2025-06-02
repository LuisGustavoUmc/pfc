package br.com.findpark.auth.jwt;

import br.com.findpark.entities.Usuario;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Service
public class TokenService {

    // Segredo usado para assinar os tokens JWT, configurado no application.properties
    @Value("${api.security.token.secret}")
    private String secret;

    // Emissor do token, uma string fixa para identificar a origem do token
    private static final String issuer = "api-auth";

    /**
     * Gera um token de acesso JWT para o usuário com validade padrão (1 hora).
     * @param usuario O usuário para quem o token será gerado.
     * @return O token JWT como string.
     */
    public String generateAccessToken(Usuario usuario) {
        try {
            // Cria algoritmo HMAC256 com o segredo para assinar o token
            Algorithm algorithm = Algorithm.HMAC256(secret);

            return JWT.create()
                    .withIssuer(issuer)                   // Define o emissor do token
                    .withSubject(usuario.getEmail())     // Define o "subject" do token (usuário)
                    .withExpiresAt(this.generateExpirationDate(0, 60L)) // Define a expiração para 1 hora (60 minutos)
                    .sign(algorithm);                    // Assina o token com o algoritmo
        } catch (JWTCreationException exception) {
            // Caso ocorra erro na criação do token, lança exceção runtime
            throw new RuntimeException("Erro ao gerar token", exception);
        }
    }

    /**
     * Gera um token JWT para o usuário com validade customizada.
     * @param usuario O usuário.
     * @param plusHours Horas a adicionar para expiração.
     * @param minutes Minutos a adicionar para expiração.
     * @return Token JWT.
     */
    public String generateAccessToken(Usuario usuario, int plusHours, long minutes) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);

            return JWT.create()
                    .withIssuer(issuer)
                    .withSubject(usuario.getEmail())
                    .withExpiresAt(this.generateExpirationDate(plusHours, minutes)) // Expiração customizada
                    .sign(algorithm);
        } catch (JWTCreationException exception) {
            throw new RuntimeException("Erro ao gerar token", exception);
        }
    }

    /**
     * Valida o token JWT e retorna o subject (email do usuário) se válido.
     * @param token Token JWT recebido.
     * @return Email do usuário extraído do token.
     */
    public String validateToken(String token) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);

            return JWT.require(algorithm)
                    .withIssuer(issuer)    // Verifica se o token foi emitido por este emissor
                    .build()
                    .verify(token)         // Verifica a assinatura e validade do token
                    .getSubject();         // Retorna o subject (email do usuário)
        } catch (JWTVerificationException exception) {
            // Token inválido ou expirado
            throw new RuntimeException("Token expirado ou inválido", exception);
        }
    }

    /**
     * Gera a data de expiração do token adicionando horas e minutos ao horário atual.
     * Considera o fuso horário -03:00 (ex: horário de Brasília).
     * @param plusHours Quantidade de horas a adicionar.
     * @param minutes Quantidade de minutos a adicionar.
     * @return Instant representando a data e hora da expiração.
     */
    private Instant generateExpirationDate(int plusHours, Long minutes) {
        return LocalDateTime.now()
                .plusHours(plusHours)  // Adiciona as horas
                .plusMinutes(minutes)  // Adiciona os minutos
                .toInstant(ZoneOffset.of("-03:00")); // Converte para Instant com offset de -3 horas
    }
}
