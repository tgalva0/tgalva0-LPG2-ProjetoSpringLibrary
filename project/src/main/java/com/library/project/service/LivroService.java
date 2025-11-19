package com.library.project.service;

import com.library.project.dto.LivroDTO;
import com.library.project.dto.LivroComStatusDTO;
import com.library.project.model.Emprestimo;
import com.library.project.model.Livro;
import com.library.project.repository.LivroRepository;
import com.library.project.model.Usuario;
import com.library.project.repository.EmprestimoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; 
import com.library.project.model.Emprestimo; 
import java.util.List; 

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class LivroService {

    // 1. Injeção de Dependência (Melhor Prática: Construtor)
    private final LivroRepository livroRepository;
    private final EmprestimoRepository emprestimoRepository;

    @Autowired
    public LivroService(LivroRepository livroRepository, EmprestimoRepository emprestimoRepository) {
        this.livroRepository = livroRepository;
        this.emprestimoRepository = emprestimoRepository;
    }

    // 2. Método de Busca por ID
    public LivroDTO buscarPorId(Long id) {
        // Usa o método que criamos no teste!
        Livro livro = livroRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Livro não encontrado. ID: " + id)); // Exceção simples

        return toDTO(livro); // Converte Entidade para DTO
    }

    // 3. Método de Listar Todos
    public List<LivroDTO> buscarTodos() {
        return livroRepository.findAll()
                .stream()       // Transforma a lista em um "stream"
                .map(this::toDTO) // Para cada item, chama o método toDTO
                .collect(Collectors.toList()); // Coleta de volta para uma Lista
    }

    // 4. Método de Salvar (Criar ou Atualizar)
    public LivroDTO salvarLivro(LivroDTO dto) {
        // Lógica de Negócio: Ex: Não permitir ISBN duplicado
        if (dto.getId() == null) { // Só checa no cadastro (novo livro)
            livroRepository.findByIsbn(dto.getIsbn()).ifPresent(livroExistente -> {
                throw new RuntimeException("ISBN já cadastrado.");
            });
        }

        Livro entidade = toEntity(dto); // Converte DTO para Entidade
        Livro livroSalvo = livroRepository.save(entidade);
        return toDTO(livroSalvo); // Converte de volta para DTO
    }

    // 5. Método de Deletar
    @Transactional // IMPORTANTE: Garante que tudo execute como uma única operação
    public void deletarLivro(Long id) {
        // 1. Verifica se o livro existe
        Livro livro = livroRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Livro não encontrado. ID: " + id));

        // 2. REGRA DE NEGÓCIO: Verifica se há empréstimos ATIVOS
        if (!emprestimoRepository.findByLivroIdAndDataDevolucaoEfetivaIsNull(id).isEmpty()) {
            throw new RuntimeException("Não é possível excluir: este livro possui empréstimos ativos.");
        }

        // 3. Se não há empréstimos ativos, apaga o histórico de empréstimos passados
        // (Usando o método que acabamos de adicionar no EmprestimoRepository)
        List<Emprestimo> emprestimosPassados = emprestimoRepository.findByLivroId(id);
        emprestimoRepository.deleteAll(emprestimosPassados);

        // 4. Agora que não há mais referências, deleta o livro
        livroRepository.delete(livro);
    }

    public List<LivroComStatusDTO> buscarPorTermo(String termo, Usuario usuarioLogado) {

        // Passo A: Busca os livros (como antes)
        List<Livro> livros = livroRepository.findByTituloContainingIgnoreCaseOrAutorContainingIgnoreCase(termo, termo);

        // Passo B: Busca os empréstimos ATIVOS do usuário
        List<Emprestimo> emprestimosAtivos = emprestimoRepository.findByUsuarioAndDataDevolucaoEfetivaIsNull(usuarioLogado);

        // Passo C: Mapeia (LivroID -> EmprestimoID) para consulta rápida
        Map<Long, Long> mapaLivroEmprestimo = emprestimosAtivos.stream()
                .collect(Collectors.toMap(
                        e -> e.getLivro().getId(), // Chave: ID do Livro
                        Emprestimo::getId         // Valor: ID do Empréstimo
                ));

        // Passo D: Constrói o DTO "inteligente"
        return livros.stream()
                .map(livro -> new LivroComStatusDTO(
                        livro,
                        mapaLivroEmprestimo.get(livro.getId()) // Retorna o ID do empréstimo (ou null)
                ))
                .collect(Collectors.toList());
    }

    private LivroDTO toDTO(Livro entidade) {
        return new LivroDTO(
                entidade.getId(),
                entidade.getTitulo(),
                entidade.getAutor(),
                entidade.getIsbn(),
                entidade.getQuantidadeDisponivel()
        );
    }

    private Livro toEntity(LivroDTO dto) {
        Livro entidade = new Livro();
        entidade.setId(dto.getId()); // Se o ID for nulo, o JPA entende que é um 'save'
        entidade.setTitulo(dto.getTitulo());
        entidade.setAutor(dto.getAutor());
        entidade.setIsbn(dto.getIsbn());
        entidade.setQuantidadeDisponivel(dto.getQuantidadeDisponivel());
        return entidade;
    }
}