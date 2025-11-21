package com.library.project.dto;

import java.util.List;

// DTO para o dashboard do usuário (com seus empréstimos)
public class UserDashboardDTO {

    private List<EmprestimoDTO> emprestimosAtivos;

    // Construtor, Getters e Setters
    public UserDashboardDTO(List<EmprestimoDTO> emprestimosAtivos) {
        this.emprestimosAtivos = emprestimosAtivos;
    }

    public List<EmprestimoDTO> getEmprestimosAtivos() { return emprestimosAtivos; }
    public void setEmprestimosAtivos(List<EmprestimoDTO> emprestimosAtivos) { this.emprestimosAtivos = emprestimosAtivos; }
}