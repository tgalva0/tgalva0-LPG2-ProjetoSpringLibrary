package com.library.project.dto;

// DTO para as estatísticas do Admin
public class AdminDashboardStatsDTO {

    private long totalUsuarios;
    private long totalLivros; // Títulos únicos
    private long totalEmprestimosAtivos;

    // Construtor, Getters e Setters
    public AdminDashboardStatsDTO(long totalUsuarios, long totalLivros, long totalEmprestimosAtivos) {
        this.totalUsuarios = totalUsuarios;
        this.totalLivros = totalLivros;
        this.totalEmprestimosAtivos = totalEmprestimosAtivos;
    }

    public long getTotalUsuarios() { return totalUsuarios; }
    public void setTotalUsuarios(long totalUsuarios) { this.totalUsuarios = totalUsuarios; }
    public long getTotalLivros() { return totalLivros; }
    public void setTotalLivros(long totalLivros) { this.totalLivros = totalLivros; }
    public long getTotalEmprestimosAtivos() { return totalEmprestimosAtivos; }
    public void setTotalEmprestimosAtivos(long totalEmprestimosAtivos) { this.totalEmprestimosAtivos = totalEmprestimosAtivos; }
}