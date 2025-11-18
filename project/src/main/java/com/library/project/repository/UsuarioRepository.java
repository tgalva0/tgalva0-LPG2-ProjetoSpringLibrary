package com.library.project.repository;


import com.library.project.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    // Este método será essencial para o Spring Security (Fase 4)
    // "SELECT u FROM Usuario u WHERE u.username = ?1"
    Optional<Usuario> findByUsername(String username);
}
