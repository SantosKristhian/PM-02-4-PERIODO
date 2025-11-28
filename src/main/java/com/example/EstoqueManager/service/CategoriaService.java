package com.example.EstoqueManager.service;

import com.example.EstoqueManager.exception.BusinessException;
import com.example.EstoqueManager.exception.ResourceNotFoundException;
import com.example.EstoqueManager.model.CategoriaModel;
import com.example.EstoqueManager.repository.CategoriaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoriaService {

    private final CategoriaRepository categoriaRepository;

    public List<CategoriaModel> findAll() {
        return categoriaRepository.findAll();
    }

    public CategoriaModel findById(Long id) {
        if (id == null || id <= 0) {
            throw new BusinessException("ID inválido. Deve ser um número positivo.");
        }

        return categoriaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Categoria não encontrada com ID: " + id));
    }

    public CategoriaModel save(CategoriaModel categoria) {
        if (categoria == null) {
            throw new BusinessException("Categoria não pode ser nula.");
        }

        if (categoria.getId() != null) {
            throw new BusinessException("ID deve ser nulo ao criar uma nova categoria.");
        }

        if (categoria.getNome() == null || categoria.getNome().trim().isEmpty()) {
            throw new BusinessException("Nome da categoria é obrigatório.");
        }

        // NOVO: Validação de nome duplicado
        if (categoriaRepository.findByNome(categoria.getNome()).isPresent()) {
            throw new BusinessException("Já existe uma categoria com este nome.");
        }

        return categoriaRepository.save(categoria);
    }

    public void deleteById(Long id) {
        if (id == null || id <= 0) {
            throw new BusinessException("ID inválido. Deve ser um número positivo.");
        }

        if (!categoriaRepository.existsById(id)) {
            throw new ResourceNotFoundException("Categoria não encontrada com ID: " + id);
        }

        categoriaRepository.deleteById(id);
    }

    public CategoriaModel updateById(Long id, CategoriaModel categoriaUpdated) {
        if (id == null || id <= 0) {
            throw new BusinessException("ID inválido. Deve ser um número positivo.");
        }

        if (categoriaUpdated == null) {
            throw new BusinessException("Categoria não pode ser nula.");
        }

        if (categoriaUpdated.getNome() == null || categoriaUpdated.getNome().trim().isEmpty()) {
            throw new BusinessException("Nome da categoria é obrigatório.");
        }

        // NOVO: Validação de nome duplicado em outra categoria
        // O método findByNomeAndIdNot está no seu Repository
        if (categoriaRepository.findByNomeAndIdNot(categoriaUpdated.getNome(), id).isPresent()) {
            throw new BusinessException("Já existe outra categoria com este nome.");
        }

        CategoriaModel categoriaExistente = categoriaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Categoria não encontrada com ID: " + id));

        categoriaExistente.setNome(categoriaUpdated.getNome());

        return categoriaRepository.save(categoriaExistente);
    }
}
