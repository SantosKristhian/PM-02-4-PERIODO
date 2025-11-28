package com.example.EstoqueManager.service;

import com.example.EstoqueManager.exception.BusinessException;
import com.example.EstoqueManager.exception.ResourceNotFoundException;
import com.example.EstoqueManager.model.CompradorModel;
import com.example.EstoqueManager.repository.CompradorRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CompradorServiceTest {

    @InjectMocks
    private CompradorService compradorService;

    @Mock
    private CompradorRepository compradorRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void findAll_ReturnsListOfCompradores() {
        // Cenário
        CompradorModel comprador1 = new CompradorModel(
                1L,
                "Comprador A",
                "111.111.111-11",
                "compradorA@email.com",
                List.of()
        );
        CompradorModel comprador2 = new CompradorModel(
                2L,
                "Comprador B",
                "222.222.222-22",
                "compradorB@email.com",
                List.of()
        );
        when(compradorRepository.findAll()).thenReturn(List.of(comprador1, comprador2));

        // Ação
        List<CompradorModel> compradores = compradorService.findAll();

        // Verificação
        assertNotNull(compradores);
        assertEquals(2, compradores.size());
        assertEquals("Comprador A", compradores.get(0).getNome());
        assertEquals("Comprador B", compradores.get(1).getNome());
    }

    @Test
    void findById_ValidId_ReturnsComprador() {
        // Cenário
        CompradorModel comprador = new CompradorModel(
                1L,
                "Comprador A",
                "111.111.111-11",
                "compradorA@email.com",
                List.of()
        );
        when(compradorRepository.findById(1L)).thenReturn(Optional.of(comprador));

        // Ação
        CompradorModel result = compradorService.findById(1L);

        // Verificação
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Comprador A", result.getNome());
    }

    @Test
    void findById_InvalidId_ThrowsResourceNotFoundException() {
        // Cenário
        when(compradorRepository.findById(1L)).thenReturn(Optional.empty());

        // Ação & Verificação
        assertThrows(ResourceNotFoundException.class, () -> compradorService.findById(1L));
    }

    @Test
    void save_ValidComprador_ReturnsSavedComprador() {
        // Cenário
        CompradorModel compradorToSave = new CompradorModel(
                null,
                "Comprador A",
                "111.111.111-11",
                "compradorA@email.com",
                List.of()
        );
        CompradorModel savedComprador = new CompradorModel(
                1L,
                "Comprador A",
                "111.111.111-11",
                "compradorA@email.com",
                List.of()
        );
        when(compradorRepository.save(compradorToSave)).thenReturn(savedComprador);

        // Ação
        CompradorModel result = compradorService.save(compradorToSave);

        // Verificação
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Comprador A", result.getNome());
    }

    @Test
    void save_InvalidComprador_ThrowsBusinessExceptionForNull() {
        // Ação & Verificação
        assertThrows(BusinessException.class, () -> compradorService.save(null));
    }

    @Test
    void save_InvalidComprador_ThrowsBusinessExceptionForInvalidData() {
        // Cenário
        CompradorModel comprador = new CompradorModel(null, "", " ", null, List.of());

        // Ação & Verificação
        assertThrows(BusinessException.class, () -> compradorService.save(comprador));
    }

    @Test
    void deleteById_ValidId_DeletesComprador() {
        // Cenário
        when(compradorRepository.existsById(1L)).thenReturn(true);
        doNothing().when(compradorRepository).deleteById(1L);

        // Ação
        compradorService.deleteById(1L);

        // Verificação
        verify(compradorRepository, times(1)).deleteById(1L);
    }

    @Test
    void deleteById_InvalidId_ThrowsResourceNotFoundException() {
        // Cenário
        when(compradorRepository.existsById(1L)).thenReturn(false);

        // Ação & Verificação
        assertThrows(ResourceNotFoundException.class, () -> compradorService.deleteById(1L));
    }

    @Test
    void updateById_ValidIdAndComprador_ReturnsUpdatedComprador() {
        // Cenário
        CompradorModel existingComprador = new CompradorModel(
                1L,
                "Comprador Antigo",
                "111.111.111-11",
                "compradorAntigo@email.com",
                List.of()
        );
        CompradorModel updates = new CompradorModel(
                null,
                "Comprador Atualizado",
                "222.222.222-22",
                "compradorAtualizado@email.com",
                List.of()
        );
        CompradorModel updatedComprador = new CompradorModel(
                1L,
                "Comprador Atualizado",
                "222.222.222-22",
                "compradorAtualizado@email.com",
                List.of()
        );
        when(compradorRepository.findById(1L)).thenReturn(Optional.of(existingComprador));
        when(compradorRepository.save(existingComprador)).thenReturn(updatedComprador);

        // Ação
        CompradorModel result = compradorService.updateById(1L, updates);

        // Verificação
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Comprador Atualizado", result.getNome());
    }

    @Test
    void updateById_InvalidId_ThrowsResourceNotFoundException() {
        // Cenário
        CompradorModel updates = new CompradorModel(
                null,
                "Comprador Atualizado",
                "222.222.222-22",
                "compradorAtualizado@email.com",
                List.of()
        );
        when(compradorRepository.findById(1L)).thenReturn(Optional.empty());

        // Ação & Verificação
        assertThrows(ResourceNotFoundException.class, () -> compradorService.updateById(1L, updates));
    }
}
