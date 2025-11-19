package com.library.project.controller;
import com.library.project.dto.LivroDTO;
import com.library.project.dto.LivroComStatusDTO;
import com.library.project.model.Usuario;
import com.library.project.service.LivroService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/livros")
public class LivroController {

    private final LivroService livroService;

    @Autowired
    public LivroController(LivroService livroService) {
        this.livroService = livroService;
    }

    @GetMapping
    public ResponseEntity<List<LivroDTO>> buscarTodosLivros() {
        List<LivroDTO> lista = livroService.buscarTodos();
        return ResponseEntity.ok(lista);
    }

    @GetMapping("/{id}")
    public ResponseEntity<LivroDTO> buscarLivroPorId(@PathVariable Long id) {
        LivroDTO dto = livroService.buscarPorId(id);
        return ResponseEntity.ok(dto);
    }

    @PostMapping
    public ResponseEntity<LivroDTO> cadastrarLivro(@Valid @RequestBody LivroDTO dto) {
        LivroDTO livroSalvo = livroService.salvarLivro(dto);
        return new ResponseEntity<>(livroSalvo, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<LivroDTO> atualizarLivro(@PathVariable Long id, @Valid @RequestBody LivroDTO dto) {
        dto.setId(id);
        LivroDTO livroAtualizado = livroService.salvarLivro(dto);
        return ResponseEntity.ok(livroAtualizado);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarLivro(@PathVariable Long id) {
        livroService.deletarLivro(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search")
    public ResponseEntity<List<LivroComStatusDTO>> buscarLivros(
            @RequestParam("q") String termo,
            @AuthenticationPrincipal Usuario usuarioLogado) { // 1. Pegue o usuário logado

        // 2. Passe o termo E o usuário para o serviço
        List<LivroComStatusDTO> resultado = livroService.buscarPorTermo(termo, usuarioLogado);
        return ResponseEntity.ok(resultado);
    }
}