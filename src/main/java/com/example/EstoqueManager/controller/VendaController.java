package com.example.EstoqueManager.controller;

import com.example.EstoqueManager.dto.VendaRequestDTO;
import com.example.EstoqueManager.dto.VendaResponseDTO;
import com.example.EstoqueManager.model.Cargo;
import com.example.EstoqueManager.model.VendaModel;
import com.example.EstoqueManager.model.UsuarioModel;
import com.example.EstoqueManager.service.ProdutoService;
import com.example.EstoqueManager.service.UsuarioService;
import com.example.EstoqueManager.service.VendaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/emanager")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", allowedHeaders = "*", allowCredentials = "false")
@PreAuthorize("hasAnyAuthority('ADM','VENDEDOR')")
public class VendaController {

    private final VendaService vendaService;
    private final UsuarioService usuarioService;
    private final ProdutoService produtoService;


    @GetMapping("/venda/findAll")
    public ResponseEntity<List<VendaResponseDTO>> findAll() {
        List<VendaResponseDTO> vendas = vendaService.listarVendas().stream()
                .map(vendaService::converterParaDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(vendas);
    }

    @GetMapping("/venda/findById/{id}")
    public ResponseEntity<VendaResponseDTO> findById(@PathVariable Long id) {
        VendaModel venda = vendaService.buscarVendaPorId(id);
        return ResponseEntity.ok(vendaService.converterParaDTO(venda));
    }

    @PostMapping("/venda/save/{usuarioId}")
    public ResponseEntity<VendaResponseDTO> criarVenda(
            @PathVariable Long usuarioId,
            @Valid @RequestBody VendaRequestDTO vendaRequestDTO,
            @AuthenticationPrincipal UsuarioModel usuarioAutenticado) {

        boolean isAdm = usuarioAutenticado.getCargo() == Cargo.ADM;
        if (!isAdm && !usuarioAutenticado.getId().equals(usuarioId)) {
            throw new AccessDeniedException("Voce so pode registrar vendas em seu proprio nome.");
        }

        VendaModel venda = vendaService.criarVendaAPartirDTO(vendaRequestDTO, usuarioId);

        return ResponseEntity.status(HttpStatus.CREATED).body(vendaService.converterParaDTO(venda));
    }

    @PutMapping("/venda/update/{id}")
    public ResponseEntity<VendaResponseDTO> updateVenda(
            @PathVariable Long id,
            @Valid @RequestBody VendaModel vendaAtualizada) {

        VendaModel venda = vendaService.updateVenda(id, vendaAtualizada);
        return ResponseEntity.ok(vendaService.converterParaDTO(venda));
    }
}