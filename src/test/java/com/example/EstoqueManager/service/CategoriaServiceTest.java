package com.example.EstoqueManager.service;

import com.example.EstoqueManager.exception.BusinessException;
import com.example.EstoqueManager.exception.ResourceNotFoundException;
import com.example.EstoqueManager.model.CategoriaModel;
import com.example.EstoqueManager.repository.CategoriaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CategoriaServiceTest {

    @InjectMocks
    private CategoriaService categoriaService;

    @Mock
    private CategoriaRepository categoriaRepository;

    private CategoriaModel categoria;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        categoria = new CategoriaModel();
        categoria.setId(1L);
        categoria.setNome("Eletrônicos");
    }

    // ==================== TESTES DE FIND BY ID ====================

    @Test
    void findById_ValidId_ReturnsCategoria() {
        when(categoriaRepository.findById(1L)).thenReturn(Optional.of(categoria));
        CategoriaModel result = categoriaService.findById(1L);
        assertNotNull(result);
        assertEquals("Eletrônicos", result.getNome());
        verify(categoriaRepository, times(1)).findById(1L);
    }

    @Test
    void findById_InvalidId_ThrowsBusinessException() {
        assertThrows(BusinessException.class, () -> categoriaService.findById(null));
        assertThrows(BusinessException.class, () -> categoriaService.findById(-1L));
    }

    @Test
    void findById_NotFound_ThrowsResourceNotFoundException() {
        when(categoriaRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> categoriaService.findById(99L));
    }

    // ==================== TESTES DE SAVE (Com Validação de Duplicidade) ====================

    @Test
    void save_ValidCategoria_ReturnsSavedCategoria() {
        categoria.setId(null);
        when(categoriaRepository.findByNome("Eletrônicos")).thenReturn(Optional.empty());
        when(categoriaRepository.save(categoria)).thenReturn(categoria);

        CategoriaModel result = categoriaService.save(categoria);

        assertNotNull(result);
        verify(categoriaRepository, times(1)).save(categoria);
    }

    @Test
    void save_NullCategoria_ThrowsBusinessException() {
        Exception exception = assertThrows(BusinessException.class, () -> {
            categoriaService.save(null);
        });
        assertEquals("Categoria não pode ser nula.", exception.getMessage());
    }

    @Test
    void save_WithId_ThrowsBusinessException() {
        // ID não deve ser enviado na criação
        Exception exception = assertThrows(BusinessException.class, () -> {
            categoriaService.save(categoria);
        });
        assertEquals("ID deve ser nulo ao criar uma nova categoria.", exception.getMessage());
    }

    @Test
    void save_EmptyNome_ThrowsBusinessException() {
        categoria.setId(null);
        categoria.setNome("   ");

        Exception exception = assertThrows(BusinessException.class, () -> {
            categoriaService.save(categoria);
        });
        assertEquals("Nome da categoria é obrigatório.", exception.getMessage());
    }

    @Test
    void save_NomeDuplicado_ThrowsBusinessException() {
        categoria.setId(null);
        when(categoriaRepository.findByNome("Eletrônicos")).thenReturn(Optional.of(categoria));

        Exception exception = assertThrows(BusinessException.class, () -> {
            categoriaService.save(categoria);
        });

        assertEquals("Já existe uma categoria com este nome.", exception.getMessage());
        verify(categoriaRepository, never()).save(any());
    }

    // ==================== TESTES DE UPDATE BY ID (Com Validação de Duplicidade) ====================

    @Test
    void updateById_ValidUpdate_ReturnsUpdatedCategoria() {
        CategoriaModel existing = new CategoriaModel();
        existing.setId(1L);
        existing.setNome("Nome Antigo");

        CategoriaModel updateData = new CategoriaModel();
        updateData.setNome("Nome Novo");

        when(categoriaRepository.findById(1L)).thenReturn(Optional.of(existing));
        // Checa se o nome existe, excluindo o ID atual (deve retornar empty)
        when(categoriaRepository.findByNomeAndIdNot("Nome Novo", 1L)).thenReturn(Optional.empty());
        when(categoriaRepository.save(any(CategoriaModel.class))).thenReturn(existing);

        CategoriaModel result = categoriaService.updateById(1L, updateData);

        assertNotNull(result);
        assertEquals("Nome Novo", result.getNome());
        verify(categoriaRepository, times(1)).findById(1L);
        verify(categoriaRepository, times(1)).save(existing);
    }

    @Test
    void updateById_InvalidId_ThrowsBusinessException() {
        assertThrows(BusinessException.class, () -> categoriaService.updateById(null, categoria));
    }

    @Test
    void updateById_NotFound_ThrowsResourceNotFoundException() {
        when(categoriaRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> categoriaService.updateById(99L, categoria));
    }

    @Test
    void updateById_NullUpdateData_ThrowsBusinessException() {
        when(categoriaRepository.findById(1L)).thenReturn(Optional.of(categoria));
        assertThrows(BusinessException.class, () -> categoriaService.updateById(1L, null));
    }

    @Test
    void updateById_EmptyNome_ThrowsBusinessException() {
        CategoriaModel updateData = new CategoriaModel();
        updateData.setNome(" ");

        when(categoriaRepository.findById(1L)).thenReturn(Optional.of(categoria));
        assertThrows(BusinessException.class, () -> categoriaService.updateById(1L, updateData));
    }

    @Test
    void updateById_DuplicatedNome_ThrowsBusinessException() {
        CategoriaModel existing = new CategoriaModel();
        existing.setId(1L);

        CategoriaModel other = new CategoriaModel();
        other.setId(2L);
        other.setNome("Nome Duplicado");

        CategoriaModel updateData = new CategoriaModel();
        updateData.setNome("Nome Duplicado");

        when(categoriaRepository.findById(1L)).thenReturn(Optional.of(existing));
        // Simula que o nome já existe e pertence a outra categoria (ID 2)
        when(categoriaRepository.findByNomeAndIdNot("Nome Duplicado", 1L)).thenReturn(Optional.of(other));

        Exception exception = assertThrows(BusinessException.class, () -> {
            categoriaService.updateById(1L, updateData);
        });

        assertEquals("Já existe outra categoria com este nome.", exception.getMessage());
    }

    // ==================== TESTES DE DELETE ====================

    @Test
    void deleteById_ValidId_DeletesCategoria() {
        when(categoriaRepository.existsById(1L)).thenReturn(true);
        doNothing().when(categoriaRepository).deleteById(1L);

        assertDoesNotThrow(() -> categoriaService.deleteById(1L));

        verify(categoriaRepository, times(1)).existsById(1L);
        verify(categoriaRepository, times(1)).deleteById(1L);
    }

    @Test
    void deleteById_InvalidId_ThrowsBusinessException() {
        assertThrows(BusinessException.class, () -> categoriaService.deleteById(null));
    }

    @Test
    void deleteById_NotFound_ThrowsResourceNotFoundException() {
        when(categoriaRepository.existsById(99L)).thenReturn(false);
        assertThrows(ResourceNotFoundException.class, () -> categoriaService.deleteById(99L));
    }
}
