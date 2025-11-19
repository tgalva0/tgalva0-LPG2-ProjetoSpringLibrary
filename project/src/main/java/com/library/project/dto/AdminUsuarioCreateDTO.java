package com.library.project.dto;

import jakarta.validation.constraints.NotEmpty;
import java.util.Set;

// DTO para o admin criar um usuário
public class AdminUsuarioCreateDTO {

    @NotEmpty(message = "Username não pode ser vazio")
    private String username;

    @NotEmpty(message = "Senha não pode ser vazia")
    private String password;

    @NotEmpty(message = "Nome Completo não pode ser vazio")
    private String nomeCompleto;

    // O Admin pode definir os papéis (ex: "ROLE_ADMIN", "ROLE_COMUM")
    private Set<String> roles;

    // Getters e Setters
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getNomeCompleto() { return nomeCompleto; }
    public void setNomeCompleto(String nomeCompleto) { this.nomeCompleto = nomeCompleto; }
    public Set<String> getRoles() { return roles; }
    public void setRoles(Set<String> roles) { this.roles = roles; }
}