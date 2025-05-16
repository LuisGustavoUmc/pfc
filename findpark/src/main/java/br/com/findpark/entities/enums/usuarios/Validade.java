package br.com.findpark.entities.enums.usuarios;

public enum Validade {
    PENDENTE("Pendente de validação"),
    VALIDADO("Validado com sucesso"),
    RECUSADO("Validação recusada");

    private final String descricao;

    Validade(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }

    @Override
    public String toString() {
        return descricao;
    }
}
