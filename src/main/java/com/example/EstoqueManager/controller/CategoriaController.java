package com.example.EstoqueManager.controller;

import com.example.EstoqueManager.model.CategoriaModel;
import com.example.EstoqueManager.service.CategoriaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/emanager")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")

public class CategoriaController {

    private final CategoriaService categoriaService;

    @GetMapping("/categoria/findAll")
    public ResponseEntity<List<CategoriaModel>> findAll() {
        try {
            return new ResponseEntity<>(categoriaService.findAll(), HttpStatus.OK);
        } catch (Exception ex) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/categoria/findById/{id}")
    public ResponseEntity<CategoriaModel> findById(@PathVariable Long id) {
        try {
            return new ResponseEntity<>(categoriaService.findById(id), HttpStatus.OK);
        } catch (Exception ex) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/categoria/save")
    public ResponseEntity<CategoriaModel> save(@RequestBody CategoriaModel categoria) {
        try {
            return new ResponseEntity<>(categoriaService.save(categoria), HttpStatus.OK);
        } catch (Exception ex) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/categoria/update/{id}")
    public ResponseEntity<CategoriaModel> update(@PathVariable Long id, @RequestBody CategoriaModel categoriaUpdated) {
        try {
            return new ResponseEntity<>(categoriaService.updateById(id, categoriaUpdated), HttpStatus.OK);
        } catch (Exception ex) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/categoria/delete/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable Long id) {
        try {
            categoriaService.deleteById(id);
            return new ResponseEntity<>(null, HttpStatus.OK);
        } catch (Exception ex) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }
}
