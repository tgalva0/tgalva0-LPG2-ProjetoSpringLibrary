package com.library.project.service;

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
    private UsuarioRepository usuarioRepository; // Mantemos, embora menos usado
    @Mock
    private LivroRepository livroRepository;

    // Objetos de cenário
    private Usuario usuarioPadrao;
    private Livro livroDisponivel;
    private Livro livroSemEstoque;

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

        // O EmprestimoCreateDTO não é mais necessário aqui
    }

    @Test
    void deveRealizarEmprestimo_ComSucesso() {
        // --- 1. Cenário (Given) ---

        // Agora o serviço não busca o usuário, ele o recebe.
        // AINDA precisamos mockar a busca do livro.
        when(livroRepository.findById(10L)).thenReturn(Optional.of(livroDisponivel));

        // Mock da verificação de limite de empréstimos
        when(emprestimoRepository.findByUsuarioAndDataDevolucaoEfetivaIsNull(usuarioPadrao))
                .thenReturn(Collections.emptyList());

        // Mock do save
        when(emprestimoRepository.save(any(Emprestimo.class))).thenAnswer(invocation -> {
            Emprestimo e = invocation.getArgument(0);
            e.setId(99L);
            e.setUsuario(usuarioPadrao);
            e.setLivro(livroDisponivel);
            return e;
        });

        // --- 2. Ação (When) ---
        // Chamamos o método com a NOVA assinatura
        EmprestimoDTO resultado = emprestimoService.realizarEmprestimo(10L, usuarioPadrao);

        // --- 3. Verificação (Then) ---
        assertThat(resultado).isNotNull();
        assertThat(resultado.getLivroTitulo()).isEqualTo("Livro com Estoque");
        assertThat(resultado.getUsuarioNome()).isEqualTo("Usuário Teste");

        // Verifica se o estoque baixou
        assertThat(livroDisponivel.getQuantidadeDisponivel()).isEqualTo(4);

        // Verifica se os mocks de save foram chamados
        verify(livroRepository, times(1)).save(livroDisponivel);
        verify(emprestimoRepository, times(1)).save(any(Emprestimo.class));
    }

    @Test
    void naoDeveRealizarEmprestimo_QuandoLivroSemEstoque() {
        // --- 1. Cenário (Given) ---
        Long idLivroSemEstoque = 11L;
        // Mockamos a busca do livro para retornar o livro com estoque 0
        when(livroRepository.findById(idLivroSemEstoque)).thenReturn(Optional.of(livroSemEstoque));

        // --- 2. Ação (When) & 3. Verificação (Then) ---
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            // Chamamos com a nova assinatura
            emprestimoService.realizarEmprestimo(idLivroSemEstoque, usuarioPadrao);
        });

        assertThat(exception.getMessage()).isEqualTo("Livro sem estoque disponível.");

        // Verifica se NADA foi salvo
        verify(livroRepository, never()).save(any(Livro.class));
        verify(emprestimoRepository, never()).save(any(Emprestimo.class));
    }

    @Test
    void naoDeveRealizarEmprestimo_QuandoUsuarioAtingeLimite() {
        // --- 1. Cenário (Given) ---
        Long idLivroDisponivel = 10L;
        when(livroRepository.findById(idLivroDisponivel)).thenReturn(Optional.of(livroDisponivel));

        // Mock da verificação de limite: RETORNA 3 EMPRÉSTIMOS
        when(emprestimoRepository.findByUsuarioAndDataDevolucaoEfetivaIsNull(usuarioPadrao))
                .thenReturn(List.of(new Emprestimo(), new Emprestimo(), new Emprestimo()));

        // --- 2. Ação (When) & 3. Verificação (Then) ---
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            emprestimoService.realizarEmprestimo(idLivroDisponivel, usuarioPadrao);
        });

        assertThat(exception.getMessage()).isEqualTo("Usuário atingiu o limite de 3 empréstimos ativos.");

        verify(livroRepository, never()).save(any(Livro.class));
        verify(emprestimoRepository, never()).save(any(Emprestimo.class));
    }
}