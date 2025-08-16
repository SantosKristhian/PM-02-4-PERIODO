package com.example.EstoqueManager.service;

import com.example.EstoqueManager.model.*;
import com.example.EstoqueManager.repository.CompradorRepository;
import com.example.EstoqueManager.repository.UsuarioRepository;
import com.example.EstoqueManager.repository.VendaRepository;
import com.example.EstoqueManager.repository.ProdutoRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class VendaService {

    private final VendaRepository vendaRepository;
    private final ProdutoRepository produtoRepository;
    private final UsuarioRepository usuarioRepository;
    private final CompradorRepository compradorRepository;

    public List<VendaModel> listarVendas() {
        return vendaRepository.findAll();
    }

    public VendaModel buscarVendaPorId(Long id) {
        return vendaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Venda não encontrada"));

    }

    @Transactional
    public VendaModel registrarVenda(VendaModel venda) {

        boolean vendaExiste = venda.getData() == null;

        if (vendaExiste) {
            venda.setData(LocalDateTime.now());
        }

        if (venda.getUsuario() == null || venda.getUsuario().getId() == null) {
            throw new RuntimeException("Usuário responsável pela venda é obrigatório.");
        }
        UsuarioModel usuario = usuarioRepository.findById(venda.getUsuario().getId())
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado: " + venda.getUsuario().getId()));
        venda.setUsuario(usuario);


        if (venda.getComprador() != null && venda.getComprador().getId() != null) {
            CompradorModel comprador = compradorRepository.findById(venda.getComprador().getId())
                    .orElseThrow(() -> new RuntimeException("Comprador não encontrado: " + venda.getComprador().getId()));
            venda.setComprador(comprador);
        } else {
            venda.setComprador(null);
        }

        double total = 0.0;

        for (ItemVendaModel item : venda.getItens()) {
            ProdutoModel produto = produtoRepository.findById(item.getProduto().getId())
                    .orElseThrow(() -> new RuntimeException("Produto não encontrado: " + item.getProduto().getId()));

            if (item.getQuantidade() > produto.getQuantidade()) {
                throw new RuntimeException("Estoque insuficiente para o produto: " + produto.getNome());
            }

            produto.setQuantidade(produto.getQuantidade() - item.getQuantidade());
            produtoRepository.save(produto);
            item.setPrecoVendido(produto.getPreco());
            item.setVenda(venda);
            double subtotal = item.getQuantidade() * item.getPrecoVendido();
            total += subtotal;
        }
        venda.setValortotal(total);
        venda.setAtivo(true);
        return vendaRepository.save(venda);
    }

    @Transactional
    public VendaModel updateVenda(Long id, VendaModel vendaAtualizada) {

        VendaModel vendaExistente = vendaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Venda não encontrada: " + id));

        if (vendaAtualizada.getData() != null) {
            vendaExistente.setData(vendaAtualizada.getData());
        }

        if (vendaAtualizada.getUsuario() != null && vendaAtualizada.getUsuario().getId() != null) {
            UsuarioModel usuario = usuarioRepository.findById(vendaAtualizada.getUsuario().getId())
                    .orElseThrow(() -> new RuntimeException("Usuário não encontrado: " + vendaAtualizada.getUsuario().getId()));
            vendaExistente.setUsuario(usuario);
        }

        if (vendaAtualizada.getComprador() != null && vendaAtualizada.getComprador().getId() != null) {
            CompradorModel comprador = compradorRepository.findById(vendaAtualizada.getComprador().getId())
                    .orElseThrow(() -> new RuntimeException("Comprador não encontrado: " + vendaAtualizada.getComprador().getId()));
            vendaExistente.setComprador(comprador);
        }

        if (vendaAtualizada.getItens() != null && !vendaAtualizada.getItens().isEmpty()) {
            for (ItemVendaModel itemAntigo : vendaExistente.getItens()) {
                ProdutoModel produto = itemAntigo.getProduto();
                produto.setQuantidade(produto.getQuantidade() + itemAntigo.getQuantidade());
                produtoRepository.save(produto);
            }
            vendaExistente.getItens().clear();

            double total = 0.0;
            for (ItemVendaModel item : vendaAtualizada.getItens()) {
                ProdutoModel produto = produtoRepository.findById(item.getProduto().getId())
                        .orElseThrow(() -> new RuntimeException("Produto não encontrado: " + item.getProduto().getId()));

                if (item.getQuantidade() > produto.getQuantidade()) {
                    throw new RuntimeException("Estoque insuficiente para o produto: " + produto.getNome());
                }

                produto.setQuantidade(produto.getQuantidade() - item.getQuantidade());
                produtoRepository.save(produto);

                item.setPrecoVendido(produto.getPreco());
                item.setVenda(vendaExistente);
                total += item.getQuantidade() * item.getPrecoVendido();

                vendaExistente.getItens().add(item);
            }
            vendaExistente.setValortotal(total);
        }

        vendaExistente.setAtivo(vendaAtualizada.isAtivo());

        return vendaRepository.save(vendaExistente);
    }





}
