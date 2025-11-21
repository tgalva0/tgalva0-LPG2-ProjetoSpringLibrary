package com.library.project.controller;

import com.library.project.dto.EmprestimoCreateDTO;
import com.library.project.dto.EmprestimoDTO;
import com.library.project.model.Usuario;
import com.library.project.service.EmprestimoService;
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

@RestController
@RequestMapping("/api/emprestimos")
@Tag(name = "4. Empréstimos", description = "Endpoints para emprestar e devolver livros")
@SecurityRequirement(name = "bearerAuth")
public class EmprestimoController {

    @Autowired
    private EmprestimoService emprestimoService;

    @PostMapping
    @Operation(summary = "Realiza um novo empréstimo")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Empréstimo realizado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Regra de negócio falhou (sem estoque, limite de usuário)"),
            @ApiResponse(responseCode = "404", description = "Livro não encontrado"),
            @ApiResponse(responseCode = "401", description = "Não autorizado")
    })
    public ResponseEntity<EmprestimoDTO> realizarEmprestimo(
            @Valid @RequestBody EmprestimoCreateDTO dto,
            @AuthenticationPrincipal Usuario usuarioLogado) {

        EmprestimoDTO novoEmprestimo = emprestimoService.realizarEmprestimo(dto.getLivroId(), usuarioLogado);
        return new ResponseEntity<>(novoEmprestimo, HttpStatus.CREATED);
    }

    @PostMapping("/{id}/devolucao")
    @Operation(summary = "Realiza a devolução de um livro")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Devolução realizada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Regra de negócio falhou (livro já devolvido)"),
            @ApiResponse(responseCode = "404", description = "Empréstimo não encontrado"),
            @ApiResponse(responseCode = "401", description = "Não autorizado")
    })
    public ResponseEntity<EmprestimoDTO> devolverLivro(@PathVariable Long id) {
        EmprestimoDTO emprestimoDevolvido = emprestimoService.realizarDevolucao(id);
        return ResponseEntity.ok(emprestimoDevolvido);
    }
}