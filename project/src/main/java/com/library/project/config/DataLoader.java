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
            // 1. Create Roles
            Role roleAdmin = new Role(); roleAdmin.setNome("ROLE_ADMIN");
            Role roleComum = new Role(); roleComum.setNome("ROLE_COMUM");
            roleRepository.saveAll(Arrays.asList(roleAdmin, roleComum));

            // 2. Create Test User (Student)
            Usuario user = new Usuario();
            user.setNomeCompleto("Aluno Exemplo");
            user.setUsername("aluno");
            user.setPassword(passwordEncoder.encode("123456")); // Senha hash
            user.addRole(roleComum);

            // 3. --- NOVO USUÁRIO ADMIN ---
            Usuario admin = new Usuario();
            admin.setNomeCompleto("Admin da Biblioteca");
            admin.setUsername("admin");
            admin.setPassword(passwordEncoder.encode("admin123")); // Senha hash
            admin.addRole(roleAdmin); // <- Papel de Admin
            admin.addRole(roleComum); // <- Também é um usuário comum

            // 4. Save both users
            usuarioRepository.saveAll(Arrays.asList(user, admin));

            // 5. Create Books (as before)
            Livro livro1 = new Livro();
            livro1.setTitulo("Clean Code");
            livro1.setAutor("Robert C. Martin");
            livro1.setIsbn("9780132350884");
            livro1.setQuantidadeDisponivel(3);

            Livro livro2 = new Livro();
            livro2.setTitulo("Arquitetura Limpa");
            livro2.setAutor("Robert C. Martin");
            livro2.setIsbn("9780134494166");
            livro2.setQuantidadeDisponivel(1); // Mudei para 1 para facilitar testes

            livroRepository.saveAll(Arrays.asList(livro1, livro2));

            System.out.println("--- DADOS DE TESTE CARREGADOS (com Admin) ---");
        };
    }
}