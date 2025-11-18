package com.library.project.service;

import com.library.project.dto.LivroDTO;
import com.library.project.model.Livro;
import com.library.project.repository.LivroRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class LivroService {

    // 1. Injeção de Dependência (Melhor Prática: Construtor)
    private final LivroRepository livroRepository;

    @Autowired
    public LivroService(LivroRepository livroRepository) {
        this.livroRepository = livroRepository;
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
    public void deletarLivro(Long id) {
        Livro livro = livroRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Livro não encontrado. ID: " + id));

        // Lógica de Negócio: Ex: Não deixar deletar livro com empréstimos ativos
        // (Adicionaremos isso quando o EmprestimoService existir)

        livroRepository.delete(livro);
    }


    // --- MÉTODOS "MAPPERS" PRIVADOS ---
    // (Converte DTO <-> Entidade)

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