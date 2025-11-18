package com.library.project.dto;

import java.util.Set;
import java.util.stream.Collectors;
import com.library.project.model.Usuario;

public class UsuarioDTO {

    private Long id;
    private String username;
    private String nomeCompleto;
    private Set<String> roles; // Apenas os nomes dos papéis

    public UsuarioDTO(Usuario entity) {
        this.id = entity.getId();
        this.username = entity.getUsername();
        this.nomeCompleto = entity.getNomeCompleto();
        this.roles = entity.getRoles().stream()
                .map(role -> role.getNome())
                .collect(Collectors.toSet());
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getNomeCompleto() { return nomeCompleto; }
    public void setNomeCompleto(String nomeCompleto) { this.nomeCompleto = nomeCompleto; }
    public Set<String> getRoles() { return roles; }
    public void setRoles(Set<String> roles) { this.roles = roles; }
}