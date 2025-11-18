package com.library.project.service;

import com.library.project.dto.EmprestimoCreateDTO;
import com.library.project.dto.EmprestimoDTO;
import com.library.project.model.Emprestimo;
import com.library.project.model.Livro;
import com.library.project.model.Usuario;
import com.library.project.repository.EmprestimoRepository;
import com.library.project.repository.LivroRepository;
import com.library.project.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class EmprestimoService {

    private final EmprestimoRepository emprestimoRepository;
    private final UsuarioRepository usuarioRepository;
    private final LivroRepository livroRepository;

    @Autowired
    public EmprestimoService(EmprestimoRepository emprestimoRepository,
                             UsuarioRepository usuarioRepository,
                             LivroRepository livroRepository) {
        this.emprestimoRepository = emprestimoRepository;
        this.usuarioRepository = usuarioRepository;
        this.livroRepository = livroRepository;
    }

    @Transactional // Essencial! Múltiplas operações no banco.
    public EmprestimoDTO realizarEmprestimo(EmprestimoCreateDTO dto) {

        // --- LÓGICA DE NEGÓCIO ---

        // 1. Validar Usuário
        Usuario usuario = usuarioRepository.findById(dto.getUsuarioId())
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado."));

        // 2. Validar Livro
        Livro livro = livroRepository.findById(dto.getLivroId())
                .orElseThrow(() -> new RuntimeException("Livro não encontrado."));

        // 3. REGRA: Verificar se o livro está disponível
        if (livro.getQuantidadeDisponivel() <= 0) {
            throw new RuntimeException("Livro sem estoque disponível.");
        }

        // 4. REGRA: Verificar se o usuário já tem muitos livros (ex: limite de 3)
        long emprestimosAtivos = emprestimoRepository
                .findByUsuarioAndDataDevolucaoEfetivaIsNull(usuario).size();

        if (emprestimosAtivos >= 3) {
            throw new RuntimeException("Usuário atingiu o limite de 3 empréstimos ativos.");
        }

        // --- FIM DA LÓGICA ---

        // 5. Atualizar o livro
        livro.setQuantidadeDisponivel(livro.getQuantidadeDisponivel() - 1);
        livroRepository.save(livro); // Salva a quantidade atualizada

        // 6. Criar o Empréstimo
        Emprestimo novoEmprestimo = new Emprestimo();
        novoEmprestimo.setUsuario(usuario);
        novoEmprestimo.setLivro(livro);
        novoEmprestimo.setDataEmprestimo(LocalDate.now());
        novoEmprestimo.setDataDevolucaoPrevista(LocalDate.now().plusDays(7)); // Regra: 7 dias
        novoEmprestimo.setDataDevolucaoEfetiva(null); // Ainda não foi devolvido

        // 7. Salvar o Empréstimo
        Emprestimo emprestimoSalvo = emprestimoRepository.save(novoEmprestimo);

        // 8. Retornar o DTO
        return new EmprestimoDTO(emprestimoSalvo);
    }

    @Transactional
    public EmprestimoDTO realizarDevolucao(Long emprestimoId) {

        // 1. Validar Empréstimo
        Emprestimo emprestimo = emprestimoRepository.findById(emprestimoId)
                .orElseThrow(() -> new RuntimeException("Empréstimo não encontrado."));

        // 2. REGRA: Verificar se já não foi devolvido
        if (emprestimo.getDataDevolucaoEfetiva() != null) {
            throw new RuntimeException("Este livro já foi devolvido em " + emprestimo.getDataDevolucaoEfetiva());
        }

        // 3. Atualizar o Empréstimo
        emprestimo.setDataDevolucaoEfetiva(LocalDate.now());
        Emprestimo emprestimoSalvo = emprestimoRepository.save(emprestimo);

        // 4. Atualizar o estoque do Livro
        Livro livro = emprestimo.getLivro();
        livro.setQuantidadeDisponivel(livro.getQuantidadeDisponivel() + 1);
        livroRepository.save(livro);

        // 5. Retornar DTO
        return new EmprestimoDTO(emprestimoSalvo);
    }
}