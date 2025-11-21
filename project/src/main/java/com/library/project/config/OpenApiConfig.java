package com.library.project.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.context.annotation.Configuration;

@Configuration
// 1. Define o esquema de segurança (o "tipo" de chave)
@SecurityScheme(
        name = "bearerAuth", // O nome que usaremos para referenciá-lo
        type = SecuritySchemeType.HTTP,
        scheme = "bearer",
        bearerFormat = "JWT",
        in = SecuritySchemeIn.HEADER,
        description = "Insira o token JWT: Bearer <seu-token>"
)
// 2. Define as informações gerais da API
@OpenAPIDefinition(
        info = @Info(title = "API da Biblioteca", version = "v1")
        // Note que removemos o @SecurityRequirement global daqui
)
public class OpenApiConfig {
}