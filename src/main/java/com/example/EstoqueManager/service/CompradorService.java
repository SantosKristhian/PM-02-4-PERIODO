package com.example.EstoqueManager.service;

import com.example.EstoqueManager.dto.CompradorRelatorioDTO;
import com.example.EstoqueManager.model.CompradorModel;
import com.example.EstoqueManager.repository.CompradorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CompradorService {

    private final CompradorRepository compradorRepository;

    public List<CompradorModel> findAll() {
        return compradorRepository.findAll();
    }

    public CompradorModel findById(Long id) {
        return compradorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Comprador não encontrado com o ID: " + id));
    }

    public CompradorModel save(CompradorModel comprador) {
        return compradorRepository.save(comprador);
    }

    public void deleteById(Long id) {
        if (!compradorRepository.existsById(id)) {
            throw new RuntimeException("Comprador não encontrado com o ID: " + id);
        }
        compradorRepository.deleteById(id);
    }

    public CompradorModel updateById(Long id, CompradorModel compradorUpdated) {
        CompradorModel compradorExistente = compradorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Comprador não encontrado com o ID: " + id));

        compradorExistente.setNome(compradorUpdated.getNome());
        compradorExistente.setCpf(compradorUpdated.getCpf());
        compradorExistente.setEmail(compradorUpdated.getEmail());

        return compradorRepository.save(compradorExistente);
    }

    // Relatório simples
    public List<CompradorRelatorioDTO> gerarRelatorioCompradores() {
        List<CompradorModel> compradores = compradorRepository.findAll();

        return compradores.stream().map(comprador -> {
            int totalVendas = comprador.getVendas() != null ? comprador.getVendas().size() : 0;
            double valorTotal = 0;
            if (comprador.getVendas() != null) {
                valorTotal = comprador.getVendas().stream()
                        .mapToDouble(venda -> venda.getValortotal())
                        .sum();
            }
            return new CompradorRelatorioDTO(
                    comprador.getId(),
                    comprador.getNome(),
                    comprador.getCpf(),
                    comprador.getEmail(),
                    totalVendas,
                    valorTotal
            );
        }).toList();
    }
}
