package com.example.EstoqueManager.dto.auditoria;

import com.example.EstoqueManager.model.ProdutoModel;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProdutoAuditSnapshot {
    private Long id;
    private String nome;
    private Integer quantidade;
    private Double preco;
    private Boolean ativo;
    private Long categoriaId;
    private String categoriaNome;

    public static ProdutoAuditSnapshot de(ProdutoModel produto) {
        ProdutoAuditSnapshot snapshot = new ProdutoAuditSnapshot();
        snapshot.setId(produto.getId());
        snapshot.setNome(produto.getNome());
        snapshot.setQuantidade(produto.getQuantidade());
        snapshot.setPreco(produto.getPreco());
        snapshot.setAtivo(produto.getAtivo());
        if (produto.getCategoria() != null) {
            snapshot.setCategoriaId(produto.getCategoria().getId());
            snapshot.setCategoriaNome(produto.getCategoria().getNome());
        }
        return snapshot;
    }
}