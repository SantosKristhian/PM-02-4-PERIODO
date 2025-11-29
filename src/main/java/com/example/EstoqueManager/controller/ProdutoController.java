
package com.example.EstoqueManager.controller;

import com.example.EstoqueManager.dto.ProdutoCurvaABCDTO;
import com.example.EstoqueManager.model.ProdutoModel;
import com.example.EstoqueManager.model.UsuarioModel;
import com.example.EstoqueManager.service.ProdutoService;
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
@CrossOrigin(origins = "*", allowedHeaders = "*", allowCredentials = "false")
public class ProdutoController {

    private final ProdutoService produtoService;
    private final UsuarioService usuarioService;

    @GetMapping("/produto/findAll")
    public ResponseEntity<List<ProdutoModel>> findAll() {
        return ResponseEntity.ok(produtoService.findAll());
    }

    @GetMapping("/produto/findById/{id}")
    public ResponseEntity<ProdutoModel> findById(@PathVariable Long id) {
        return ResponseEntity.ok(produtoService.findById(id));
    }

    @PostMapping("/produto/save/{usuarioId}")
    public ResponseEntity<ProdutoModel> save(
            @PathVariable Long usuarioId,
            @Valid @RequestBody ProdutoModel produto) {

        UsuarioModel usuario = usuarioService.findById(usuarioId);
        return ResponseEntity.status(HttpStatus.CREATED).body(produtoService.save(produto, usuario));
    }

    @PutMapping("/produto/update/{id}/{usuarioId}")
    public ResponseEntity<ProdutoModel> update(
            @PathVariable Long id,
            @PathVariable Long usuarioId,
            @Valid @RequestBody ProdutoModel produtoUpdated) {

        UsuarioModel usuario = usuarioService.findById(usuarioId);
        return ResponseEntity.ok(produtoService.updateByID(id, produtoUpdated, usuario));
    }

    @GetMapping("produto/curva-abc")
    public ResponseEntity<List<ProdutoCurvaABCDTO>> getCurvaABC() {
        List<ProdutoCurvaABCDTO> curvaABC = produtoService.getCurvaABC();
        return ResponseEntity.ok(curvaABC);
    }


    @DeleteMapping("/produto/delete/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable Long id) {
        produtoService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}