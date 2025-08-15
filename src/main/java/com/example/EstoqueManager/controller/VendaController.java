package com.example.EstoqueManager.controller;

import com.example.EstoqueManager.model.VendaModel;
import com.example.EstoqueManager.model.UsuarioModel;
import com.example.EstoqueManager.service.VendaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/emanager")
@RequiredArgsConstructor
public class VendaController {

    private final VendaService vendaService;

    @GetMapping("/venda/findAll")
    public ResponseEntity<List<VendaModel>> findAll() {
        try {
            List<VendaModel> vendas = (List<VendaModel>) vendaService.listarVendas();
            return new ResponseEntity<>(vendas, HttpStatus.OK);
        } catch (Exception ex) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/venda/findById/{id}")
    public ResponseEntity<VendaModel> findById(@PathVariable Long id) {
        try {
            return new ResponseEntity<>(vendaService.buscarVendaPorId(id), HttpStatus.OK);
        } catch (Exception ex) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/venda/save/{usuarioId}")
    public ResponseEntity<VendaModel> save(
            @PathVariable Long usuarioId,
            @RequestBody VendaModel venda) {
        try {
            UsuarioModel usuario = new UsuarioModel();
            usuario.setId(usuarioId); // apenas para associar
            venda.setUsuario(usuario);
            return new ResponseEntity<>(vendaService.registrarVenda(venda), HttpStatus.OK);
        } catch (Exception ex) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }

    // Para atualização de venda, se necessário
    @PutMapping("/venda/update/{id}/{usuarioId}")
    public ResponseEntity<VendaModel> update(
            @PathVariable Long id,
            @PathVariable Long usuarioId,
            @RequestBody VendaModel vendaUpdated) {
        try {
            UsuarioModel usuario = new UsuarioModel();
            usuario.setId(usuarioId);
            vendaUpdated.setUsuario(usuario);

            return new ResponseEntity<>(vendaService.registrarVenda(vendaUpdated), HttpStatus.OK);
        } catch (Exception ex) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/venda/delete/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable Long id) {
        try {
            // Se quiser deletar uma venda, adicione método deleteById no VendaService
            return new ResponseEntity<>(null, HttpStatus.OK);
        } catch (Exception ex) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }
}
