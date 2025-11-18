package com.library.project.controller;

import com.library.project.dto.EmprestimoCreateDTO;
import com.library.project.dto.EmprestimoDTO;
import com.library.project.service.EmprestimoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/emprestimos")
public class EmprestimoController {

    private final EmprestimoService emprestimoService;

    @Autowired
    public EmprestimoController(EmprestimoService emprestimoService) {
        this.emprestimoService = emprestimoService;
    }

    @PostMapping
    public ResponseEntity<EmprestimoDTO> realizarEmprestimo(@Valid @RequestBody EmprestimoCreateDTO dto) {
        EmprestimoDTO novoEmprestimo = emprestimoService.realizarEmprestimo(dto);
        return new ResponseEntity<>(novoEmprestimo, HttpStatus.CREATED);
    }

    @PostMapping("/{id}/devolucao")
    public ResponseEntity<EmprestimoDTO> devolverLivro(@PathVariable Long id) {
        EmprestimoDTO emprestimoDevolvido = emprestimoService.realizarDevolucao(id);
        return ResponseEntity.ok(emprestimoDevolvido);
    }
}
