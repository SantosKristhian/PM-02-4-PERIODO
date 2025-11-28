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

        // Configuração inicial de um usuário VÁLIDO
        usuario = new UsuarioModel();
        usuario.setId(1L);
        usuario.setNome("João Silva");
        usuario.setCpf("12345678900");
        usuario.setIdade(30);
        usuario.setLogin("joao123");
        usuario.setSenha("senha123");
        usuario.setCargo(Cargo.ADM);
    }

    // ==================== TESTES DE AUTENTICAÇÃO ====================

    @Test
    void autenticar_ValidLoginAndSenha_ReturnsUsuario() {
        when(usuarioRepository.findByLoginAndSenha("joao123", "senha123")).thenReturn(usuario);
        UsuarioModel result = usuarioService.autenticar("joao123", "senha123");
        assertNotNull(result);
        assertEquals("João Silva", result.getNome());
        verify(usuarioRepository, times(1)).findByLoginAndSenha("joao123", "senha123");
    }

    @Test
    void autenticar_InvalidLoginOrSenha_ReturnsNull() {
        when(usuarioRepository.findByLoginAndSenha("loginErrado", "senhaErrada")).thenReturn(null);
        UsuarioModel result = usuarioService.autenticar("loginErrado", "senhaErrada");
        assertNull(result);
        verify(usuarioRepository, times(1)).findByLoginAndSenha("loginErrado", "senhaErrada");
    }

    // ==================== TESTES DE FIND ALL ====================

    @Test
    void findAll_ReturnsListOfUsuarios() {
        List<UsuarioModel> usuarios = new ArrayList<>();
        usuarios.add(usuario);
        when(usuarioRepository.findAll()).thenReturn(usuarios);
        List<UsuarioModel> result = usuarioService.findAll();
        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        verify(usuarioRepository, times(1)).findAll();
    }

    // ==================== TESTES DE FIND BY ID ====================

    @Test
    void findById_ValidId_ReturnsUsuario() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        UsuarioModel result = usuarioService.findById(1L);
        assertNotNull(result);
        assertEquals("João Silva", result.getNome());
        verify(usuarioRepository, times(1)).findById(1L);
    }

    @Test
    void findById_InvalidId_ThrowsResourceNotFoundException() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.empty());
        Exception exception = assertThrows(ResourceNotFoundException.class, () -> {
            usuarioService.findById(1L);
        });
        assertEquals("Usuário não encontrado com ID: 1", exception.getMessage());
        verify(usuarioRepository, times(1)).findById(1L);
    }

    // ==================== TESTES DE SAVE (Criação e Validação) ====================

    @Test
    void save_ValidUsuario_ReturnsUsuario() {
        usuario.setId(null);
        when(usuarioRepository.save(usuario)).thenReturn(usuario);
        UsuarioModel result = usuarioService.save(usuario);
        assertNotNull(result);
        assertEquals("João Silva", result.getNome());
        verify(usuarioRepository, times(1)).save(usuario);
    }

    @Test
    void save_InvalidUsuario_ThrowsBusinessException_LoginDuplicado() {
        usuario.setId(null);
        when(usuarioRepository.findByLogin("joao123")).thenReturn(usuario);

        Exception exception = assertThrows(BusinessException.class, () -> {
            usuarioService.save(usuario);
        });

        assertEquals("Login já cadastrado no sistema.", exception.getMessage());
        verify(usuarioRepository, times(1)).findByLogin("joao123");
        verify(usuarioRepository, never()).save(any());
    }

    // --- Novos Testes de Validação (Cobrindo 55% do validarUsuario) ---

    @Test
    void save_NullUsuario_ThrowsBusinessException() {
        Exception exception = assertThrows(BusinessException.class,
                () -> usuarioService.save(null));

        assertEquals("Usuário não pode ser nulo.", exception.getMessage());
    }
