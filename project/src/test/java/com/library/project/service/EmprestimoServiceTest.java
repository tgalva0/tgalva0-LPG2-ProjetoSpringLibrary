package com.library.project.service;

import com.library.project.dto.EmprestimoCreateDTO;
import com.library.project.dto.EmprestimoDTO;
import com.library.project.model.Emprestimo;
import com.library.project.model.Livro;
import com.library.project.model.Usuario;
import com.library.project.repository.EmprestimoRepository;
import com.library.project.repository.LivroRepository;
import com.library.project.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
public class EmprestimoServiceTest {

    @InjectMocks
    private EmprestimoService emprestimoService;

    @Mock
    private EmprestimoRepository emprestimoRepository;
    @Mock
    private UsuarioRepository usuarioRepository;
    @Mock
    private LivroRepository livroRepository;

    private Usuario usuarioPadrao;
    private Livro livroDisponivel;
    private Livro livroSemEstoque;
    private EmprestimoCreateDTO dto;

    @BeforeEach
    void setUp() {
        usuarioPadrao = new Usuario();
        usuarioPadrao.setId(1L);
        usuarioPadrao.setNomeCompleto("Usuário Teste");

        livroDisponivel = new Livro();
        livroDisponivel.setId(10L);
        livroDisponivel.setTitulo("Livro com Estoque");
        livroDisponivel.setQuantidadeDisponivel(5);

        livroSemEstoque = new Livro();
        livroSemEstoque.setId(11L);
        livroSemEstoque.setTitulo("Livro sem Estoque");
        livroSemEstoque.setQuantidadeDisponivel(0);

        dto = new EmprestimoCreateDTO();
        dto.setUsuarioId(1L);
        dto.setLivroId(10L);
    }

    @Test
    void deveRealizarEmprestimo_ComSucesso() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuarioPadrao));
        when(livroRepository.findById(10L)).thenReturn(Optional.of(livroDisponivel));
        when(emprestimoRepository.findByUsuarioAndDataDevolucaoEfetivaIsNull(usuarioPadrao))
                .thenReturn(Collections.emptyList());

        when(emprestimoRepository.save(any(Emprestimo.class))).thenAnswer(invocation -> {
            Emprestimo e = invocation.getArgument(0);
            e.setId(99L);
            e.setUsuario(usuarioPadrao);
            e.setLivro(livroDisponivel);
            return e;
        });

        EmprestimoDTO resultado = emprestimoService.realizarEmprestimo(dto);
        assertThat(resultado).isNotNull();
        assertThat(resultado.getLivroTitulo()).isEqualTo("Livro com Estoque");
        assertThat(resultado.getUsuarioNome()).isEqualTo("Usuário Teste");
        assertThat(resultado.getDataDevolucaoPrevista()).isEqualTo(LocalDate.now().plusDays(7));

        verify(livroRepository, times(1)).save(livroDisponivel);
        verify(emprestimoRepository, times(1)).save(any(Emprestimo.class));

        assertThat(livroDisponivel.getQuantidadeDisponivel()).isEqualTo(4);
    }

    @Test
    void naoDeveRealizarEmprestimo_QuandoLivroSemEstoque() {
        dto.setLivroId(11L);
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuarioPadrao));
        when(livroRepository.findById(11L)).thenReturn(Optional.of(livroSemEstoque));
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            emprestimoService.realizarEmprestimo(dto);
        });

        assertThat(exception.getMessage()).isEqualTo("Livro sem estoque disponível.");

        verify(livroRepository, never()).save(any(Livro.class));
        verify(emprestimoRepository, never()).save(any(Emprestimo.class));
    }

    @Test
    void naoDeveRealizarEmprestimo_QuandoUsuarioAtingeLimite() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuarioPadrao));
        when(livroRepository.findById(10L)).thenReturn(Optional.of(livroDisponivel));
        when(emprestimoRepository.findByUsuarioAndDataDevolucaoEfetivaIsNull(usuarioPadrao))
                .thenReturn(List.of(new Emprestimo(), new Emprestimo(), new Emprestimo()));
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            emprestimoService.realizarEmprestimo(dto);
        });

        assertThat(exception.getMessage()).isEqualTo("Usuário atingiu o limite de 3 empréstimos ativos.");

        verify(livroRepository, never()).save(any(Livro.class));
        verify(emprestimoRepository, never()).save(any(Emprestimo.class));
    }
}