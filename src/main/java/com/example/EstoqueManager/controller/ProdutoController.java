package com.example.EstoqueManager.controller;

import com.example.EstoqueManager.model.ProdutoModel;
import com.example.EstoqueManager.model.UsuarioModel;
import com.example.EstoqueManager.service.ProdutoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/emanager")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")

public class ProdutoController {

    private final ProdutoService produtoService;

    @GetMapping("/produto/findAll")
    public ResponseEntity<List<ProdutoModel>> findAll() {
        try {
            return new ResponseEntity<>(produtoService.findAll(), HttpStatus.OK);
        } catch (Exception ex) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/produto/findById/{id}")
    public ResponseEntity<ProdutoModel> findById(@PathVariable Long id) {
        try {
            return new ResponseEntity<>(produtoService.findById(id), HttpStatus.OK);
        } catch (Exception ex) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/produto/save/{usuarioId}")
    public ResponseEntity<ProdutoModel> save(
            @PathVariable Long usuarioId,
            @RequestBody ProdutoModel produto) {
        try {
            UsuarioModel usuario = new UsuarioModel();
            usuario.setId(usuarioId); // apenas para associar, sem buscar no banco
            return new ResponseEntity<>(produtoService.save(produto, usuario), HttpStatus.OK);
        } catch (Exception ex) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/produto/update/{id}/{usuarioId}")
    public ResponseEntity<ProdutoModel> update(
            @PathVariable Long id,
            @PathVariable Long usuarioId,
            @RequestBody ProdutoModel produtoUpdated) {
        try {
            UsuarioModel usuario = new UsuarioModel();
            usuario.setId(usuarioId); // apenas para associar
            return new ResponseEntity<>(produtoService.updateByID(id, produtoUpdated, usuario), HttpStatus.OK);
        } catch (Exception ex) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/produto/delete/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable Long id) {
        try {
            produtoService.deleteById(id);
            return new ResponseEntity<>(null, HttpStatus.OK);
        } catch (Exception ex) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }
}
