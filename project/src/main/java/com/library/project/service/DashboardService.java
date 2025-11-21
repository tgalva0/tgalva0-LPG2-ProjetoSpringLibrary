package com.library.project.service;

import com.library.project.dto.AdminDashboardStatsDTO;
import com.library.project.dto.EmprestimoDTO;
import com.library.project.dto.UserDashboardDTO;
import com.library.project.model.Usuario;
import com.library.project.repository.EmprestimoRepository;
import com.library.project.repository.LivroRepository;
import com.library.project.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true) // Otimiza para consultas (apenas leitura)
public class DashboardService {

    @Autowired
    private UsuarioRepository usuarioRepository;
    @Autowired
    private LivroRepository livroRepository;
    @Autowired
    private EmprestimoRepository emprestimoRepository;

    // --- Método para o Admin ---
    public AdminDashboardStatsDTO getAdminDashboard() {
        long totalUsuarios = usuarioRepository.count();
        long totalLivros = livroRepository.count(); // Conta títulos únicos
        long totalEmprestimosAtivos = emprestimoRepository.countByDataDevolucaoEfetivaIsNull();

        return new AdminDashboardStatsDTO(totalUsuarios, totalLivros, totalEmprestimosAtivos);
    }

    // --- Método para o Usuário Comum ---
    public UserDashboardDTO getUserDashboard(Usuario usuarioLogado) {
        // Reutiliza o DTO de Empréstimo que já temos
        List<EmprestimoDTO> emprestimos = emprestimoRepository
                .findByUsuarioAndDataDevolucaoEfetivaIsNull(usuarioLogado)
                .stream()
                .map(EmprestimoDTO::new) // Converte Entidade -> DTO
                .collect(Collectors.toList());

        return new UserDashboardDTO(emprestimos);
    }
}