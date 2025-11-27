
package com.example.EstoqueManager.controller;

import com.example.EstoqueManager.model.Cargo;
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
/*
    @Test
    void login_ValidUser_ReturnsOk() throws Exception {
        UsuarioModel input = new UsuarioModel();
        input.setLogin("usuario1");
        input.setSenha("senha123");

        UsuarioModel usuarioAutenticado = new UsuarioModel();
        usuarioAutenticado.setId(1L);
        usuarioAutenticado.setNome("João Silva");
        usuarioAutenticado.setCpf("12345678901");
        usuarioAutenticado.setIdade(30);
        usuarioAutenticado.setLogin("usuario1");
        usuarioAutenticado.setSenha("senha123");
        usuarioAutenticado.setCargo(Cargo.VENDEDOR);

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
        UsuarioModel input = new UsuarioModel();
        input.setLogin("usuario1");
        input.setSenha("senhaErrada");

        when(usuarioService.autenticar("usuario1", "senhaErrada")).thenReturn(null);

        mockMvc.perform(post("/api/emanager/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string("Login ou senha inválidos"));
    }
*/
    @Test
    void findAll_ReturnsListOfUsuarios() throws Exception {
        UsuarioModel u1 = new UsuarioModel();
        u1.setId(1L);
        u1.setNome("João Silva");
        u1.setCpf("12345678901");
        u1.setIdade(30);
        u1.setLogin("usuario1");
        u1.setSenha("senha123");
        u1.setCargo(Cargo.VENDEDOR);

        UsuarioModel u2 = new UsuarioModel();
        u2.setId(2L);
        u2.setNome("Maria Santos");
        u2.setCpf("98765432100");
        u2.setIdade(25);
        u2.setLogin("usuario2");
        u2.setSenha("senha456");
        u2.setCargo(Cargo.ADM);

        when(usuarioService.findAll()).thenReturn(List.of(u1, u2));

        mockMvc.perform(get("/api/emanager/user/findAll"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(2))
                .andExpect(jsonPath("$[0].login").value("usuario1"))
                .andExpect(jsonPath("$[1].login").value("usuario2"));
    }

    @Test
    void findById_ValidId_ReturnsUsuario() throws Exception {
        UsuarioModel usuario = new UsuarioModel();
        usuario.setId(1L);
        usuario.setNome("João Silva");
        usuario.setCpf("12345678901");
        usuario.setIdade(30);
        usuario.setLogin("usuario1");
        usuario.setSenha("senha123");
        usuario.setCargo(Cargo.VENDEDOR);

        when(usuarioService.findById(1L)).thenReturn(usuario);

        mockMvc.perform(get("/api/emanager/user/findById/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.login").value("usuario1"));
    }

    @Test
    void save_ValidUsuario_ReturnsCreated() throws Exception {
        UsuarioModel input = new UsuarioModel();
        input.setNome("João Silva");
        input.setCpf("12345678901");
        input.setIdade(30);
        input.setLogin("usuario1");
        input.setSenha("senha123");
        input.setCargo(Cargo.VENDEDOR);

        UsuarioModel saved = new UsuarioModel();
        saved.setId(1L);
        saved.setNome("João Silva");
        saved.setCpf("12345678901");
        saved.setIdade(30);
        saved.setLogin("usuario1");
        saved.setSenha("senha123");
        saved.setCargo(Cargo.VENDEDOR);

        when(usuarioService.save(any(UsuarioModel.class))).thenReturn(saved);

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
        UsuarioModel updates = new UsuarioModel();
        updates.setNome("João Silva Atualizado");
        updates.setCpf("12345678901");
        updates.setIdade(31);
        updates.setLogin("usuarioAtualizado");
        updates.setSenha("novaSenha");
        updates.setCargo(Cargo.ADM);

        UsuarioModel updated = new UsuarioModel();
        updated.setId(1L);
        updated.setNome("João Silva Atualizado");
        updated.setCpf("12345678901");
        updated.setIdade(31);
        updated.setLogin("usuarioAtualizado");
        updated.setSenha("novaSenha");
        updated.setCargo(Cargo.ADM);

        when(usuarioService.updateByID(eq(1L), any(UsuarioModel.class))).thenReturn(updated);

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