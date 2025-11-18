package com.library.project.repository;

import com.library.project.model.Role;
import com.library.project.model.Usuario;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;


import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class UsuarioRepositoryTest {


    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Test
    public void deveSalvarUsuarioComRoleCorretamente() {
        Role roleAdmin = new Role();
        roleAdmin.setNome("ROLE_ADMIN");
        roleRepository.save(roleAdmin);

        Usuario usuario = new Usuario();
        usuario.setUsername("admin.user");
        usuario.setPassword("senha123");
        usuario.setNomeCompleto("Administrador do Sistema");
        usuario.addRole(roleAdmin);
        Usuario usuarioSalvo = usuarioRepository.save(usuario);
        assertThat(usuarioSalvo).isNotNull();
        assertThat(usuarioSalvo.getId()).isGreaterThan(0);
        assertThat(usuarioSalvo.getRoles()).isNotEmpty();
        assertThat(usuarioSalvo.getRoles().iterator().next().getNome()).isEqualTo("ROLE_ADMIN");
    }

    @Test
    public void deveEncontrarUsuarioPeloUsername() {
        Usuario usuario = new Usuario();
        usuario.setUsername("usuario.busca");
        usuario.setPassword("senha123");
        usuario.setNomeCompleto("Usuario de Teste de Busca");
        usuarioRepository.save(usuario);
        Optional<Usuario> usuarioEncontrado = usuarioRepository.findByUsername("usuario.busca");
        assertThat(usuarioEncontrado).isPresent();
        assertThat(usuarioEncontrado.get().getNomeCompleto()).isEqualTo("Usuario de Teste de Busca");
    }

    @Test
    public void naoDeveEncontrarUsuarioPorUsernameInexistente() {
        Optional<Usuario> usuarioEncontrado = usuarioRepository.findByUsername("usuario.fantasma");
        assertThat(usuarioEncontrado).isNotPresent();
    }
}