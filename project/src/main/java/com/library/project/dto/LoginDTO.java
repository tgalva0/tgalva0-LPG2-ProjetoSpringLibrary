package com.library.project.dto;
import jakarta.validation.constraints.NotEmpty;

public record LoginDTO(
        @NotEmpty(message = "Username não pode ser vazio")
        String username,

        @NotEmpty(message = "Senha não pode ser vazia")
        String password
) {
}