/*
    @Test
    void save_NullNome_ThrowsBusinessException() {
        usuario.setNome(null);

        Exception exception = assertThrows(BusinessException.class,
                () -> usuarioService.save(usuario));

        assertEquals("Nome é obrigatório.", exception.getMessage());
    }

    @Test
    void save_EmptyNome_ThrowsBusinessException() {
        usuario.setNome("   ");

        Exception exception = assertThrows(BusinessException.class,
                () -> usuarioService.save(usuario));

        assertEquals("Nome é obrigatório.", exception.getMessage());
    }

    @Test
    void save_InvalidCpfFormat_ThrowsBusinessException() {
        usuario.setCpf("123");

        Exception exception = assertThrows(BusinessException.class,
                () -> usuarioService.save(usuario));

        assertEquals("CPF deve ter 11 dígitos.", exception.getMessage());
    }

    @Test
    void save_NullIdade_ThrowsBusinessException() {
        usuario.setIdade(null);

        Exception exception = assertThrows(BusinessException.class,
                () -> usuarioService.save(usuario));

        assertEquals("Idade é obrigatória.", exception.getMessage());
    }

    @Test
    void save_NegativeIdade_ThrowsBusinessException() {
        usuario.setIdade(-5);

        Exception exception = assertThrows(BusinessException.class,
                () -> usuarioService.save(usuario));

        assertEquals("Idade deve ser positiva.", exception.getMessage());
    }
*/
    @Test
    void save_NullLogin_ThrowsBusinessException() {
        usuario.setLogin(null);

        Exception exception = assertThrows(BusinessException.class,
                () -> usuarioService.save(usuario));

        assertEquals("Login é obrigatório.", exception.getMessage());
    }

    @Test
    void save_NullSenha_ThrowsBusinessException() {
        usuario.setSenha(null);

        Exception exception = assertThrows(BusinessException.class,
                () -> usuarioService.save(usuario));

        assertEquals("Senha é obrigatória.", exception.getMessage());
    }

    @Test
    void save_NullCargo_ThrowsBusinessException() {
        usuario.setCargo(null);

        Exception exception = assertThrows(BusinessException.class,
                () -> usuarioService.save(usuario));

        assertEquals("Cargo é obrigatório.", exception.getMessage());
    }

    // ==================== TESTES DE DELETE ====================

    @Test
    void deleteById_ValidId_RemovesUsuario() {
        when(usuarioRepository.existsById(1L)).thenReturn(true);
        doNothing().when(usuarioRepository).deleteById(1L);

        assertDoesNotThrow(() -> usuarioService.deleteById(1L));

        verify(usuarioRepository, times(1)).existsById(1L);
        verify(usuarioRepository, times(1)).deleteById(1L);
    }

    @Test
    void deleteById_InvalidId_ThrowsResourceNotFoundException() {
        when(usuarioRepository.existsById(1L)).thenReturn(false);

        Exception exception = assertThrows(ResourceNotFoundException.class, () -> {
            usuarioService.deleteById(1L);
        });

        assertEquals("Usuário não encontrado com ID: 1", exception.getMessage());
        verify(usuarioRepository, times(1)).existsById(1L);
    }

    // ==================== TESTES DE UPDATE ====================

    @Test
    void updateByID_ValidId_UpdatesUsuario() {
        // Setup inicial
        UsuarioModel existingUsuario = new UsuarioModel();
        existingUsuario.setId(1L);
        existingUsuario.setNome("João Antigo");
        existingUsuario.setLogin("antigo");
        existingUsuario.setSenha("senhaAntiga");
        existingUsuario.setCargo(Cargo.VENDEDOR);

        // Dados de atualização
        UsuarioModel usuarioUpdatedData = new UsuarioModel();
        usuarioUpdatedData.setNome("João Atualizado");
        usuarioUpdatedData.setCpf("12345678900");
        usuarioUpdatedData.setIdade(35);
        usuarioUpdatedData.setLogin("joao123"); // Login vai mudar, mas não é usado aqui
        usuarioUpdatedData.setSenha("novaSenha");
        usuarioUpdatedData.setCargo(Cargo.ADM);

        // Configuração do mock
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(existingUsuario));
        when(usuarioRepository.save(existingUsuario)).thenReturn(existingUsuario);

        // Ação
        UsuarioModel result = usuarioService.updateByID(1L, usuarioUpdatedData);

        // Verificações
        assertNotNull(result);
        assertEquals("João Atualizado", result.getNome());
        assertEquals(35, result.getIdade());
        assertEquals("novaSenha", result.getSenha(), "Senha deve ser atualizada");
        assertEquals(Cargo.ADM, result.getCargo());

        verify(usuarioRepository, times(1)).findById(1L);
        verify(usuarioRepository, times(1)).save(existingUsuario);
    }

    @Test
    void updateByID_InvalidId_ThrowsResourceNotFoundException() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.empty());

        Exception exception = assertThrows(ResourceNotFoundException.class, () -> {
            usuarioService.updateByID(1L, usuario);
        });

        assertEquals("Usuário não encontrado com ID: 1", exception.getMessage());
        verify(usuarioRepository, times(1)).findById(1L);
    }
/*
    @Test
    void updateByID_NullUpdateData_ThrowsBusinessException() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));

        Exception exception = assertThrows(BusinessException.class, () -> {
            usuarioService.updateByID(1L, null);
        });

        assertEquals("Dados de atualização não podem ser nulos.", exception.getMessage());
        verify(usuarioRepository, times(1)).findById(1L);
    }
*/
    // ==================== TESTES DE EXISTÊNCIA ====================

    @Test
    void existemUsuarios_ReturnsTrue() {
        when(usuarioRepository.count()).thenReturn(1L);
        boolean result = usuarioService.existemUsuarios();
        assertTrue(result);
        verify(usuarioRepository, times(1)).count();
    }

    @Test
    void existeAdministrador_ReturnsTrue() {
        when(usuarioRepository.findAll()).thenReturn(List.of(usuario));
        boolean result = usuarioService.existeAdministrador();
        assertTrue(result);
        verify(usuarioRepository, times(1)).findAll();
    }

    @Test
    void existeAdministrador_ReturnsFalse() {
        UsuarioModel usuarioVendedor = new UsuarioModel();
        usuarioVendedor.setCargo(Cargo.VENDEDOR);
        when(usuarioRepository.findAll()).thenReturn(List.of(usuarioVendedor));

        boolean result = usuarioService.existeAdministrador();

        assertFalse(result);
        verify(usuarioRepository, times(1)).findAll();
    }

    @Test
    void existeAdministrador_EmptyList_ReturnsFalse() {
        when(usuarioRepository.findAll()).thenReturn(List.of());

        boolean result = usuarioService.existeAdministrador();

        assertFalse(result);
        verify(usuarioRepository, times(1)).findAll();
    }
}
