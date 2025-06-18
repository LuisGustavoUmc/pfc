package br.com.findpark.entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Endereco {
    private String cep;
    private String logradouro;
    private String numero;
    private String complemento;
    private String unidade;
    private String bairro;
    private String localidade;
    private String uf;
    private String estado;
    private String regiao;
    private String ibge;
    private String gia;
    private String ddd;
    private String siafi;

    public Endereco(String s, String endereçoNãoDisponível, String s1, String s2, String s3, String s4, String s5, String s6, String s7, String s8, String s9, String s10, String s11, String s12, String s13) {
    }
}

