package com.example.EstoqueManager.controller;

import com.example.EstoqueManager.model.CategoriaModel;
import com.example.EstoqueManager.service.CategoriaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/emanager")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", allowedHeaders = "*", allowCredentials = "false")
public class CategoriaController {

    private final CategoriaService categoriaService;

    @PreAuthorize("hasAnyAuthority('ADM','VENDEDOR')")
    @GetMapping("/categoria/findAll")
    public ResponseEntity<List<CategoriaModel>> findAll() {
        return ResponseEntity.ok(categoriaService.findAll());
    }

    @PreAuthorize("hasAnyAuthority('ADM','VENDEDOR')")
    @GetMapping("/categoria/findById/{id}")
    public ResponseEntity<CategoriaModel> findById(@PathVariable Long id) {
        return ResponseEntity.ok(categoriaService.findById(id));
    }

    @PreAuthorize("hasAuthority('ADM')")
    @PostMapping("/categoria/save")
    public ResponseEntity<CategoriaModel> save(@Valid @RequestBody CategoriaModel categoria) {
        return ResponseEntity.status(HttpStatus.CREATED).body(categoriaService.save(categoria));
    }

    @PreAuthorize("hasAuthority('ADM')")
    @PutMapping("/categoria/update/{id}")
    public ResponseEntity<CategoriaModel> update(
            @PathVariable Long id,
            @Valid @RequestBody CategoriaModel categoriaUpdated) {
        return ResponseEntity.ok(categoriaService.updateById(id, categoriaUpdated));
    }

    @PreAuthorize("hasAuthority('ADM')")
    @DeleteMapping("/categoria/delete/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable Long id) {
        categoriaService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}