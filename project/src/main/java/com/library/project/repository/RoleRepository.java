package com.library.project.repository;

import com.library.project.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {

    // Exemplo de Query Method:
    // O Spring entende esse nome e cria a consulta: "SELECT r FROM Role r WHERE r.nome = ?1"
    Optional<Role> findByNome(String nome);
}