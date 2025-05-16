package br.com.findpark.entities.enums.usuarios;

public enum UserRole {
    ADMIN("admin"),
    PROPRIETARIO("proprietario"),
    CLIENTE("cliente");

    private String role;

    UserRole(String role){
        this.role = role;
    }

    public String getRole(){
        return role;
    }
}
