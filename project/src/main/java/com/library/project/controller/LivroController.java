package com.library.project.controller;

import com.library.project.dto.LivroDTO;
import com.library.project.dto.LivroComStatusDTO;
import com.library.project.model.Usuario;
import com.library.project.service.LivroService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/livros")
@Tag(name = "3. Livros", description = "Endpoints para gerenciamento de livros")
public class LivroController {

    @Autowired
    private LivroService livroService;

    @GetMapping("/{id}")
    @Operation(summary = "Busca um livro por ID")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Livro encontrado"),
            @ApiResponse(responseCode = "404", description = "Livro não encontrado"),
            @ApiResponse(responseCode = "401", description = "Não autorizado (token inválido)")
    })
    public ResponseEntity<LivroDTO> buscarLivroPorId(@PathVariable Long id) {
        LivroDTO dto = livroService.buscarPorId(id);
        return ResponseEntity.ok(dto);
    }

    @PostMapping
    @Operation(summary = "Cria um novo livro", description = "Apenas para ADMINS")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Livro criado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos (ISBN duplicado, campos em branco)"),
            @ApiResponse(responseCode = "403", description = "Acesso negado (Usuário não é ADMIN)")
    })
    public ResponseEntity<LivroDTO> cadastrarLivro(@Valid @RequestBody LivroDTO dto) {
        LivroDTO livroSalvo = livroService.salvarLivro(dto);
        return new ResponseEntity<>(livroSalvo, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualiza um livro existente", description = "Apenas para ADMINS")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Livro atualizado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos"),
            @ApiResponse(responseCode = "404", description = "Livro não encontrado"),
            @ApiResponse(responseCode = "403", description = "Acesso negado (Usuário não é ADMIN)")
    })
    public ResponseEntity<LivroDTO> atualizarLivro(@PathVariable Long id,
                                                   @Valid @RequestBody LivroDTO dto) {
        dto.setId(id);
        LivroDTO livroAtualizado = livroService.salvarLivro(dto);
        return ResponseEntity.ok(livroAtualizado);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Exclui um livro", description = "Apenas para ADMINS")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Livro excluído com sucesso"),
            @ApiResponse(responseCode = "400", description = "Não é possível excluir (livro com empréstimos ativos)"),
            @ApiResponse(responseCode = "404", description = "Livro não encontrado"),
            @ApiResponse(responseCode = "403", description = "Acesso negado (Usuário não é ADMIN)")
    })
    public ResponseEntity<Void> deletarLivro(@PathVariable Long id) {
        livroService.deletarLivro(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search")
    @Operation(summary = "Busca livros por título ou autor", description = "Retorna o livro e o status de empréstimo do usuário logado.")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Busca realizada com sucesso"),
            @ApiResponse(responseCode = "401", description = "Não autorizado (token inválido)")
    })
    public ResponseEntity<List<LivroComStatusDTO>> buscarLivros(
            @RequestParam("q") String termo,
            @AuthenticationPrincipal Usuario usuarioLogado) {
        List<LivroComStatusDTO> resultado = livroService.buscarPorTermo(termo, usuarioLogado);
        return ResponseEntity.ok(resultado);
    }
}