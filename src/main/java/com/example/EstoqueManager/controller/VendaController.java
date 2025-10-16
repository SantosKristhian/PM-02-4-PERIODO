package com.example.EstoqueManager.controller;

import com.example.EstoqueManager.model.VendaModel;
import com.example.EstoqueManager.model.UsuarioModel;
import com.example.EstoqueManager.repository.UsuarioRepository;
import com.example.EstoqueManager.service.UsuarioService;
import com.example.EstoqueManager.service.VendaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/emanager")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
public class VendaController {

    private final VendaService vendaService;
    private final UsuarioRepository usuarioRepository;

    @GetMapping("/venda/findAll")
    public ResponseEntity<?> findAll() {
        try {
            List<VendaModel> vendas = vendaService.listarVendas();
            return ResponseEntity.ok(vendas);
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Erro ao buscar vendas: " + ex.getMessage());
        }
    }

    @GetMapping("/venda/findById/{id}")
    public ResponseEntity<?> findById(@PathVariable Long id) {
        try {
            VendaModel venda = vendaService.buscarVendaPorId(id);
            if (venda == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Venda não encontrada para o ID: " + id);
            }
            return ResponseEntity.ok(venda);
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Erro ao buscar venda: " + ex.getMessage());
        }
    }

    @PostMapping("/venda/save/{usuarioId}")
    public ResponseEntity<?> criarVenda(
            @PathVariable Long usuarioId,
            @RequestBody VendaModel venda) {
        try {
            UsuarioModel usuario = usuarioRepository.findById(usuarioId)
                    .orElse(null);

            if (usuario == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Usuário não encontrado para o ID: " + usuarioId);
            }

            if (venda.getItens() == null || venda.getItens().isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("A venda deve conter ao menos um item.");
            }

            if (venda.getComprador() == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Comprador não informado.");
            }

            venda.setUsuario(usuario);
            VendaModel vendaSalva = vendaService.registrarVenda(venda);

            if (vendaSalva == null) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body("Erro ao salvar venda.");
            }

            return ResponseEntity.status(HttpStatus.CREATED).body(vendaSalva);
        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Erro de negócio: " + ex.getMessage());
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro inesperado: " + ex.getMessage());
        }
    }

    @PutMapping("/venda/update/{id}")
    public ResponseEntity<?> updateVenda(
            @PathVariable Long id,
            @RequestBody VendaModel vendaAtualizada) {
        try {
            VendaModel vendaExistente = vendaService.buscarVendaPorId(id);
            if (vendaExistente == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Venda não encontrada para o ID: " + id);
            }

            VendaModel vendaSalva = vendaService.updateVenda(id, vendaAtualizada);
            if (vendaSalva == null) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body("Erro ao atualizar venda.");
            }

            return ResponseEntity.ok(vendaSalva);
        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Erro de negócio: " + ex.getMessage());
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro inesperado: " + ex.getMessage());
        }
    }
}