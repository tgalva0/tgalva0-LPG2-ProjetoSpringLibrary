package com.library.project.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.context.annotation.Configuration;

@Configuration
// 1. Define o esquema de segurança (o "tipo" de chave)
@SecurityScheme(
        name = "bearerAuth", // Um nome interno para este esquema
        type = SecuritySchemeType.HTTP, // O tipo é HTTP
        scheme = "bearer", // O esquema é "Bearer"
        bearerFormat = "JWT", // O formato é JWT
        in = SecuritySchemeIn.HEADER, // O token vai no Cabeçalho (Header)
        description = "Insira o token JWT: Bearer <seu-token>"
)
// 2. Define as informações gerais da API e aplica o esquema globalmente
@OpenAPIDefinition(
        info = @Info(title = "API da Biblioteca", version = "v1"),
        security = {
                // 3. Aplica o esquema "bearerAuth" a TODOS os endpoints
                @SecurityRequirement(name = "bearerAuth")
        }
)
public class OpenApiConfig {
}