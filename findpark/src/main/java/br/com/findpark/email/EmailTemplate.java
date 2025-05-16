package br.com.findpark.email;

public enum EmailTemplate {
    RECUPERAR_SENHA("recuperar-senha.html"),
    VALIDAR_USUARIO("validar-usuario.html");

    private final String name;

    EmailTemplate(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
