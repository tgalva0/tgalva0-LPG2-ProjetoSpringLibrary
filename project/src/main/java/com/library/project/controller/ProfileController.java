package com.library.project.controller;

import com.library.project.dto.PasswordChangeDTO;
import com.library.project.dto.UserDashboardDTO;
import com.library.project.model.Usuario;
import com.library.project.service.DashboardService;
import com.library.project.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/profile") // Novo endpoint base
public class ProfileController {

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private DashboardService dashboardService;

    @GetMapping("/dashboard")
    public ResponseEntity<UserDashboardDTO> getUserDashboard(
            @AuthenticationPrincipal Usuario usuarioLogado) {
        return ResponseEntity.ok(dashboardService.getUserDashboard(usuarioLogado));
    }

    @PostMapping("/change-password")
    public ResponseEntity<Void> changePassword(
            @AuthenticationPrincipal Usuario usuarioLogado,
            @Valid @RequestBody PasswordChangeDTO dto) {

        usuarioService.changePassword(usuarioLogado, dto);
        return ResponseEntity.ok().build();
    }
}