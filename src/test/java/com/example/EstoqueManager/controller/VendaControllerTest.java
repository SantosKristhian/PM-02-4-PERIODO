package com.example.EstoqueManager.controller;

import com.example.EstoqueManager.model.Cargo;
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
        VendaModel v1 = new VendaModel();
        v1.setId(1L);
        v1.setValortotal(100.0);
        v1.setAtivo(true);

        VendaModel v2 = new VendaModel();
        v2.setId(2L);
        v2.setValortotal(50.0);
        v2.setAtivo(true);

        when(vendaService.listarVendas()).thenReturn(List.of(v1, v2));

        mockMvc.perform(get("/api/emanager/venda/findAll"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(2))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[1].id").value(2));
    }

    @Test
    void findById_ValidId_ReturnsVenda() throws Exception {
        VendaModel venda = new VendaModel();
        venda.setId(1L);
        venda.setValortotal(100.0);
        venda.setAtivo(true);

        when(vendaService.buscarVendaPorId(1L)).thenReturn(venda);

        mockMvc.perform(get("/api/emanager/venda/findById/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.valortotal").value(100.0));
    }

    @Test
    void criarVenda_ValidVenda_ReturnsCreated() throws Exception {
        UsuarioModel usuario = new UsuarioModel();
        usuario.setId(1L);
        usuario.setNome("João Silva");
        usuario.setCpf("12345678901");
        usuario.setIdade(30);
        usuario.setLogin("usuario1");
        usuario.setSenha("senha123");
        usuario.setCargo(Cargo.VENDEDOR);

        VendaModel input = new VendaModel();
        input.setValortotal(100.0);
        input.setAtivo(true);

        VendaModel saved = new VendaModel();
        saved.setId(1L);
        saved.setUsuario(usuario);
        saved.setValortotal(100.0);
        saved.setAtivo(true);

        when(usuarioService.findById(1L)).thenReturn(usuario);
        when(vendaService.registrarVenda(any(VendaModel.class))).thenReturn(saved);

        mockMvc.perform(post("/api/emanager/venda/save/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.usuario.id").value(1))
                .andExpect(jsonPath("$.valortotal").value(100.0));
    }

    @Test
    void updateVenda_ValidVenda_ReturnsUpdated() throws Exception {
        VendaModel updates = new VendaModel();
        updates.setValortotal(150.0);
        updates.setAtivo(true);

        VendaModel updated = new VendaModel();
        updated.setId(1L);
        updated.setValortotal(150.0);
        updated.setAtivo(true);

        when(vendaService.updateVenda(eq(1L), any(VendaModel.class))).thenReturn(updated);

        mockMvc.perform(put("/api/emanager/venda/update/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updates)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.valortotal").value(150.0));
    }
}