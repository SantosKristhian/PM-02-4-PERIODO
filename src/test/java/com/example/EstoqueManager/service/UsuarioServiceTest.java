package com.example.EstoqueManager.service;

import com.example.EstoqueManager.exception.BusinessException;
import com.example.EstoqueManager.exception.ResourceNotFoundException;
import com.example.EstoqueManager.model.Cargo;
import com.example.EstoqueManager.model.UsuarioModel;
import com.example.EstoqueManager.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UsuarioServiceTest {

    @InjectMocks
    private UsuarioService usuarioService;

    @Mock
    private UsuarioRepository usuarioRepository;

    private UsuarioModel usuario;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Configuração inicial de um usuário
        usuario = new UsuarioModel();
        usuario.setId(1L);
        usuario.setNome("João Silva");
        usuario.setCpf("12345678900");
        usuario.setIdade(30);
        usuario.setLogin("joao123");
        usuario.setSenha("senha123");
        usuario.setCargo(Cargo.ADM); // Usando o valor válido da enum Cargo
    }

    @Test
    void autenticar_ValidLoginAndSenha_ReturnsUsuario() {
        // Configuração do mock
        when(usuarioRepository.findByLoginAndSenha("joao123", "senha123")).thenReturn(usuario);

        // Execução
        UsuarioModel result = usuarioService.autenticar("joao123", "senha123");

        // Verificações
        assertNotNull(result);
        assertEquals("João Silva", result.getNome());
        verify(usuarioRepository, times(1)).findByLoginAndSenha("joao123", "senha123");
    }

    @Test
    void autenticar_InvalidLoginOrSenha_ReturnsNull() {
        // Configuração do mock
        when(usuarioRepository.findByLoginAndSenha("loginErrado", "senhaErrada")).thenReturn(null);

        // Execução
        UsuarioModel result = usuarioService.autenticar("loginErrado", "senhaErrada");

        // Verificações
        assertNull(result);
        verify(usuarioRepository, times(1)).findByLoginAndSenha("loginErrado", "senhaErrada");
    }

    @Test
    void findAll_ReturnsListOfUsuarios() {
        // Configuração do mock
        List<UsuarioModel> usuarios = new ArrayList<>();
        usuarios.add(usuario);
        when(usuarioRepository.findAll()).thenReturn(usuarios);

        // Execução
        List<UsuarioModel> result = usuarioService.findAll();

        // Verificações
        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        verify(usuarioRepository, times(1)).findAll();
    }

    @Test
    void findById_ValidId_ReturnsUsuario() {
        // Configuração do mock
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));

        // Execução
        UsuarioModel result = usuarioService.findById(1L);

        // Verificações
        assertNotNull(result);
        assertEquals("João Silva", result.getNome());
        verify(usuarioRepository, times(1)).findById(1L);
    }

    @Test
    void findById_InvalidId_ThrowsResourceNotFoundException() {
        // Configuração do mock
        when(usuarioRepository.findById(1L)).thenReturn(Optional.empty());

        // Execução e Verificação
        Exception exception = assertThrows(ResourceNotFoundException.class, () -> {
            usuarioService.findById(1L);
        });

        // Atualize a mensagem esperada para coincidir com a mensagem real
        assertEquals("Usuário não encontrado com ID: 1", exception.getMessage());
        verify(usuarioRepository, times(1)).findById(1L);
    }

    @Test
    void save_ValidUsuario_ReturnsUsuario() {
        // Configuração inicial de um novo usuário sem ID
        usuario.setId(null); // ID deve ser nulo para simular a criação de um novo usuário

        // Configuração do mock
        when(usuarioRepository.save(usuario)).thenReturn(usuario);

        // Execução
        UsuarioModel result = usuarioService.save(usuario);

        // Verificações
        assertNotNull(result);
        assertEquals("João Silva", result.getNome());
        verify(usuarioRepository, times(1)).save(usuario);
    }

    @Test
    void save_InvalidUsuario_ThrowsBusinessException() {
        // Configuração inicial de um usuário sem ID
        usuario.setId(null); // O ID deve ser nulo para evitar a validação do ID

        // Configurar um login duplicado
        when(usuarioRepository.findByLogin("joao123")).thenReturn(usuario);

        // Execução e Verificação
        Exception exception = assertThrows(BusinessException.class, () -> {
            usuarioService.save(usuario);
        });

        assertEquals("Login já cadastrado no sistema.", exception.getMessage());
        verify(usuarioRepository, times(1)).findByLogin("joao123");
    }

    @Test
    void deleteById_ValidId_RemovesUsuario() {
        // Mockando o comportamento correto do repositório
        when(usuarioRepository.existsById(1L)).thenReturn(true);
        doNothing().when(usuarioRepository).deleteById(1L);

        // Execução do método a ser testado
        assertDoesNotThrow(() -> usuarioService.deleteById(1L));

        // Verificações
        verify(usuarioRepository, times(1)).existsById(1L);
        verify(usuarioRepository, times(1)).deleteById(1L);
    }

    @Test
    void deleteById_InvalidId_ThrowsResourceNotFoundException() {
        // Configuração do mock
        when(usuarioRepository.existsById(1L)).thenReturn(false);

        // Execução e Verificação
        Exception exception = assertThrows(ResourceNotFoundException.class, () -> {
            usuarioService.deleteById(1L);
        });

        // Corrigir a mensagem esperada
        assertEquals("Usuário não encontrado com ID: 1", exception.getMessage());
        verify(usuarioRepository, times(1)).existsById(1L);
    }

    @Test
    void updateByID_ValidId_UpdatesUsuario() {
        // Configuração do mock
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(usuarioRepository.save(usuario)).thenReturn(usuario);

        // Criação de um usuário atualizado
        UsuarioModel usuarioUpdated = new UsuarioModel();
        usuarioUpdated.setNome("João Atualizado");
        usuarioUpdated.setCpf("12345678900");
        usuarioUpdated.setIdade(35);
        usuarioUpdated.setLogin("joao123");
        usuarioUpdated.setSenha("novaSenha");
        usuarioUpdated.setCargo(Cargo.VENDEDOR); // Usando o valor VENDEDOR da enum

        // Execução
        UsuarioModel result = usuarioService.updateByID(1L, usuarioUpdated);

        // Verificações
        assertNotNull(result);
        assertEquals("João Atualizado", result.getNome());
        assertEquals(35, result.getIdade());
        verify(usuarioRepository, times(1)).save(usuario);
    }

    @Test
    void updateByID_InvalidId_ThrowsResourceNotFoundException() {
        // Configuração do mock
        when(usuarioRepository.findById(1L)).thenReturn(Optional.empty());

        // Execução e Verificação
        Exception exception = assertThrows(ResourceNotFoundException.class, () -> {
            usuarioService.updateByID(1L, usuario);
        });

        // Ajuste na mensagem esperada para refletir o valor real
        assertEquals("Usuário não encontrado com ID: 1", exception.getMessage());
        verify(usuarioRepository, times(1)).findById(1L);
    }

    @Test
    void existemUsuarios_ReturnsTrue() {
        when(usuarioRepository.count()).thenReturn(1L);

        boolean result = usuarioService.existemUsuarios();
        assertTrue(result);
        verify(usuarioRepository, times(1)).count();
    }


    @Test
    void existeAdministrador_ReturnsTrue() {
        // Mock para findAll() retornar uma lista com o usuário que tem cargo ADM
        when(usuarioRepository.findAll()).thenReturn(List.of(usuario));

        boolean result = usuarioService.existeAdministrador();

        assertTrue(result);
        verify(usuarioRepository, times(1)).findAll();
    }

    @Test
    void existeAdministrador_ReturnsFalse() {
        // Criar um usuário sem cargo ADM
        UsuarioModel usuarioVendedor = new UsuarioModel();
        usuarioVendedor.setId(2L);
        usuarioVendedor.setNome("Maria Santos");
        usuarioVendedor.setCpf("98765432100");
        usuarioVendedor.setIdade(25);
        usuarioVendedor.setLogin("maria123");
        usuarioVendedor.setSenha("senha456");
        usuarioVendedor.setCargo(Cargo.VENDEDOR);

        // Mock para findAll() retornar uma lista com apenas o vendedor
        when(usuarioRepository.findAll()).thenReturn(List.of(usuarioVendedor));

        boolean result = usuarioService.existeAdministrador();

        assertFalse(result);
        verify(usuarioRepository, times(1)).findAll();
    }

    @Test
    void existeAdministrador_EmptyList_ReturnsFalse() {
        // Mock para findAll() retornar lista vazia
        when(usuarioRepository.findAll()).thenReturn(List.of());

        boolean result = usuarioService.existeAdministrador();

        assertFalse(result);
        verify(usuarioRepository, times(1)).findAll();
    }

}