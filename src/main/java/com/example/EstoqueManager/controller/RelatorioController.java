package com.example.EstoqueManager.controller;

import com.example.EstoqueManager.model.VendaModel;
import com.example.EstoqueManager.model.ProdutoModel;
import com.example.EstoqueManager.model.CompradorModel;
import com.example.EstoqueManager.model.UsuarioModel;
import com.example.EstoqueManager.service.RelatorioService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/emanager/relatorios" )
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200" )

public class RelatorioController {

    private final RelatorioService relatorioService;

    // Relatório de Vendas por Período
    @GetMapping("/vendas-periodo")
    public ResponseEntity<Map<String, Object>> getVendasPorPeriodo(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFim) {
        try {
            Map<String, Object> relatorio = relatorioService.getVendasPorPeriodo(dataInicio, dataFim);
            return new ResponseEntity<>(relatorio, HttpStatus.OK);
        } catch (Exception ex) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }

    // Relatório de Vendas Diárias (últimos 30 dias)
    @GetMapping("/vendas-diarias")
    public ResponseEntity<List<Map<String, Object>>> getVendasDiarias() {
        try {
            List<Map<String, Object>> relatorio = relatorioService.getVendasDiarias();
            return new ResponseEntity<>(relatorio, HttpStatus.OK);
        } catch (Exception ex) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }

    // Produtos Mais Vendidos
    @GetMapping("/produtos-mais-vendidos")
    public ResponseEntity<List<Map<String, Object>>> getProdutosMaisVendidos(
            @RequestParam(defaultValue = "10") int limite) {
        try {
            List<Map<String, Object>> relatorio = relatorioService.getProdutosMaisVendidos(limite);
            return new ResponseEntity<>(relatorio, HttpStatus.OK);
        } catch (Exception ex) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }

    // Relatório de Estoque
    @GetMapping("/estoque")
    public ResponseEntity<List<Map<String, Object>>> getRelatorioEstoque() {
        try {
            List<Map<String, Object>> relatorio = relatorioService.getRelatorioEstoque();
            return new ResponseEntity<>(relatorio, HttpStatus.OK);
        } catch (Exception ex) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }

    // Produtos com Baixo Estoque
    @GetMapping("/estoque-baixo")
    public ResponseEntity<List<Map<String, Object>>> getProdutosBaixoEstoque(
            @RequestParam(defaultValue = "10") int limite) {
        try {
            List<Map<String, Object>> relatorio = relatorioService.getProdutosBaixoEstoque(limite);
            return new ResponseEntity<>(relatorio, HttpStatus.OK);
        } catch (Exception ex) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }

    // Vendas por Vendedor
    @GetMapping("/vendas-por-vendedor")
    public ResponseEntity<List<Map<String, Object>>> getVendasPorVendedor(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFim) {
        try {
            List<Map<String, Object>> relatorio = relatorioService.getVendasPorVendedor(dataInicio, dataFim);
            return new ResponseEntity<>(relatorio, HttpStatus.OK);
        } catch (Exception ex) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }

    // Relatório de Compradores
    @GetMapping("/compradores-ativos")
    public ResponseEntity<List<Map<String, Object>>> getCompradoresAtivos(
            @RequestParam(defaultValue = "10") int limite) {
        try {
            List<Map<String, Object>> relatorio = relatorioService.getCompradoresAtivos(limite);
            return new ResponseEntity<>(relatorio, HttpStatus.OK);
        } catch (Exception ex) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }

    // Dashboard - Resumo Geral
    @GetMapping("/dashboard")
    public ResponseEntity<Map<String, Object>> getDashboard() {
        try {
            Map<String, Object> dashboard = relatorioService.getDashboard();
            return new ResponseEntity<>(dashboard, HttpStatus.OK);
        } catch (Exception ex) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }

    // Relatório de Vendas por Mês (últimos 12 meses)
    @GetMapping("/vendas-mensais")
    public ResponseEntity<List<Map<String, Object>>> getVendasMensais() {
        try {
            List<Map<String, Object>> relatorio = relatorioService.getVendasMensais();
            return new ResponseEntity<>(relatorio, HttpStatus.OK);
        } catch (Exception ex) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }
}
