package br.com.findpark.security;

import br.com.findpark.auth.jwt.TokenService;
import br.com.findpark.entities.Usuario;
import br.com.findpark.exceptions.RespostaException;
import br.com.findpark.exceptions.usuario.RecursoNaoEncontradoException;
import br.com.findpark.repositories.UsuarioRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;
import java.util.Date;

@Component
public class SecurityFilter extends OncePerRequestFilter {

    @Autowired
    private TokenService tokenService;

    @Autowired
    private UsuarioRepository usuarioRepository;

    private String recuperarToken(HttpServletRequest request) {
        var authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) return null;
        return authHeader.replace("Bearer ", "");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        var token = recuperarToken(request);

        if (token != null) {
            try {
                var login = tokenService.validateToken(token);

                Usuario usuario = usuarioRepository.findByEmail(login.toLowerCase())
                        .orElseThrow(() -> new RecursoNaoEncontradoException("Usuário não encontrado"));

                // Converte a role do enum para o formato Spring Security: ROLE_<NOME>
                var role = "ROLE_" + usuario.getRole().name().toUpperCase();
                var autorizacoes = Collections.singletonList(new SimpleGrantedAuthority(role));

                var autenticacao = new UsernamePasswordAuthenticationToken(usuario, null, autorizacoes);
                SecurityContextHolder.getContext().setAuthentication(autenticacao);

            } catch (Exception e) {
                var erroResposta = new RespostaException(new Date(), e.getMessage(), request.getRequestURI());

                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.setContentType(MediaType.APPLICATION_JSON_VALUE);

                var objectMapper = new ObjectMapper();
                var respostaJson = objectMapper.writeValueAsString(erroResposta);

                response.getWriter().write(respostaJson);
                return;
            }
        }

        filterChain.doFilter(request, response);
    }
}
