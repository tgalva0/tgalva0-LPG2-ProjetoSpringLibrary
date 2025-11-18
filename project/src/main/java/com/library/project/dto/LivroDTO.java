package com.library.project.dto;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
public class LivroDTO {

    private Long id;

    @NotEmpty(message = "Título não pode ser vazio")
    @Size(min = 3, message = "Título deve ter no mínimo 3 caracteres")
    private String titulo;

    @NotEmpty(message = "Autor não pode ser vazio")
    private String autor;

    @NotEmpty(message = "ISBN não pode ser vazio")
    @Size(min = 10, max = 13, message = "ISBN deve ter entre 10 e 13 caracteres")
    private String isbn;

    @NotNull(message = "Quantidade não pode ser nula")
    @PositiveOrZero(message = "Quantidade deve ser 0 ou maior")
    private int quantidadeDisponivel;

    public LivroDTO() {}

    public LivroDTO(Long id, String titulo, String autor, String isbn, int quantidadeDisponivel) {
        this.id = id;
        this.titulo = titulo;
        this.autor = autor;
        this.isbn = isbn;
        this.quantidadeDisponivel = quantidadeDisponivel;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }
    public String getAutor() { return autor; }
    public void setAutor(String autor) { this.autor = autor; }
    public String getIsbn() { return isbn; }
    public void setIsbn(String isbn) { this.isbn = isbn; }
    public int getQuantidadeDisponivel() { return quantidadeDisponivel; }
    public void setQuantidadeDisponivel(int quantidadeDisponivel) { this.quantidadeDisponivel = quantidadeDisponivel; }
}