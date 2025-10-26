package com.example.EstoqueManager.controller;

import com.example.EstoqueManager.dto.ProdutoCurvaABCDTO;
import com.example.EstoqueManager.exception.ResourceNotFoundException;
import com.example.EstoqueManager.model.Cargo;
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
        ProdutoModel p1 = new ProdutoModel();
        p1.setId(1L);
        p1.setNome("Produto A");
        p1.setQuantidade(10);
        p1.setPreco(100.0);
        p1.setAtivo(true);

        ProdutoModel p2 = new ProdutoModel();
        p2.setId(2L);
        p2.setNome("Produto B");
        p2.setQuantidade(5);
        p2.setPreco(50.0);
        p2.setAtivo(true);

        when(produtoService.findAll()).thenReturn(List.of(p1, p2));

        mockMvc.perform(get("/api/emanager/produto/findAll"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(2))
                .andExpect(jsonPath("$[0].nome").value("Produto A"))
                .andExpect(jsonPath("$[1].nome").value("Produto B"));
    }

    @Test
    void findById_ValidId_ReturnsProduto() throws Exception {
        ProdutoModel produto = new ProdutoModel();
        produto.setId(1L);
        produto.setNome("Produto A");
        produto.setQuantidade(10);
        produto.setPreco(100.0);
        produto.setAtivo(true);

        when(produtoService.findById(1L)).thenReturn(produto);

        mockMvc.perform(get("/api/emanager/produto/findById/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nome").value("Produto A"));
    }

    @Test
    void save_ValidProduto_ReturnsCreated() throws Exception {
        UsuarioModel usuario = new UsuarioModel();
        usuario.setId(1L);
        usuario.setNome("Usuário Teste");
        usuario.setCpf("12345678901");
        usuario.setIdade(30);
        usuario.setLogin("usuario1");
        usuario.setSenha("senha123");
        usuario.setCargo(Cargo.VENDEDOR);

        ProdutoModel input = new ProdutoModel();
        input.setNome("Produto A");
        input.setQuantidade(10);
        input.setPreco(100.0);
        input.setAtivo(true);

        ProdutoModel saved = new ProdutoModel();
        saved.setId(1L);
        saved.setNome("Produto A");
        saved.setQuantidade(10);
        saved.setPreco(100.0);
        saved.setAtivo(true);

        when(usuarioService.findById(1L)).thenReturn(usuario);
        when(produtoService.save(any(ProdutoModel.class), any(UsuarioModel.class))).thenReturn(saved);

        mockMvc.perform(post("/api/emanager/produto/save/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nome").value("Produto A"));
    }

    @Test
    void update_ValidProduto_ReturnsUpdatedProduto() throws Exception {
        UsuarioModel usuario = new UsuarioModel();
        usuario.setId(1L);
        usuario.setNome("Usuário Teste");
        usuario.setCpf("12345678901");
        usuario.setIdade(30);
        usuario.setLogin("usuario1");
        usuario.setSenha("senha123");
        usuario.setCargo(Cargo.VENDEDOR);

        ProdutoModel updates = new ProdutoModel();
        updates.setNome("Produto Atualizado");
        updates.setQuantidade(15);
        updates.setPreco(150.0);
        updates.setAtivo(true);

        ProdutoModel updated = new ProdutoModel();
        updated.setId(1L);
        updated.setNome("Produto Atualizado");
        updated.setQuantidade(15);
        updated.setPreco(150.0);
        updated.setAtivo(true);

        when(usuarioService.findById(1L)).thenReturn(usuario);
        when(produtoService.updateByID(eq(1L), any(ProdutoModel.class), any(UsuarioModel.class))).thenReturn(updated);

        mockMvc.perform(put("/api/emanager/produto/update/1/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updates)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nome").value("Produto Atualizado"));
    }

    @Test
    void getCurvaABC_ReturnsListOfCurvaDTO() throws Exception {
        ProdutoCurvaABCDTO dto1 = new ProdutoCurvaABCDTO();
        dto1.setNome("Produto A");
        dto1.setValorTotalVendido(100.0);
        dto1.setClassificacao("A");

        ProdutoCurvaABCDTO dto2 = new ProdutoCurvaABCDTO();
        dto2.setNome("Produto B");
        dto2.setValorTotalVendido(50.0);
        dto2.setClassificacao("B");

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