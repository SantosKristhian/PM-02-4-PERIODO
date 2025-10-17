package com.example.EstoqueManager.service;

import com.example.EstoqueManager.exception.BusinessException;
import com.example.EstoqueManager.exception.ResourceNotFoundException;
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
        if (id == null || id <= 0) {
            throw new BusinessException("ID inválido. Deve ser um número positivo.");
        }

        return compradorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Comprador não encontrado com ID: " + id));
    }

    public CompradorModel save(CompradorModel comprador) {
        validarComprador(comprador);

        if (comprador.getId() != null) {
            throw new BusinessException("ID deve ser nulo ao criar um novo comprador.");
        }

        return compradorRepository.save(comprador);
    }

    public void deleteById(Long id) {
        if (id == null || id <= 0) {
            throw new BusinessException("ID inválido. Deve ser um número positivo.");
        }

        if (!compradorRepository.existsById(id)) {
            throw new ResourceNotFoundException("Comprador não encontrado com ID: " + id);
        }

        compradorRepository.deleteById(id);
    }

    public CompradorModel updateById(Long id, CompradorModel compradorUpdated) {
        if (id == null || id <= 0) {
            throw new BusinessException("ID inválido. Deve ser um número positivo.");
        }

        validarComprador(compradorUpdated);

        CompradorModel compradorExistente = compradorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Comprador não encontrado com ID: " + id));

        compradorExistente.setNome(compradorUpdated.getNome());
        compradorExistente.setCpf(compradorUpdated.getCpf());
        compradorExistente.setEmail(compradorUpdated.getEmail());

        return compradorRepository.save(compradorExistente);
    }

    private void validarComprador(CompradorModel comprador) {
        if (comprador == null) {
            throw new BusinessException("Comprador não pode ser nulo.");
        }

        if (comprador.getNome() == null || comprador.getNome().trim().isEmpty()) {
            throw new BusinessException("Nome do comprador é obrigatório.");
        }

        if (comprador.getCpf() == null || comprador.getCpf().trim().isEmpty()) {
            throw new BusinessException("CPF do comprador é obrigatório.");
        }

        if (comprador.getEmail() == null || comprador.getEmail().trim().isEmpty()) {
            throw new BusinessException("Email do comprador é obrigatório.");
        }
    }
}