package com.example.EstoqueManager;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class EstoqueManagerApplicationTest {

    @Test
    void contextLoads() {
        // Este teste garante que o contexto do Spring Boot é carregado com sucesso.
        // Se o contexto carregar, a aplicação é considerada funcional.
    }

    @Test
    void mainMethodRuns() {
        // Testa a execução do método main para cobrir a linha EstoqueManagerApplication.java:10
        EstoqueManagerApplication.main(new String[] {});
    }
}
