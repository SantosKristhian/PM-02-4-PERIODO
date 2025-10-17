package com.example.EstoqueManager.service;

import com.example.EstoqueManager.exception.BusinessException;
import com.example.EstoqueManager.exception.ResourceNotFoundException;
import com.example.EstoqueManager.model.ProdutoModel;
import com.example.EstoqueManager.model.UsuarioModel;
import com.example.EstoqueManager.repository.CategoriaRepository;
import com.example.EstoqueManager.repository.ItemVendaRepository;
import com.example.EstoqueManager.repository.ProdutoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProdutoService {

    private final ProdutoRepository produtoRepository;
    private final CategoriaRepository categoriaRepository;
    private final ItemVendaRepository itemVendaRepository;

    public List<ProdutoModel> findAll() {
        return produtoRepository.findAll();
    }

    public ProdutoModel findById(Long id) {
        if (id == null || id <= 0) {
            throw new BusinessException("ID inválido. Deve ser um número positivo.");
        }

        return produtoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Produto não encontrado com ID: " + id));
    }

    public ProdutoModel save(ProdutoModel produto, UsuarioModel usuario) {
        validarProduto(produto);

        if (produto.getId() != null) {
            throw new BusinessException("ID deve ser nulo ao criar um novo produto.");
        }

        if (usuario == null || usuario.getId() == null) {
            throw new BusinessException("Usuário responsável é obrigatório.");
        }

        if (!categoriaRepository.existsById(produto.getCategoria().getId())) {
            throw new ResourceNotFoundException("Categoria não encontrada com ID: " + produto.getCategoria().getId());
        }

        produto.setUsuarioUltimaAlteracao(usuario);
        produto.setDataUltimaAlteracao(LocalDateTime.now());
        produto.setAtivo(true); // Define como ativo ao criar

        return produtoRepository.save(produto);
    }

    public ProdutoModel updateByID(Long id, ProdutoModel produtoUpdated, UsuarioModel usuario) {
        if (id == null || id <= 0) {
            throw new BusinessException("ID inválido. Deve ser um número positivo.");
        }

        validarProduto(produtoUpdated);

        if (usuario == null || usuario.getId() == null) {
            throw new BusinessException("Usuário responsável é obrigatório.");
        }

        ProdutoModel produtoExistente = produtoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Produto não encontrado com ID: " + id));

        if (!categoriaRepository.existsById(produtoUpdated.getCategoria().getId())) {
            throw new ResourceNotFoundException("Categoria não encontrada com ID: " + produtoUpdated.getCategoria().getId());
        }

        produtoExistente.setNome(produtoUpdated.getNome());
        produtoExistente.setQuantidade(produtoUpdated.getQuantidade());
        produtoExistente.setPreco(produtoUpdated.getPreco());
        produtoExistente.setCategoria(produtoUpdated.getCategoria());
        produtoExistente.setAtivo(produtoUpdated.getAtivo()); // ADICIONE ESTA LINHA
        produtoExistente.setUsuarioUltimaAlteracao(usuario);
        produtoExistente.setDataUltimaAlteracao(LocalDateTime.now());

        return produtoRepository.save(produtoExistente);
    }

    public void deleteById(Long id) {
        if (id == null || id <= 0) {
            throw new BusinessException("ID inválido. Deve ser um número positivo.");
        }

        ProdutoModel produto = produtoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Produto não encontrado com ID: " + id));

        // Verifica se o produto está em alguma venda
        boolean produtoEmUso = itemVendaRepository.findAll().stream()
                .anyMatch(item -> item.getProduto().getId().equals(id));

        if (produtoEmUso) {
            // Soft delete - marca como inativo ao invés de deletar
            produto.setAtivo(false);
            produto.setDataUltimaAlteracao(LocalDateTime.now());
            produtoRepository.save(produto);
        } else {
            // Hard delete - deleta realmente se não está em nenhuma venda
            produtoRepository.deleteById(id);
        }
    }

    private void validarProduto(ProdutoModel produto) {
        if (produto == null) {
            throw new BusinessException("Produto não pode ser nulo.");
        }

        if (produto.getNome() == null || produto.getNome().trim().isEmpty()) {
            throw new BusinessException("Nome do produto é obrigatório.");
        }

        if (produto.getQuantidade() == null || produto.getQuantidade() < 0) {
            throw new BusinessException("Quantidade deve ser maior ou igual a zero.");
        }

        if (produto.getPreco() == null || produto.getPreco() <= 0) {
            throw new BusinessException("Preço deve ser maior que zero.");
        }

        if (produto.getCategoria() == null || produto.getCategoria().getId() == null) {
            throw new BusinessException("Categoria é obrigatória.");
        }
    }
}