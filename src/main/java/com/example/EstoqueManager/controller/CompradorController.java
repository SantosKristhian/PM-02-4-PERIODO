package com.example.EstoqueManager.controller;

import com.example.EstoqueManager.model.CompradorModel;
import com.example.EstoqueManager.service.CompradorService;
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
public class CompradorController {

    private final CompradorService compradorService;

    @GetMapping("/comprador/findAll")
    public ResponseEntity<List<CompradorModel>> findAll() {
        return ResponseEntity.ok(compradorService.findAll());
    }

    @GetMapping("/comprador/findById/{id}")
    public ResponseEntity<CompradorModel> findById(@PathVariable Long id) {
        return ResponseEntity.ok(compradorService.findById(id));
    }

    @PostMapping("/comprador/save")
    public ResponseEntity<CompradorModel> save(@Valid @RequestBody CompradorModel comprador) {
        return ResponseEntity.status(HttpStatus.CREATED).body(compradorService.save(comprador));
    }

    @PutMapping("/comprador/update/{id}")
    public ResponseEntity<CompradorModel> update(
            @PathVariable Long id,
            @Valid @RequestBody CompradorModel compradorUpdated) {
        return ResponseEntity.ok(compradorService.updateById(id, compradorUpdated));
    }

    @DeleteMapping("/comprador/delete/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable Long id) {
        compradorService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}