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

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void findAll_ReturnsListOfCategories() {
        // Cenário
        CategoriaModel categoria1 = new CategoriaModel(1L, "Categoria A", List.of());
        CategoriaModel categoria2 = new CategoriaModel(2L, "Categoria B", List.of());
        when(categoriaRepository.findAll()).thenReturn(List.of(categoria1, categoria2));

        // Ação
        List<CategoriaModel> categorias = categoriaService.findAll();

        // Verificação
        assertNotNull(categorias);
        assertEquals(2, categorias.size());
        assertEquals("Categoria A", categorias.get(0).getNome());
        assertEquals("Categoria B", categorias.get(1).getNome());
    }

    @Test
    void findById_ValidId_ReturnsCategoria() {
        // Cenário
        CategoriaModel categoria = new CategoriaModel(1L, "Categoria A", List.of());
        when(categoriaRepository.findById(1L)).thenReturn(Optional.of(categoria));

        // Ação
        CategoriaModel result = categoriaService.findById(1L);

        // Verificação
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Categoria A", result.getNome());
    }

    @Test
    void findById_InvalidId_ThrowsResourceNotFoundException() {
        // Cenário
        when(categoriaRepository.findById(1L)).thenReturn(Optional.empty());

        // Ação & Verificação
        assertThrows(ResourceNotFoundException.class, () -> categoriaService.findById(1L));
    }

    @Test
    void save_ValidCategoria_ReturnsSavedCategoria() {
        // Cenário
        CategoriaModel categoriaToSave = new CategoriaModel(null, "Categoria A", List.of());
        CategoriaModel savedCategoria = new CategoriaModel(1L, "Categoria A", List.of());
        when(categoriaRepository.save(categoriaToSave)).thenReturn(savedCategoria);

        // Ação
        CategoriaModel result = categoriaService.save(categoriaToSave);

        // Verificação
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Categoria A", result.getNome());
    }

    @Test
    void save_NullCategoria_ThrowsBusinessException() {
        // Ação & Verificação
        assertThrows(BusinessException.class, () -> categoriaService.save(null));
    }

    @Test
    void deleteById_ValidId_DeletesCategoria() {
        // Cenário
        when(categoriaRepository.existsById(1L)).thenReturn(true);
        doNothing().when(categoriaRepository).deleteById(1L);

        // Ação
        categoriaService.deleteById(1L);

        // Verificação
        verify(categoriaRepository, times(1)).deleteById(1L);
    }

    @Test
    void deleteById_InvalidId_ThrowsResourceNotFoundException() {
        // Cenário
        when(categoriaRepository.existsById(1L)).thenReturn(false);

        // Ação & Verificação
        assertThrows(ResourceNotFoundException.class, () -> categoriaService.deleteById(1L));
    }

    @Test
    void updateById_ValidIdAndCategoria_ReturnsUpdatedCategoria() {
        // Cenário
        CategoriaModel existingCategoria = new CategoriaModel(1L, "Categoria Antiga", List.of());
        CategoriaModel updates = new CategoriaModel(null, "Categoria Atualizada", List.of());
        CategoriaModel updatedCategoria = new CategoriaModel(1L, "Categoria Atualizada", List.of());
        when(categoriaRepository.findById(1L)).thenReturn(Optional.of(existingCategoria));
        when(categoriaRepository.save(existingCategoria)).thenReturn(updatedCategoria);

        // Ação
        CategoriaModel result = categoriaService.updateById(1L, updates);

        // Verificação
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Categoria Atualizada", result.getNome());
    }

    @Test
    void updateById_InvalidId_ThrowsResourceNotFoundException() {
        // Cenário
        CategoriaModel updates = new CategoriaModel(null, "Categoria Atualizada", List.of());
        when(categoriaRepository.findById(1L)).thenReturn(Optional.empty());

        // Ação & Verificação
        assertThrows(ResourceNotFoundException.class, () -> categoriaService.updateById(1L, updates));
    }
}