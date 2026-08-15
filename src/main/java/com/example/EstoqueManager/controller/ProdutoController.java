package com.example.EstoqueManager.controller;

import com.example.EstoqueManager.dto.ProdutoCurvaABCDTO;
import com.example.EstoqueManager.dto.ProdutoResponseDTO;
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

    // ENDPOINTS NOVOS QUE RETORNAM DTOS (COM CATEGORIA)

    @GetMapping("/produto/findAll")
    public ResponseEntity<List<ProdutoResponseDTO>> findAll() {
        List<ProdutoResponseDTO> produtosDTO = produtoService.findAllComCategoria();
        return ResponseEntity.ok(produtosDTO);
    }

    @GetMapping("/produto/findById/{id}")
    public ResponseEntity<ProdutoResponseDTO> findById(@PathVariable Long id) {
        ProdutoResponseDTO produtoDTO = produtoService.findByIdComCategoria(id);
        return ResponseEntity.ok(produtoDTO);
    }

    @PostMapping("/produto/save/{usuarioId}")
    public ResponseEntity<ProdutoResponseDTO> save(
            @PathVariable Long usuarioId,
            @Valid @RequestBody ProdutoModel produto) {

        UsuarioModel usuario = usuarioService.findById(usuarioId);
        ProdutoModel produtoSalvo = produtoService.save(produto, usuario);
        ProdutoResponseDTO produtoDTO = produtoService.converterParaDTO(produtoSalvo);
        return ResponseEntity.status(HttpStatus.CREATED).body(produtoDTO);
    }

    @PutMapping("/produto/update/{id}/{usuarioId}")
    public ResponseEntity<ProdutoResponseDTO> update(
            @PathVariable Long id,
            @PathVariable Long usuarioId,
            @Valid @RequestBody ProdutoModel produtoUpdated) {

        UsuarioModel usuario = usuarioService.findById(usuarioId);
        ProdutoModel produtoAtualizado = produtoService.updateByID(id, produtoUpdated, usuario);
        ProdutoResponseDTO produtoDTO = produtoService.converterParaDTO(produtoAtualizado);
        return ResponseEntity.ok(produtoDTO);
    }

    @GetMapping("/produto/curva-abc")
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