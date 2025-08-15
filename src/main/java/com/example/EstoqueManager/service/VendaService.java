package com.example.EstoqueManager.service;

import com.example.EstoqueManager.model.ItemVendaModel;
import com.example.EstoqueManager.model.ProdutoModel;
import com.example.EstoqueManager.model.VendaModel;
import com.example.EstoqueManager.repository.VendaRepository;
import com.example.EstoqueManager.repository.ProdutoRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class VendaService {

    private final VendaRepository vendaRepository;
    private final ProdutoRepository produtoRepository;

    public List<VendaModel> listarVendas() {
        return vendaRepository.findAll();
    }

    public VendaModel buscarVendaPorId(Long id) {
        return vendaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Venda não encontrada"));
    }

    @Transactional
    public VendaModel registrarVenda(VendaModel venda) {
        // Define a data da venda se não estiver setada
        if (venda.getData() == null) {
            venda.setData(LocalDate.now());
        }

        double total = 0;

        // Associa cada item à venda, calcula subtotal e atualiza estoque
        for (ItemVendaModel item : venda.getItens()) {
            ProdutoModel produto = produtoRepository.findById(item.getProduto().getId())
                    .orElseThrow(() -> new RuntimeException("Produto não encontrado: " + item.getProduto().getId()));

            if (item.getQuantidade() > produto.getQuantidade()) {
                throw new RuntimeException("Estoque insuficiente para o produto: " + produto.getNome());
            }

            // Atualiza estoque
            produto.setQuantidade(produto.getQuantidade() - item.getQuantidade());
            produtoRepository.save(produto);

            // Define preço unitário e subtotal do item
            item.setPrecoUnitario(produto.getPreco());
            item.calcularSubtotal();

            // Associa item à venda
            item.setVenda(venda);

            // Soma o subtotal ao total da venda
            total += item.getSubtotal();
        }

        venda.setValortotal(total);

        // Salva a venda com todos os itens
        return vendaRepository.save(venda);
    }
}
