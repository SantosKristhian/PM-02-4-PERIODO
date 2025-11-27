
package com.example.EstoqueManager.controller;

import com.example.EstoqueManager.auth.LoginDTO;
import com.example.EstoqueManager.auth.LoginService;
import com.example.EstoqueManager.model.UsuarioModel;
import com.example.EstoqueManager.service.UsuarioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/emanager")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class UsuarioController {

    private final UsuarioService usuarioService;
    private final LoginService loginService;

    @PostMapping("/login")
    public ResponseEntity<String> logar(@RequestBody LoginDTO loginDTO) {

        String token = loginService.logar(loginDTO);
        return new ResponseEntity<>(token, HttpStatus.OK);
    }
    
    @GetMapping("/user/findAll")
    public ResponseEntity<List<UsuarioModel>> findAll() {
        return ResponseEntity.ok(usuarioService.findAll());
    }

    @GetMapping("/user/findById/{id}")
    public ResponseEntity<UsuarioModel> findById(@PathVariable Long id) {
        return ResponseEntity.ok(usuarioService.findById(id));
    }

    @PostMapping("/user/save")
    public ResponseEntity<UsuarioModel> save(@Valid @RequestBody UsuarioModel usuario) {
        return ResponseEntity.status(HttpStatus.CREATED).body(usuarioService.save(usuario));
    }

    @GetMapping("/user/exists")
    public ResponseEntity<Boolean> existemUsuarios() {
        return ResponseEntity.ok(usuarioService.existemUsuarios());
    }

    @GetMapping("/user/exists-admin")
    public ResponseEntity<Boolean> existeAdministrador() {
        return ResponseEntity.ok(usuarioService.existeAdministrador());
    }


    @DeleteMapping("/user/delete/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable Long id) {
        usuarioService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/user/update/{id}")
    public ResponseEntity<UsuarioModel> update(
            @PathVariable Long id,
            @Valid @RequestBody UsuarioModel usuarioUpdated) {
        return ResponseEntity.ok(usuarioService.updateByID(id, usuarioUpdated));
    }
}