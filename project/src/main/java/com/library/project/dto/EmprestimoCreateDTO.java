package com.library.project.dto;

import jakarta.validation.constraints.NotNull;

public class EmprestimoCreateDTO {

    @NotNull(message = "ID do livro é obrigatório")
    private Long livroId;

    public Long getLivroId() { return livroId; }
    public void setLivroId(Long livroId) { this.livroId = livroId; }
}