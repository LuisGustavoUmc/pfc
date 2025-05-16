package br.com.findpark.entities;

import br.com.findpark.entities.enums.usuarios.UserRole;
import br.com.findpark.entities.enums.usuarios.Validade;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "usuarios")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Usuario {

    @Id
    private String id;
    private String nome;
    private String email;
    private String senha;
    private String telefone;
    private UserRole role;
    private Validade validade;
    private LocalDateTime criadoEm;
    private String tokenConfirmacao;
}
