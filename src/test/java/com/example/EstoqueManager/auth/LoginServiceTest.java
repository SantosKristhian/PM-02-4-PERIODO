package com.example.EstoqueManager.auth;

import com.example.EstoqueManager.config.JwtServiceGenerator;
import com.example.EstoqueManager.model.UsuarioModel;
// Certifique-se de que o import para o LoginDTO está correto:
import com.example.EstoqueManager.auth.LoginDTO;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class LoginServiceTest {

    @InjectMocks
    private LoginService loginService;

    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private LoginRepository repository;
    @Mock
    private JwtServiceGenerator jwtService;

    private LoginDTO loginDTO;
    private UsuarioModel userModel;
    private UsernamePasswordAuthenticationToken authToken;

    @BeforeEach
    void setup() {
        // Opção B: Assumindo que seu DTO usa construtor vazio + setters (Ajuste se for diferente!)
        loginDTO = new LoginDTO();
        loginDTO.setLogin("usuario_teste");
        loginDTO.setSenha("senha123");

        userModel = new UsuarioModel();
        userModel.setLogin("usuario_teste");

        authToken = new UsernamePasswordAuthenticationToken(
                loginDTO.getLogin(),
                loginDTO.getSenha()
        );
    }

    @Test
    void logar_shouldAuthenticateFindUserAndGenerateToken() {
        // 1. Configurar Mocks: Cenário de SUCESSO

        // Simula que a autenticação passou
        when(authenticationManager.authenticate(authToken)).thenReturn(mock(Authentication.class));

        // Simula que o repositório encontrou o usuário
        when(repository.findByLogin(loginDTO.getLogin())).thenReturn(Optional.of(userModel));

        // Simula que o JwtServiceGenerator gerou um token
        String expectedToken = "simulated.jwt.token";
        when(jwtService.generateToken(userModel)).thenReturn(expectedToken);

        // 2. Chamar o método a ser testado
        String resultToken = loginService.logar(loginDTO);

        // 3. Verificar o resultado
        assertNotNull(resultToken, "O token retornado não deve ser nulo.");
        assertEquals(expectedToken, resultToken, "Deve retornar o token simulado.");

        // 4. Verificar se as dependências foram chamadas (o que garante a cobertura)
        verify(authenticationManager, times(1)).authenticate(authToken);
        verify(repository, times(1)).findByLogin(loginDTO.getLogin());
        verify(jwtService, times(1)).generateToken(userModel);
    }

    @Test
    void gerarToken_shouldThrowExceptionIfUserNotFound() {
        // 1. Configurar Mocks: Cenário de FALHA (usuário não encontrado no DB)

        // Simula que a autenticação passou (o erro ocorrerá depois)
        when(authenticationManager.authenticate(authToken)).thenReturn(mock(Authentication.class));

        // CORREÇÃO AQUI: Sintaxe correta do Mockito para simular que o repositório NÃO encontra o usuário
        when(repository.findByLogin(loginDTO.getLogin())).thenReturn(Optional.empty());

        // 2. Verifica se uma exceção é lançada
        // Espera-se que lance NoSuchElementException quando .get() for chamado em um Optional.empty()
        assertThrows(java.util.NoSuchElementException.class, () -> {
            loginService.gerarToken(loginDTO);
        });

        // 3. Verifica que o gerador de token NÃO foi chamado
        verify(jwtService, never()).generateToken(any());
    }
}
