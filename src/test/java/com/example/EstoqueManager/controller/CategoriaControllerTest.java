package com.example.EstoqueManager.controller;

import com.example.EstoqueManager.exception.BusinessException;
import com.example.EstoqueManager.exception.ResourceNotFoundException;
import com.example.EstoqueManager.model.CategoriaModel;
import com.example.EstoqueManager.service.CategoriaService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class CategoriaControllerTest {

    private MockMvc mockMvc;

    @InjectMocks
    private CategoriaController categoriaController;

    @Mock
    private CategoriaService categoriaService;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(categoriaController).build();
        objectMapper = new ObjectMapper();
    }

    @Test
    void findAll_ReturnsListOfCategories() throws Exception {
        CategoriaModel cat1 = new CategoriaModel(1L, "Categoria A", List.of());
        CategoriaModel cat2 = new CategoriaModel(2L, "Categoria B", List.of());

        when(categoriaService.findAll()).thenReturn(List.of(cat1, cat2));

        mockMvc.perform(get("/api/emanager/categoria/findAll"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(2))
                .andExpect(jsonPath("$[0].nome").value("Categoria A"))
                .andExpect(jsonPath("$[1].nome").value("Categoria B"));
    }

    @Test
    void findById_ValidId_ReturnsCategoria() throws Exception {
        CategoriaModel categoria = new CategoriaModel(1L, "Categoria A", List.of());
        when(categoriaService.findById(1L)).thenReturn(categoria);

        mockMvc.perform(get("/api/emanager/categoria/findById/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nome").value("Categoria A"));
    }

    @Test
    void findById_InvalidId_ThrowsResourceNotFoundException() throws Exception {
        when(categoriaService.findById(1L)).thenThrow(ResourceNotFoundException.class);

        mockMvc.perform(get("/api/emanager/categoria/findById/1"))
                .andExpect(status().isNotFound());
    }

    @Test
    void save_ValidCategoria_ReturnsCreated() throws Exception {
        CategoriaModel input = new CategoriaModel(null, "Categoria A", List.of());
        CategoriaModel saved = new CategoriaModel(1L, "Categoria A", List.of());

        when(categoriaService.save(input)).thenReturn(saved);

        mockMvc.perform(post("/api/emanager/categoria/save")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nome").value("Categoria A"));
    }

    @Test
    void save_NullCategoria_ThrowsBusinessException() throws Exception {
        when(categoriaService.save(null)).thenThrow(BusinessException.class);

        mockMvc.perform(post("/api/emanager/categoria/save")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void update_ValidId_ReturnsUpdatedCategoria() throws Exception {
        CategoriaModel updates = new CategoriaModel(null, "Categoria Atualizada", List.of());
        CategoriaModel updated = new CategoriaModel(1L, "Categoria Atualizada", List.of());

        when(categoriaService.updateById(1L, updates)).thenReturn(updated);

        mockMvc.perform(put("/api/emanager/categoria/update/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updates)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nome").value("Categoria Atualizada"));
    }

    @Test
    void deleteById_ValidId_ReturnsNoContent() throws Exception {
        doNothing().when(categoriaService).deleteById(1L);

        mockMvc.perform(delete("/api/emanager/categoria/delete/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteById_InvalidId_ThrowsResourceNotFoundException() throws Exception {
        doThrow(ResourceNotFoundException.class).when(categoriaService).deleteById(1L);

        mockMvc.perform(delete("/api/emanager/categoria/delete/1"))
                .andExpect(status().isNotFound());
    }
}
