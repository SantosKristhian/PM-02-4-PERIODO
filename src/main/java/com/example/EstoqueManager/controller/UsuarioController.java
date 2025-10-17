
package com.example.EstoqueManager.controller;

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
@CrossOrigin(origins = "http://localhost:4200")
public class UsuarioController {

    private final UsuarioService usuarioService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody UsuarioModel usuario) {
        UsuarioModel usuarioEncontrado = usuarioService.autenticar(usuario.getLogin(), usuario.getSenha());

        if (usuarioEncontrado == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Login ou senha inválidos");
        }

        return ResponseEntity.ok(usuarioEncontrado);
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