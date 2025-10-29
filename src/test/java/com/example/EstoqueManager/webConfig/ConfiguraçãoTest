package com.example.EstoqueManager.webConfig;

import org.junit.jupiter.api.Test;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class ConfiguracaoTest {

    @Test
    void corsConfigurerBeanIsCreated() {
        Configuração configuracao = new Configuração();
        WebMvcConfigurer configurer = configuracao.corsConfigurer();

        // Verifica se o bean foi criado e não é nulo, cobrindo o método.
        assertNotNull(configurer, "O bean corsConfigurer não deve ser nulo.");

        // Para uma cobertura mais completa, podemos tentar adicionar mapeamentos,
        // mas o teste de criação do bean já cobre a instrução principal.
    }
}
