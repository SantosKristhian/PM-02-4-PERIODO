package com.example.EstoqueManager.auth;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// A anotação @WebMvcTest foca em testar a camada de Controller/Web
// Você precisa importar sua classe de configuração de segurança (ajuste o nome se for diferente)
@WebMvcTest
// Se o seu SecurityConfig precisar de outros Beans para funcionar, você pode precisar usar @Import
// Ex: @Import(MinhaClasseDeSeguranca.class)
class SecurityAccessTests {

    @Autowired
    private MockMvc mockMvc;

    // **Ajuste as URLs de acordo com o seu projeto!**

    @Test
    void shouldDenyAccessToProtectedEndpointWithoutAuth() throws Exception {
        // Supondo que /api/produto é um endpoint que exige login
        mockMvc.perform(get("/api/produto"))
                // Espera-se 401 Unauthorized ou 403 Forbidden se estiver protegido
                .andExpect(status().isUnauthorized()); // ou .isForbidden() dependendo da sua config
    }

    @Test
    void shouldAllowAccessToPublicEndpoint() throws Exception {
        // Supondo que /auth/login é um endpoint público
        mockMvc.perform(get("/auth/login"))
                // Espera-se 200 OK ou 4xx, mas não um erro de segurança (se for uma rota pública)
                .andExpect(status().isOk());
    }

    // Testes adicionais devem ser criados com autenticação simulada para garantir o acesso.
}
