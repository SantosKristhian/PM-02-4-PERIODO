package com.example.EstoqueManager.controller;

import com.example.EstoqueManager.dto.ProdutoCurvaABCDTO;
import com.example.EstoqueManager.exception.ResourceNotFoundException;
import com.example.EstoqueManager.model.ProdutoModel;
import com.example.EstoqueManager.model.UsuarioModel;
import com.example.EstoqueManager.service.ProdutoService;
import com.example.EstoqueManager.service.UsuarioService;
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

class ProdutoControllerTest {

    private MockMvc mockMvc;

    @InjectMocks
    private ProdutoController produtoController;

    @Mock
    private ProdutoService produtoService;

    @Mock
    private UsuarioService usuarioService;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(produtoController).build();
        objectMapper = new ObjectMapper();
    }

    @Test
    void findAll_ReturnsListOfProdutos() throws Exception {
        ProdutoModel p1 = new ProdutoModel(1L, "Produto A", 10, 100.0);
        ProdutoModel p2 = new ProdutoModel(2L, "Produto B", 5, 50.0);

        when(produtoService.findAll()).thenReturn(List.of(p1, p2));

        mockMvc.perform(get("/api/emanager/produto/findAll"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(2))
                .andExpect(jsonPath("$[0].nome").value("Produto A"))
                .andExpect(jsonPath("$[1].nome").value("Produto B"));
    }

    @Test
    void findById_ValidId_ReturnsProduto() throws Exception {
        ProdutoModel produto = new ProdutoModel(1L, "Produto A", 10, 100.0);
        when(produtoService.findById(1L)).thenReturn(produto);

        mockMvc.perform(get("/api/emanager/produto/findById/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nome").value("Produto A"));
    }

    @Test
    void save_ValidProduto_ReturnsCreated() throws Exception {
        UsuarioModel usuario = new UsuarioModel(1L, "Usuário Teste", "teste@mail.com");
        ProdutoModel input = new ProdutoModel(null, "Produto A", 10, 100.0);
        ProdutoModel saved = new ProdutoModel(1L, "Produto A", 10, 100.0);

        when(usuarioService.findById(1L)).thenReturn(usuario);
        when(produtoService.save(input, usuario)).thenReturn(saved);

        mockMvc.perform(post("/api/emanager/produto/save/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nome").value("Produto A"));
    }

    @Test
    void update_ValidProduto_ReturnsUpdatedProduto() throws Exception {
        UsuarioModel usuario = new UsuarioModel(1L, "Usuário Teste", "teste@mail.com");
        ProdutoModel updates = new ProdutoModel(null, "Produto Atualizado", 15, 150.0);
        ProdutoModel updated = new ProdutoModel(1L, "Produto Atualizado", 15, 150.0);

        when(usuarioService.findById(1L)).thenReturn(usuario);
        when(produtoService.updateByID(1L, updates, usuario)).thenReturn(updated);

        mockMvc.perform(put("/api/emanager/produto/update/1/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updates)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nome").value("Produto Atualizado"));
    }

    @Test
    void getCurvaABC_ReturnsListOfCurvaDTO() throws Exception {
        ProdutoCurvaABCDTO dto1 = new ProdutoCurvaABCDTO("Produto A", 100.0, "A");
        ProdutoCurvaABCDTO dto2 = new ProdutoCurvaABCDTO("Produto B", 50.0, "B");

        when(produtoService.getCurvaABC()).thenReturn(List.of(dto1, dto2));

        mockMvc.perform(get("/api/emanager/produto/curva-abc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(2))
                .andExpect(jsonPath("$[0].nome").value("Produto A"))
                .andExpect(jsonPath("$[1].nome").value("Produto B"));
    }

    @Test
    void deleteById_ValidId_ReturnsNoContent() throws Exception {
        doNothing().when(produtoService).deleteById(1L);

        mockMvc.perform(delete("/api/emanager/produto/delete/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteById_InvalidId_ThrowsResourceNotFoundException() throws Exception {
        doThrow(ResourceNotFoundException.class).when(produtoService).deleteById(1L);

        mockMvc.perform(delete("/api/emanager/produto/delete/1"))
                .andExpect(status().isNotFound());
    }
}
