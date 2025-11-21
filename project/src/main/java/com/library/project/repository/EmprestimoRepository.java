package com.library.project.repository;

import com.library.project.model.Emprestimo;
import com.library.project.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EmprestimoRepository extends JpaRepository<Emprestimo, Long> {

    List<Emprestimo> findByUsuario(Usuario usuario);

    List<Emprestimo> findByLivroId(Long livroId);

    List<Emprestimo> findByUsuarioAndDataDevolucaoEfetivaIsNull(Usuario usuario);

    List<Emprestimo> findByLivroIdAndDataDevolucaoEfetivaIsNull(Long livroId);

    long countByDataDevolucaoEfetivaIsNull();
}