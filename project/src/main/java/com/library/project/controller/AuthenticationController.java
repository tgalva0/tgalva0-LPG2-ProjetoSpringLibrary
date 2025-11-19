package com.library.project.controller;

import com.library.project.dto.LoginDTO;
import com.library.project.dto.LoginResponseDTO; // Importe o DTO de Resposta
import com.library.project.dto.TokenDTO;
import com.library.project.model.Usuario;
import com.library.project.security.TokenService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set; // Importe o Set
import java.util.stream.Collectors; // Importe o Collectors

@RestController
@RequestMapping("/api/auth")
public class AuthenticationController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private TokenService tokenService;

    @PostMapping("/login")
    // O tipo de retorno agora é o DTO de Resposta
    public ResponseEntity<LoginResponseDTO> login(@RequestBody @Valid LoginDTO loginDTO) {

        var usernamePassword = new UsernamePasswordAuthenticationToken(
                loginDTO.username(),
                loginDTO.password()
        );

        // Esta é a primeira variável 'auth'
        Authentication auth = authenticationManager.authenticate(usernamePassword);

        Usuario usuario = (Usuario) auth.getPrincipal();
        String token = tokenService.gerarToken(usuario);

        // Pega os nomes dos papéis (ex: "ROLE_ADMIN")
        Set<String> roles = usuario.getAuthorities().stream()
                // --- AQUI ESTÁ A CORREÇÃO ---
                // Renomeamos 'auth' para 'authority' para evitar o conflito
                .map(authority -> authority.getAuthority())
                .collect(Collectors.toSet());

        // Retorna o Token, Nome E os Papéis
        return ResponseEntity.ok(new LoginResponseDTO(token, usuario.getNomeCompleto(), roles));
    }
}