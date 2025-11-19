package com.library.project.repository;

import com.library.project.model.Livro;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LivroRepository extends JpaRepository<Livro, Long> {

    // "SELECT l FROM Livro l WHERE l.titulo LIKE %?1%"
    List<Livro> findByTituloContaining(String titulo);

    // "SELECT l FROM Livro l WHERE l.autor LIKE %?1%"
    List<Livro> findByAutorContaining(String autor);

    // "SELECT l FROM Livro l WHERE l.isbn = ?1"
    Optional<Livro> findByIsbn(String isbn);

    List<Livro> findByTituloContainingIgnoreCaseOrAutorContainingIgnoreCase(String termoTitulo, String termoAutor);
}