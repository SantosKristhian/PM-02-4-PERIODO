package com.example.EstoqueManager.controller;

import com.example.EstoqueManager.model.VendaModel;
import com.example.EstoqueManager.model.UsuarioModel;
import com.example.EstoqueManager.service.ProdutoService;
import com.example.EstoqueManager.service.UsuarioService;
import com.example.EstoqueManager.service.VendaService;
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

class VendaControllerTest {

    private MockMvc mockMvc;

    @InjectMocks
    private VendaController vendaController;

    @Mock
    private VendaService vendaService;

    @Mock
    private UsuarioService usuarioService;

    @Mock
    private ProdutoService produtoService;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(vendaController).build();
        objectMapper = new ObjectMapper();
    }

    @Test
    void findAll_ReturnsListOfVendas() throws Exception {
        VendaModel v1 = new VendaModel(1L, null, 100.0);
        VendaModel v2 = new VendaModel(2L, null, 50.0);

        when(vendaService.listarVendas()).thenReturn(List.of(v1, v2));

        mockMvc.perform(get("/api/emanager/venda/findAll"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(2))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[1].id").value(2));
    }

    @Test
    void findById_ValidId_ReturnsVenda() throws Exception {
        VendaModel venda = new VendaModel(1L, null, 100.0);
        when(vendaService.buscarVendaPorId(1L)).thenReturn(venda);

        mockMvc.perform(get("/api/emanager/venda/findById/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.valorTotal").value(100.0));
    }

    @Test
    void criarVenda_ValidVenda_ReturnsCreated() throws Exception {
        UsuarioModel usuario = new UsuarioModel(1L, "usuario1", "senha123");
        VendaModel input = new VendaModel(null, null, 100.0);
        VendaModel saved = new VendaModel(1L, usuario, 100.0);

        when(usuarioService.findById(1L)).thenReturn(usuario);
        when(vendaService.registrarVenda(any(VendaModel.class))).thenReturn(saved);

        mockMvc.perform(post("/api/emanager/venda/save/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.usuario.id").value(1))
                .andExpect(jsonPath("$.valorTotal").value(100.0));
    }

    @Test
    void updateVenda_ValidVenda_ReturnsUpdated() throws Exception {
        VendaModel updates = new VendaModel(null, null, 150.0);
        VendaModel updated = new VendaModel(1L, null, 150.0);

        when(vendaService.updateVenda(1L, updates)).thenReturn(updated);

        mockMvc.perform(put("/api/emanager/venda/update/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updates)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.valorTotal").value(150.0));
    }
}
