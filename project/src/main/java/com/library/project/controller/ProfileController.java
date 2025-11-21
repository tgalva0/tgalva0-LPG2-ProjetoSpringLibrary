package com.library.project.controller;

import com.library.project.dto.PasswordChangeDTO;
import com.library.project.dto.UserDashboardDTO;
import com.library.project.model.Usuario;
import com.library.project.service.DashboardService;
import com.library.project.service.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/profile")
@Tag(name = "2. Perfil do Usuário", description = "Endpoints para gerenciamento do próprio perfil")
@SecurityRequirement(name = "bearerAuth")
public class ProfileController {

    @Autowired
    private UsuarioService usuarioService;
    @Autowired
    private DashboardService dashboardService;

    @GetMapping("/dashboard")
    @Operation(summary = "Busca o dashboard do usuário", description = "Retorna os empréstimos ativos do usuário logado.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Dashboard retornado com sucesso"),
            @ApiResponse(responseCode = "401", description = "Não autorizado")
    })
    public ResponseEntity<UserDashboardDTO> getUserDashboard(
            @AuthenticationPrincipal Usuario usuarioLogado) {
        return ResponseEntity.ok(dashboardService.getUserDashboard(usuarioLogado));
    }

    @PostMapping("/change-password")
    @Operation(summary = "Altera a senha do usuário logado")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Senha alterada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos (senha atual incorreta, nova senha curta)"),
            @ApiResponse(responseCode = "401", description = "Não autorizado")
    })
    public ResponseEntity<Void> changePassword(
            @AuthenticationPrincipal Usuario usuarioLogado,
            @Valid @RequestBody PasswordChangeDTO dto) {

        usuarioService.changePassword(usuarioLogado, dto);
        return ResponseEntity.ok().build();
    }
}