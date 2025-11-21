package com.library.project.dto;

import jakarta.validation.constraints.NotEmpty;
import java.util.Set;

// DTO para ATUALIZAR um usuário (sem alterar senha)
public class AdminUsuarioUpdateDTO {

    @NotEmpty(message = "Nome Completo não pode ser vazio")
    private String nomeCompleto;

    @NotEmpty(message = "Usuário deve ter ao menos um papel")
    private Set<String> roles;

    // Getters e Setters
    public String getNomeCompleto() { return nomeCompleto; }
    public void setNomeCompleto(String nomeCompleto) { this.nomeCompleto = nomeCompleto; }
    public Set<String> getRoles() { return roles; }
    public void setRoles(Set<String> roles) { this.roles = roles; }
}