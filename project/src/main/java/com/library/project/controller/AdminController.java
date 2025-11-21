package com.library.project.controller;

import com.library.project.dto.AdminDashboardStatsDTO;
import com.library.project.dto.AdminUsuarioCreateDTO;
import com.library.project.dto.AdminUsuarioUpdateDTO;
import com.library.project.dto.UsuarioDTO;
import com.library.project.model.Usuario;
import com.library.project.service.DashboardService;
import com.library.project.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private DashboardService dashboardService;

    @GetMapping("/dashboard/stats")
    public ResponseEntity<AdminDashboardStatsDTO> getAdminStats() {
        return ResponseEntity.ok(dashboardService.getAdminDashboard());
    }

    @GetMapping("/usuarios")
    public ResponseEntity<List<UsuarioDTO>> listarUsuarios() {
        return ResponseEntity.ok(usuarioService.listarTodos());
    }

    @PostMapping("/usuarios")
    public ResponseEntity<UsuarioDTO> criarUsuario(@Valid @RequestBody AdminUsuarioCreateDTO dto) {
        UsuarioDTO novoUsuario = usuarioService.adminCriarUsuario(dto);
        return new ResponseEntity<>(novoUsuario, HttpStatus.CREATED);
    }

    @GetMapping("/usuarios/search")
    public ResponseEntity<List<UsuarioDTO>> buscarUsuarios(
            @RequestParam("q") String termo,
            @AuthenticationPrincipal Usuario adminLogado) {
        List<UsuarioDTO> resultado = usuarioService.buscarPorUsername(termo, adminLogado);
        return ResponseEntity.ok(resultado);
    }

    // --- ENDPOINT NOVO DE ATUALIZAÇÃO ---
    @PutMapping("/usuarios/{id}")
    public ResponseEntity<UsuarioDTO> atualizarUsuario(
            @PathVariable Long id,
            @Valid @RequestBody AdminUsuarioUpdateDTO dto) {
        UsuarioDTO usuarioAtualizado = usuarioService.adminAtualizarUsuario(id, dto);
        return ResponseEntity.ok(usuarioAtualizado);
    }

    @DeleteMapping("/usuarios/{id}")
    public ResponseEntity<Void> deletarUsuario(@PathVariable Long id) {
        usuarioService.deletarUsuario(id);
        return ResponseEntity.noContent().build();
    }
}