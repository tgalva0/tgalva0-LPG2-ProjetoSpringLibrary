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

    @Transactional
    // --- ASSINATURA ATUALIZADA ---
    // Removemos o DTO e recebemos os dados "puros"
    public EmprestimoDTO realizarEmprestimo(Long livroId, Usuario usuarioLogado) {

        // --- LÓGICA DE NEGÓCIO ---

        // 1. Validar Usuário (Já temos o objeto! Não precisamos buscar)
        // Usuario usuario = usuarioRepository.findById(dto.getUsuarioId())... (PODE APAGAR ISSO)

        // 2. Validar Livro
        Livro livro = livroRepository.findById(livroId)
                .orElseThrow(() -> new RuntimeException("Livro não encontrado."));

        // 3. REGRA: Verificar se o livro está disponível
        if (livro.getQuantidadeDisponivel() <= 0) {
            throw new RuntimeException("Livro sem estoque disponível.");
        }

        // 4. REGRA: Verificar limite de livros (agora usa o 'usuarioLogado')
        long emprestimosAtivos = emprestimoRepository
                .findByUsuarioAndDataDevolucaoEfetivaIsNull(usuarioLogado).size(); // Usa o objeto

        if (emprestimosAtivos >= 3) {
            throw new RuntimeException("Usuário atingiu o limite de 3 empréstimos ativos.");
        }

        // 5. Atualizar o livro
        livro.setQuantidadeDisponivel(livro.getQuantidadeDisponivel() - 1);
        livroRepository.save(livro);

        // 6. Criar o Empréstimo
        Emprestimo novoEmprestimo = new Emprestimo();
        novoEmprestimo.setUsuario(usuarioLogado); // Usa o objeto
        novoEmprestimo.setLivro(livro);
        novoEmprestimo.setDataEmprestimo(LocalDate.now());
        novoEmprestimo.setDataDevolucaoPrevista(LocalDate.now().plusDays(7));
        novoEmprestimo.setDataDevolucaoEfetiva(null);

        Emprestimo emprestimoSalvo = emprestimoRepository.save(novoEmprestimo);
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