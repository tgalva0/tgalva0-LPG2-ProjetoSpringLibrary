package com.library.project.controller;

import com.library.project.dto.AdminUsuarioCreateDTO;
import com.library.project.dto.UsuarioDTO;
import com.library.project.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @Autowired
    private UsuarioService usuarioService;

    // --- ENDPOINTS DE GESTÃO DE USUÁRIOS ---

    @GetMapping("/usuarios")
    public ResponseEntity<List<UsuarioDTO>> listarUsuarios() {
        return ResponseEntity.ok(usuarioService.listarTodos());
    }

    @PostMapping("/usuarios")
    public ResponseEntity<UsuarioDTO> criarUsuario(@Valid @RequestBody AdminUsuarioCreateDTO dto) {
        UsuarioDTO novoUsuario = usuarioService.adminCriarUsuario(dto);
        return new ResponseEntity<>(novoUsuario, HttpStatus.CREATED);
    }

    @DeleteMapping("/usuarios/{id}")
    public ResponseEntity<Void> deletarUsuario(@PathVariable Long id) {
        usuarioService.deletarUsuario(id);
        return ResponseEntity.noContent().build();
    }
}