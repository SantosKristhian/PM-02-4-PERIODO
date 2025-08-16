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
    public ResponseEntity<VendaModel> criarVenda(
            @PathVariable Long usuarioId,
            @RequestBody VendaModel venda) {
        try {

            UsuarioModel usuario = new UsuarioModel();
            usuario.setId(usuarioId);
            venda.setUsuario(usuario);
            VendaModel vendaSalva = vendaService.registrarVenda(venda);

            return new ResponseEntity<>(vendaSalva, HttpStatus.CREATED);
        } catch (RuntimeException ex) {

            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        } catch (Exception ex) {

            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PatchMapping("/venda/update/{id}")
    public ResponseEntity<VendaModel> updateVenda(
            @PathVariable Long id,
            @RequestBody VendaModel vendaAtualizada) {
        try {
            VendaModel vendaSalva = vendaService.updateVenda(id, vendaAtualizada);
                return new ResponseEntity<>(vendaSalva, HttpStatus.OK);
            } catch (RuntimeException ex) {
                return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
            } catch (Exception ex) {
                return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
            }
    }

}