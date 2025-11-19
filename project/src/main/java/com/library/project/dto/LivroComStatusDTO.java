package com.library.project.dto;

import com.library.project.model.Livro;

// Um DTO "inteligente" que sabe sobre o status do usuário
public class LivroComStatusDTO {

    private Long id;
    private String titulo;
    private String autor;
    private String isbn;
    private int quantidadeDisponivel;
    private Long emprestimoId; // ID do empréstimo ATIVO (ou null se não houver)

    // Construtor que "mapeia" a Entidade e o status
    public LivroComStatusDTO(Livro livro, Long emprestimoId) {
        this.id = livro.getId();
        this.titulo = livro.getTitulo();
        this.autor = livro.getAutor();
        this.isbn = livro.getIsbn();
        this.quantidadeDisponivel = livro.getQuantidadeDisponivel();
        this.emprestimoId = emprestimoId;
    }

    // Gere todos os Getters
    public Long getId() { return id; }
    public String getTitulo() { return titulo; }
    public String getAutor() { return autor; }
    public String getIsbn() { return isbn; }
    public int getQuantidadeDisponivel() { return quantidadeDisponivel; }
    public Long getEmprestimoId() { return emprestimoId; }
}
