package com.library.project.config;

import com.library.project.model.Livro;
import com.library.project.model.Role;
import com.library.project.model.Usuario;
import com.library.project.repository.LivroRepository;
import com.library.project.repository.RoleRepository;
import com.library.project.repository.UsuarioRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Arrays;

@Configuration
public class DataLoader {

    @Bean
    CommandLineRunner baseDeDados(UsuarioRepository usuarioRepository,
                                  RoleRepository roleRepository,
                                  LivroRepository livroRepository,
                                  PasswordEncoder passwordEncoder) {
        return args -> {
            Role roleAdmin = new Role(); roleAdmin.setNome("ROLE_ADMIN");
            Role roleComum = new Role(); roleComum.setNome("ROLE_COMUM");
            roleRepository.saveAll(Arrays.asList(roleAdmin, roleComum));

            Usuario user = new Usuario();
            user.setNomeCompleto("Aluno Exemplo");
            user.setUsername("aluno");
            user.setPassword(passwordEncoder.encode("123456"));
            user.addRole(roleComum);
            usuarioRepository.save(user);

            Livro livro1 = new Livro();
            livro1.setTitulo("Clean Code");
            livro1.setAutor("Robert C. Martin");
            livro1.setIsbn("9780132350884");
            livro1.setQuantidadeDisponivel(3);

            Livro livro2 = new Livro();
            livro2.setTitulo("Arquitetura Limpa");
            livro2.setAutor("Robert C. Martin");
            livro2.setIsbn("9780134494166");
            livro2.setQuantidadeDisponivel(0);

            livroRepository.saveAll(Arrays.asList(livro1, livro2));

            System.out.println("--- DADOS DE TESTE CARREGADOS ---");
        };
    }
}
