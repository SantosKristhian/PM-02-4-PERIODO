package com.example.EstoqueManager.controller;

import com.example.EstoqueManager.model.AuditoriaModel;
import com.example.EstoqueManager.service.AuditoriaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/emanager/auditoria")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", allowedHeaders = "*", allowCredentials = "false")
@PreAuthorize("hasAuthority('ADM')")
public class AuditoriaController {

    private final AuditoriaService auditoriaService;

    @GetMapping("/findAll")
    public ResponseEntity<List<AuditoriaModel>> findAll() {
        return ResponseEntity.ok(auditoriaService.listarTudo());
    }

    @GetMapping("/entidade/{entidade}")
    public ResponseEntity<List<AuditoriaModel>> findByEntidade(@PathVariable String entidade) {
        return ResponseEntity.ok(auditoriaService.listarPorEntidade(entidade.toUpperCase()));
    }

    @GetMapping("/entidade/{entidade}/{entidadeId}")
    public ResponseEntity<List<AuditoriaModel>> findByRegistro(
            @PathVariable String entidade,
            @PathVariable Long entidadeId) {
        return ResponseEntity.ok(auditoriaService.listarPorRegistro(entidade.toUpperCase(), entidadeId));
    }
}