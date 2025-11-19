package com.library.project.dto;
import java.util.Set;

public record LoginResponseDTO(String token, String nomeCompleto, Set<String> roles) {
}