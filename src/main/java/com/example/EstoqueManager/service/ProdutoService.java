package com.example.EstoqueManager.service;

import com.example.EstoqueManager.model.ProdutoModel;
import com.example.EstoqueManager.model.UsuarioModel;
import com.example.EstoqueManager.model.CategoriaModel;
import com.example.EstoqueManager.service.CategoriaService; // importe o serviço de categoria
import com.example.EstoqueManager.repository.ProdutoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProdutoService {

    private final ProdutoRepository produtoRepository;
    private final CategoriaService categoriaService; // injetar serviço de categoria

    public List<ProdutoModel> findAll() {
        return produtoRepository.findAll();
    }

    public ProdutoModel findById(Long id) {
        return produtoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Produto não encontrado com o ID: " + id));
    }

    public ProdutoModel save(ProdutoModel produto, UsuarioModel usuarioQueAlterou) {

        CategoriaModel categoria = categoriaService.findById(produto.getCategoria().getId());
        produto.setCategoria(categoria);

        produto.setUsuarioUltimaAlteracao(usuarioQueAlterou);
        produto.setDataUltimaAlteracao(LocalDateTime.now());
        return produtoRepository.save(produto);
    }

    public void deleteById(Long id) {
        if (!produtoRepository.existsById(id)) {
            throw new RuntimeException("Produto não encontrado com o ID: " + id);
        }
        produtoRepository.deleteById(id);
    }

    public ProdutoModel updateByID(Long id, ProdutoModel produtoUpdated, UsuarioModel usuarioQueAlterou) {
        ProdutoModel produtoExistente = produtoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Produto não encontrado com o ID: " + id));

        produtoExistente.setNome(produtoUpdated.getNome());
        produtoExistente.setQuantidade(produtoUpdated.getQuantidade());
        produtoExistente.setPreco(produtoUpdated.getPreco());
        produtoExistente.setCategoria(produtoUpdated.getCategoria());

        // Marca quem e quando alterou
        produtoExistente.setUsuarioUltimaAlteracao(usuarioQueAlterou);
        produtoExistente.setDataUltimaAlteracao(LocalDateTime.now());

        return produtoRepository.save(produtoExistente);
    }
}
