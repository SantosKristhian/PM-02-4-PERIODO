package com.example.EstoqueManager.controller;

import com.example.EstoqueManager.model.UsuarioModel;
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

class UsuarioControllerTest {

    private MockMvc mockMvc;

    @InjectMocks
    private UsuarioController usuarioController;

    @Mock
    private UsuarioService usuarioService;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(usuarioController).build();
        objectMapper = new ObjectMapper();
    }

    @Test
    void login_ValidUser_ReturnsOk() throws Exception {
        UsuarioModel input = new UsuarioModel(null, "usuario1", "senha123");
        UsuarioModel usuarioAutenticado = new UsuarioModel(1L, "usuario1", "senha123");

        when(usuarioService.autenticar("usuario1", "senha123")).thenReturn(usuarioAutenticado);

        mockMvc.perform(post("/api/emanager/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.login").value("usuario1"));
    }

    @Test
    void login_InvalidUser_ReturnsUnauthorized() throws Exception {
        UsuarioModel input = new UsuarioModel(null, "usuario1", "senhaErrada");

        when(usuarioService.autenticar("usuario1", "senhaErrada")).thenReturn(null);

        mockMvc.perform(post("/api/emanager/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string("Login ou senha inválidos"));
    }

    @Test
    void findAll_ReturnsListOfUsuarios() throws Exception {
        UsuarioModel u1 = new UsuarioModel(1L, "usuario1", "senha123");
        UsuarioModel u2 = new UsuarioModel(2L, "usuario2", "senha456");

        when(usuarioService.findAll()).thenReturn(List.of(u1, u2));

        mockMvc.perform(get("/api/emanager/user/findAll"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(2))
                .andExpect(jsonPath("$[0].login").value("usuario1"))
                .andExpect(jsonPath("$[1].login").value("usuario2"));
    }

    @Test
    void findById_ValidId_ReturnsUsuario() throws Exception {
        UsuarioModel usuario = new UsuarioModel(1L, "usuario1", "senha123");
        when(usuarioService.findById(1L)).thenReturn(usuario);

        mockMvc.perform(get("/api/emanager/user/findById/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.login").value("usuario1"));
    }

    @Test
    void save_ValidUsuario_ReturnsCreated() throws Exception {
        UsuarioModel input = new UsuarioModel(null, "usuario1", "senha123");
        UsuarioModel saved = new UsuarioModel(1L, "usuario1", "senha123");

        when(usuarioService.save(input)).thenReturn(saved);

        mockMvc.perform(post("/api/emanager/user/save")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.login").value("usuario1"));
    }

    @Test
    void existemUsuarios_ReturnsBoolean() throws Exception {
        when(usuarioService.existemUsuarios()).thenReturn(true);

        mockMvc.perform(get("/api/emanager/user/exists"))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));
    }

    @Test
    void existeAdministrador_ReturnsBoolean() throws Exception {
        when(usuarioService.existeAdministrador()).thenReturn(false);

        mockMvc.perform(get("/api/emanager/user/exists-admin"))
                .andExpect(status().isOk())
                .andExpect(content().string("false"));
    }

    @Test
    void update_ValidUsuario_ReturnsUpdated() throws Exception {
        UsuarioModel updates = new UsuarioModel(null, "usuarioAtualizado", "novaSenha");
        UsuarioModel updated = new UsuarioModel(1L, "usuarioAtualizado", "novaSenha");

        when(usuarioService.updateByID(1L, updates)).thenReturn(updated);

        mockMvc.perform(put("/api/emanager/user/update/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updates)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.login").value("usuarioAtualizado"));
    }

    @Test
    void deleteById_ValidId_ReturnsNoContent() throws Exception {
        doNothing().when(usuarioService).deleteById(1L);

        mockMvc.perform(delete("/api/emanager/user/delete/1"))
                .andExpect(status().isNoContent());
    }
}
