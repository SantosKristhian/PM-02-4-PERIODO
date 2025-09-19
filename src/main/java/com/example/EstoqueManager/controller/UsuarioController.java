package com.example.EstoqueManager.controller;

import com.example.EstoqueManager.model.UsuarioModel;
import com.example.EstoqueManager.service.UsuarioService;
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

    @GetMapping("/user/findAll")
    public ResponseEntity<List<UsuarioModel>> findAll() {
        try {
            var result = usuarioService.findAll();
            return new ResponseEntity<>(result, HttpStatus.OK);
        } catch (Exception ex) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/user/findById/{id}")
    public ResponseEntity<UsuarioModel> findById(@PathVariable Long id) {
        try {
            var result = usuarioService.findById(id);
            return new ResponseEntity<>(result, HttpStatus.OK);
        } catch (Exception ex) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/user/save")
    public ResponseEntity<UsuarioModel> save(@RequestBody UsuarioModel usuario) {
        try {
            return new ResponseEntity<>(usuarioService.save(usuario), HttpStatus.OK);
        } catch (Exception ex) {
            ex.printStackTrace(); // mostra no console o que está acontecendo
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    @DeleteMapping("/user/delete/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable Long id) {
        try {
            usuarioService.deleteById(id);
            return new ResponseEntity<>(null, HttpStatus.OK);
        } catch (Exception ex) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }


    @PutMapping("/user/update/{id}")
    public ResponseEntity<UsuarioModel> update(@PathVariable Long id,
                                               @RequestBody UsuarioModel usuarioUpdated) {
        try {
            var result = usuarioService.updateByID(id, usuarioUpdated);
            return new ResponseEntity<>(result, HttpStatus.OK);
        } catch (Exception ex) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }

    }



}