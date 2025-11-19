package com.library.project.repository;

import com.library.project.model.Emprestimo;
import com.library.project.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EmprestimoRepository extends JpaRepository<Emprestimo, Long> {

    // Busca todos os empréstimos de um usuário específico
    // "SELECT e FROM Emprestimo e WHERE e.usuario = ?1"
    List<Emprestimo> findByUsuario(Usuario usuario);

    List<Emprestimo> findByLivroId(Long livroId);

    // Busca empréstimos de um usuário que ainda não foram devolvidos
    // "SELECT e FROM Emprestimo e WHERE e.usuario = ?1 AND e.dataDevolucaoEfetiva IS NULL"
    List<Emprestimo> findByUsuarioAndDataDevolucaoEfetivaIsNull(Usuario usuario);

    // Busca empréstimos de um livro que ainda não foram devolvidos (para saber se está emprestado)
    // "SELECT e FROM Emprestimo e WHERE e.livro.id = ?1 AND e.dataDevolucaoEfetiva IS NULL"
    List<Emprestimo> findByLivroIdAndDataDevolucaoEfetivaIsNull(Long livroId);
}