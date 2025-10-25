package com.example.EstoqueManager.controller;

import com.example.EstoqueManager.exception.BusinessException;
import com.example.EstoqueManager.exception.ResourceNotFoundException;
import com.example.EstoqueManager.model.CompradorModel;
import com.example.EstoqueManager.service.CompradorService;
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

class CompradorControllerTest {

    private MockMvc mockMvc;

    @InjectMocks
    private CompradorController compradorController;

    @Mock
    private CompradorService compradorService;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(compradorController).build();
        objectMapper = new ObjectMapper();
    }

    @Test
    void findAll_ReturnsListOfCompradores() throws Exception {
        CompradorModel comp1 = new CompradorModel(1L, "João", "12345678901", "joao@mail.com");
        CompradorModel comp2 = new CompradorModel(2L, "Maria", "10987654321", "maria@mail.com");

        when(compradorService.findAll()).thenReturn(List.of(comp1, comp2));

        mockMvc.perform(get("/api/emanager/comprador/findAll"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(2))
                .andExpect(jsonPath("$[0].nome").value("João"))
                .andExpect(jsonPath("$[1].nome").value("Maria"));
    }

    @Test
    void findById_ValidId_ReturnsComprador() throws Exception {
        CompradorModel comprador = new CompradorModel(1L, "João", "12345678901", "joao@mail.com");
        when(compradorService.findById(1L)).thenReturn(comprador);

        mockMvc.perform(get("/api/emanager/comprador/findById/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nome").value("João"));
    }

    @Test
    void findById_InvalidId_ThrowsResourceNotFoundException() throws Exception {
        when(compradorService.findById(1L)).thenThrow(ResourceNotFoundException.class);

        mockMvc.perform(get("/api/emanager/comprador/findById/1"))
                .andExpect(status().isNotFound());
    }

    @Test
    void save_ValidComprador_ReturnsCreated() throws Exception {
        CompradorModel input = new CompradorModel(null, "João", "12345678901", "joao@mail.com");
        CompradorModel saved = new CompradorModel(1L, "João", "12345678901", "joao@mail.com");

        when(compradorService.save(input)).thenReturn(saved);

        mockMvc.perform(post("/api/emanager/comprador/save")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nome").value("João"));
    }

    @Test
    void save_NullComprador_ThrowsBusinessException() throws Exception {
        when(compradorService.save(null)).thenThrow(BusinessException.class);

        mockMvc.perform(post("/api/emanager/comprador/save")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void update_ValidId_ReturnsUpdatedComprador() throws Exception {
        CompradorModel updates = new CompradorModel(null, "João Atualizado", "12345678901", "joao@mail.com");
        CompradorModel updated = new CompradorModel(1L, "João Atualizado", "12345678901", "joao@mail.com");

        when(compradorService.updateById(1L, updates)).thenReturn(updated);

        mockMvc.perform(put("/api/emanager/comprador/update/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updates)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nome").value("João Atualizado"));
    }

    @Test
    void deleteById_ValidId_ReturnsNoContent() throws Exception {
        doNothing().when(compradorService).deleteById(1L);

        mockMvc.perform(delete("/api/emanager/comprador/delete/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteById_InvalidId_ThrowsResourceNotFoundException() throws Exception {
        doThrow(ResourceNotFoundException.class).when(compradorService).deleteById(1L);

        mockMvc.perform(delete("/api/emanager/comprador/delete/1"))
                .andExpect(status().isNotFound());
    }
}
