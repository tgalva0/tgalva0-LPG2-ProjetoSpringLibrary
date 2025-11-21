package com.library.project.controller;

import com.library.project.dto.AdminDashboardStatsDTO;
import com.library.project.dto.AdminUsuarioCreateDTO;
import com.library.project.dto.AdminUsuarioUpdateDTO;
import com.library.project.dto.UsuarioDTO;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@Tag(name = "5. Admin", description = "Endpoints de gerenciamento para Administradores")
@SecurityRequirement(name = "bearerAuth")
public class AdminController {

    @Autowired
    private UsuarioService usuarioService;
    @Autowired
    private DashboardService dashboardService;

    @GetMapping("/dashboard/stats")
    @Operation(summary = "Busca estatísticas gerais", description = "Retorna contagem de usuários, livros e empréstimos.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Estatísticas retornadas com sucesso"),
            @ApiResponse(responseCode = "403", description = "Acesso negado (Usuário não é ADMIN)")
    })
    public ResponseEntity<AdminDashboardStatsDTO> getAdminStats() {
        return ResponseEntity.ok(dashboardService.getAdminDashboard());
    }

    @GetMapping("/usuarios")
    @Operation(summary = "Lista todos os usuários", description = "Retorna uma lista de todos os usuários registrados.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso"),
            @ApiResponse(responseCode = "403", description = "Acesso negado (Usuário não é ADMIN)")
    })
    public ResponseEntity<List<UsuarioDTO>> listarUsuarios() {
        return ResponseEntity.ok(usuarioService.listarTodos());
    }

    @PostMapping("/usuarios")
    @Operation(summary = "Cria um novo usuário", description = "Cria um novo usuário com papéis definidos pelo admin.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Usuário criado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos (username duplicado, papel não existe)"),
            @ApiResponse(responseCode = "403", description = "Acesso negado (Usuário não é ADMIN)")
    })
    public ResponseEntity<UsuarioDTO> criarUsuario(@Valid @RequestBody AdminUsuarioCreateDTO dto) {
        UsuarioDTO novoUsuario = usuarioService.adminCriarUsuario(dto);
        return new ResponseEntity<>(novoUsuario, HttpStatus.CREATED);
    }

    @GetMapping("/usuarios/search")
    @Operation(summary = "Busca usuários por username", description = "Busca usuários (exceto o próprio admin) por parte do username.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Busca realizada com sucesso"),
            @ApiResponse(responseCode = "403", description = "Acesso negado (Usuário não é ADMIN)")
    })
    public ResponseEntity<List<UsuarioDTO>> buscarUsuarios(
            @RequestParam("q") String termo,
            @AuthenticationPrincipal Usuario adminLogado) {
        List<UsuarioDTO> resultado = usuarioService.buscarPorUsername(termo, adminLogado);
        return ResponseEntity.ok(resultado);
    }

    @PutMapping("/usuarios/{id}")
    @Operation(summary = "Atualiza um usuário", description = "Atualiza o nome completo e os papéis de um usuário.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuário atualizado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos (papel não existe)"),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado"),
            @ApiResponse(responseCode = "403", description = "Acesso negado (Usuário não é ADMIN)")
    })
    public ResponseEntity<UsuarioDTO> atualizarUsuario(
            @PathVariable Long id,
            @Valid @RequestBody AdminUsuarioUpdateDTO dto) {
        UsuarioDTO usuarioAtualizado = usuarioService.adminAtualizarUsuario(id, dto);
        return ResponseEntity.ok(usuarioAtualizado);
    }

    @DeleteMapping("/usuarios/{id}")
    @Operation(summary = "Exclui um usuário", description = "Exclui um usuário (se não for o último admin e não tiver empréstimos ativos).")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Usuário excluído com sucesso"),
            @ApiResponse(responseCode = "400", description = "Não é possível excluir (regra de negócio violada)"),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado"),
            @ApiResponse(responseCode = "403", description = "Acesso negado (Usuário não é ADMIN)")
    })
    public ResponseEntity<Void> deletarUsuario(@PathVariable Long id) {
        usuarioService.deletarUsuario(id);
        return ResponseEntity.noContent().build();
    }
}