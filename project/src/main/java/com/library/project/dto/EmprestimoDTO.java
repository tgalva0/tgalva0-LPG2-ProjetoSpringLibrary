package com.library.project.dto;

import com.library.project.model.Emprestimo;
import java.time.LocalDate;

public class EmprestimoDTO {

    private Long id;
    private LocalDate dataEmprestimo;
    private LocalDate dataDevolucaoPrevista;
    private LocalDate dataDevolucaoEfetiva;

    private Long usuarioId;
    private String usuarioNome;
    private Long livroId;
    private String livroTitulo;

    public EmprestimoDTO() {}

    public EmprestimoDTO(Emprestimo entidade) {
        this.id = entidade.getId();
        this.dataEmprestimo = entidade.getDataEmprestimo();
        this.dataDevolucaoPrevista = entidade.getDataDevolucaoPrevista();
        this.dataDevolucaoEfetiva = entidade.getDataDevolucaoEfetiva();
        this.usuarioId = entidade.getUsuario().getId();
        this.usuarioNome = entidade.getUsuario().getNomeCompleto();
        this.livroId = entidade.getLivro().getId();
        this.livroTitulo = entidade.getLivro().getTitulo();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getDataEmprestimo() {
        return dataEmprestimo;
    }

    public void setDataEmprestimo(LocalDate dataEmprestimo) {
        this.dataEmprestimo = dataEmprestimo;
    }

    public LocalDate getDataDevolucaoPrevista() {
        return dataDevolucaoPrevista;
    }

    public void setDataDevolucaoPrevista(LocalDate dataDevolucaoPrevista) {
        this.dataDevolucaoPrevista = dataDevolucaoPrevista;
    }

    public LocalDate getDataDevolucaoEfetiva() {
        return dataDevolucaoEfetiva;
    }

    public void setDataDevolucaoEfetiva(LocalDate dataDevolucaoEfetiva) {
        this.dataDevolucaoEfetiva = dataDevolucaoEfetiva;
    }

    public Long getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(Long usuarioId) {
        this.usuarioId = usuarioId;
    }

    public String getUsuarioNome() {
        return usuarioNome;
    }

    public void setUsuarioNome(String usuarioNome) {
        this.usuarioNome = usuarioNome;
    }

    public Long getLivroId() {
        return livroId;
    }

    public void setLivroId(Long livroId) {
        this.livroId = livroId;
    }

    public String getLivroTitulo() {
        return livroTitulo;
    }

    public void setLivroTitulo(String livroTitulo) {
        this.livroTitulo = livroTitulo;
    }
}