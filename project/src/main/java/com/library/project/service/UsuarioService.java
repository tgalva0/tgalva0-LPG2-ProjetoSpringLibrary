package com.library.project.service;

import com.library.project.dto.UsuarioCreateDTO;
import com.library.project.dto.UsuarioDTO;
import com.library.project.model.Role;
import com.library.project.model.Usuario;
import com.library.project.repository.RoleRepository;
import com.library.project.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder; // Injetado do AppConfig

    @Autowired
    public UsuarioService(UsuarioRepository usuarioRepository,
                          RoleRepository roleRepository,
                          PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional // Garante que tudo execute dentro de uma transação
    public UsuarioDTO registrarUsuario(UsuarioCreateDTO dto) {

        // --- Lógica de Negócio ---

        // 1. Verificar se o username já existe
        usuarioRepository.findByUsername(dto.getUsername()).ifPresent(u -> {
            throw new RuntimeException("Username já está em uso.");
        });

        // 2. Buscar o papel "ROLE_COMUM"
        // (Query Method que criamos no RoleRepository)
        Role roleComum = roleRepository.findByNome("ROLE_COMUM")
                .orElseGet(() -> {
                    // Se não existir, cria e salva
                    Role newRole = new Role();
                    newRole.setNome("ROLE_COMUM");
                    return roleRepository.save(newRole);
                });

        // 3. Criar a nova entidade Usuario
        Usuario novoUsuario = new Usuario();
        novoUsuario.setUsername(dto.getUsername());
        novoUsuario.setNomeCompleto(dto.getNomeCompleto());

        // 4. Codificar a senha (NUNCA SALVAR TEXTO PURO)
        novoUsuario.setPassword(passwordEncoder.encode(dto.getPassword()));

        // 5. Adicionar o papel
        novoUsuario.addRole(roleComum);

        // 6. Salvar no banco
        Usuario usuarioSalvo = usuarioRepository.save(novoUsuario);

        // 7. Retornar o DTO (sem a senha)
        return new UsuarioDTO(usuarioSalvo);
    }

    // MÉTODO NOVO (retorna DTO para o Controller)
    @Transactional(readOnly = true) // readOnly = true -> otimiza consultas que não alteram dados
    public UsuarioDTO buscarPorId(Long id) {
        Usuario entidade = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado. ID: " + id));
        return new UsuarioDTO(entidade);
    }

    // MÉTODO NOVO (retorna a Entidade para outros Serviços)
    // Este será usado internamente pelo EmprestimoService
    protected Usuario buscarEntidadePorId(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado. ID: " + id));
    }
}