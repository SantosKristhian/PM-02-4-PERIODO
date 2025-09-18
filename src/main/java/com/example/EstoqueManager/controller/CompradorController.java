package com.example.EstoqueManager.controller;

/*import com.example.EstoqueManager.dto.CompradorRelatorioDTO;*/
import com.example.EstoqueManager.model.CompradorModel;
import com.example.EstoqueManager.service.CompradorService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/emanager")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")

public class CompradorController {

    private final CompradorService compradorService;

    @GetMapping("/comprador/findAll")
    public ResponseEntity<List<CompradorModel>> findAll() {
        try {
            return new ResponseEntity<>(compradorService.findAll(), HttpStatus.OK);
        } catch (Exception ex) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/comprador/findById/{id}")
    public ResponseEntity<CompradorModel> findById(@PathVariable Long id) {
        try {
            return new ResponseEntity<>(compradorService.findById(id), HttpStatus.OK);
        } catch (Exception ex) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/comprador/save")
    public ResponseEntity<CompradorModel> save(@RequestBody CompradorModel comprador) {
        try {
            return new ResponseEntity<>(compradorService.save(comprador), HttpStatus.OK);
        } catch (Exception ex) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/comprador/update/{id}")
    public ResponseEntity<CompradorModel> update(@PathVariable Long id, @RequestBody CompradorModel compradorUpdated) {
        try {
            return new ResponseEntity<>(compradorService.updateById(id, compradorUpdated), HttpStatus.OK);
        } catch (Exception ex) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/comprador/delete/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable Long id) {
        try {
            compradorService.deleteById(id);
            return new ResponseEntity<>(null, HttpStatus.OK);
        } catch (Exception ex) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }

   /* // Endpoint do relatório
    @GetMapping("/comprador/relatorio")
    public ResponseEntity<List<CompradorRelatorioDTO>> relatorioCompradores() {
        try {
            return new ResponseEntity<>(compradorService.gerarRelatorioCompradores(), HttpStatus.OK);
        } catch (Exception ex) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }*/
}
