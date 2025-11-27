package com.example.EstoqueManager.controller;

import com.example.EstoqueManager.dto.ProdutoCurvaABCDTO;
import com.example.EstoqueManager.model.VendaModel;
import com.example.EstoqueManager.model.UsuarioModel;
import com.example.EstoqueManager.service.ProdutoService;
import com.example.EstoqueManager.service.UsuarioService;
import com.example.EstoqueManager.service.VendaService;
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
public class VendaController {

    private final VendaService vendaService;
    private final UsuarioService usuarioService;
    private final ProdutoService produtoService;


    @GetMapping("/venda/findAll")
    public ResponseEntity<List<VendaModel>> findAll() {
        return ResponseEntity.ok(vendaService.listarVendas());
    }

    @GetMapping("/venda/findById/{id}")
    public ResponseEntity<VendaModel> findById(@PathVariable Long id) {
        return ResponseEntity.ok(vendaService.buscarVendaPorId(id));
    }

    @PostMapping("/venda/save/{usuarioId}")
    public ResponseEntity<VendaModel> criarVenda(
            @PathVariable Long usuarioId,
            @Valid @RequestBody VendaModel venda) {

        UsuarioModel usuario = usuarioService.findById(usuarioId);
        venda.setUsuario(usuario);

        return ResponseEntity.status(HttpStatus.CREATED).body(vendaService.registrarVenda(venda));
    }

    @PutMapping("/venda/update/{id}")
    public ResponseEntity<VendaModel> updateVenda(
            @PathVariable Long id,
            @Valid @RequestBody VendaModel vendaAtualizada) {

        return ResponseEntity.ok(vendaService.updateVenda(id, vendaAtualizada));
    }
}