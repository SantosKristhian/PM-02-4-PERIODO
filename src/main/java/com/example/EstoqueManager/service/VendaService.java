package com.example.EstoqueManager.service;

import com.example.EstoqueManager.exception.BusinessException;
import com.example.EstoqueManager.exception.ResourceNotFoundException;
import com.example.EstoqueManager.model.*;
import com.example.EstoqueManager.repository.CompradorRepository;
import com.example.EstoqueManager.repository.UsuarioRepository;
import com.example.EstoqueManager.repository.VendaRepository;
import com.example.EstoqueManager.repository.ProdutoRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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
        if (id == null || id <= 0) {
            throw new BusinessException("ID inválido. Deve ser um número positivo.");
        }

        return vendaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Venda não encontrada com ID: " + id));
    }

    @Transactional
    public VendaModel registrarVenda(VendaModel venda) {
        validarVenda(venda);

        // Define data se não foi informada
        if (venda.getData() == null) {
            venda.setData(LocalDateTime.now());
        }

        // Valida e busca o usuário
        if (venda.getUsuario() == null || venda.getUsuario().getId() == null) {
            throw new BusinessException("Usuário responsável pela venda é obrigatório.");
        }

        UsuarioModel usuario = usuarioRepository.findById(venda.getUsuario().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado com ID: " + venda.getUsuario().getId()));
        venda.setUsuario(usuario);

        // Processa o comprador (OPCIONAL)
        processarComprador(venda);

        // Processa os itens da venda
        double total = processarItensVenda(venda);

        venda.setValortotal(total);
        processarPagamento(venda);

        venda.setAtivo(true);

        return vendaRepository.save(venda);
    }

    private void processarPagamento(VendaModel venda) {
        if (venda.getMetodoPagamento() == MetodoPagamento.DINHEIRO) {
            // Para dinheiro, o valor pago é obrigatório
            if (venda.getValorPago() == null || venda.getValorPago() <= 0) {
                throw new BusinessException("Valor pago é obrigatório para pagamento em dinheiro.");
            }

            // Valida se o valor pago é suficiente
            if (venda.getValorPago() < venda.getValortotal()) {
                throw new BusinessException(
                        String.format("Valor pago (R$ %.2f) é insuficiente. Total da venda: R$ %.2f",
                                venda.getValorPago(), venda.getValortotal())
                );
            }

            // Calcula o troco automaticamente
            venda.setTroco(venda.getValorPago() - venda.getValortotal());

        } else {
            // Para outros métodos, não há troco
            venda.setValorPago(venda.getValortotal());
            venda.setTroco(0.0);
        }
    }


    @Transactional
    public VendaModel updateVenda(Long id, VendaModel vendaAtualizada) {
        if (id == null || id <= 0) {
            throw new BusinessException("ID inválido. Deve ser um número positivo.");
        }

        VendaModel vendaExistente = vendaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Venda não encontrada com ID: " + id));

        if (vendaAtualizada.getData() != null) {
            vendaExistente.setData(vendaAtualizada.getData());
        }

        if (vendaAtualizada.getUsuario() != null && vendaAtualizada.getUsuario().getId() != null) {
            UsuarioModel usuario = usuarioRepository.findById(vendaAtualizada.getUsuario().getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado com ID: " + vendaAtualizada.getUsuario().getId()));
            vendaExistente.setUsuario(usuario);
        }

        // Atualiza comprador (pode ser null, removendo o comprador)
        if (vendaAtualizada.getComprador() != null) {
            if (vendaAtualizada.getComprador().getId() != null) {
                CompradorModel comprador = compradorRepository.findById(vendaAtualizada.getComprador().getId())
                        .orElseThrow(() -> new ResourceNotFoundException("Comprador não encontrado com ID: " + vendaAtualizada.getComprador().getId()));
                vendaExistente.setComprador(comprador);
            } else {
                // Novo comprador sendo adicionado na atualização
                validarDadosNovoComprador(vendaAtualizada.getComprador());
                CompradorModel novoComprador = compradorRepository.save(vendaAtualizada.getComprador());
                vendaExistente.setComprador(novoComprador);
            }
        } else {
            // Remove o comprador se vier null
            vendaExistente.setComprador(null);
        }

        if (vendaAtualizada.getItens() != null && !vendaAtualizada.getItens().isEmpty()) {
            // Devolve o estoque dos itens antigos
            devolverEstoqueItensAntigos(vendaExistente);

            vendaExistente.getItens().clear();

            // Processa os novos itens
            vendaAtualizada.getItens().forEach(item -> item.setVenda(vendaExistente));
            double total = processarItensVenda(vendaAtualizada);

            vendaExistente.getItens().addAll(vendaAtualizada.getItens());
            vendaExistente.setValortotal(total);
        }

        // Atualiza método de pagamento
        if (vendaAtualizada.getMetodoPagamento() != null) {
            vendaExistente.setMetodoPagamento(vendaAtualizada.getMetodoPagamento());
            vendaExistente.setValorPago(vendaAtualizada.getValorPago());
            processarPagamento(vendaExistente);
        }


        vendaExistente.setAtivo(vendaAtualizada.isAtivo());

        return vendaRepository.save(vendaExistente);
    }

    private void validarVenda(VendaModel venda) {
        if (venda == null) {
            throw new BusinessException("Venda não pode ser nula.");
        }

        if (venda.getItens() == null || venda.getItens().isEmpty()) {
            throw new BusinessException("A venda deve conter ao menos um item.");
        }

        // Valida método de pagamento
        if (venda.getMetodoPagamento() == null) {
            throw new BusinessException("Método de pagamento é obrigatório.");
        }


        // Valida cada item
        for (ItemVendaModel item : venda.getItens()) {
            if (item.getProduto() == null || item.getProduto().getId() == null) {
                throw new BusinessException("Produto inválido no item da venda.");
            }

            if (item.getQuantidadeVendida() == null || item.getQuantidadeVendida() <= 0) {
                throw new BusinessException("Quantidade vendida deve ser maior que zero.");
            }
        }
    }

    private void processarComprador(VendaModel venda) {
        // Se o comprador não foi informado, a venda prossegue sem comprador
        if (venda.getComprador() == null) {
            return;
        }

        // Se tem ID, é um comprador existente
        if (venda.getComprador().getId() != null) {
            CompradorModel comprador = compradorRepository.findById(venda.getComprador().getId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Comprador não encontrado com ID: " + venda.getComprador().getId()));
            venda.setComprador(comprador);
        } else {
            // É um novo comprador, valida os dados obrigatórios
            validarDadosNovoComprador(venda.getComprador());
            CompradorModel novoComprador = compradorRepository.save(venda.getComprador());
            venda.setComprador(novoComprador);
        }
    }

    private void validarDadosNovoComprador(CompradorModel comprador) {
        if (comprador.getNome() == null || comprador.getNome().trim().isEmpty()) {
            throw new BusinessException("Nome do comprador é obrigatório para cadastro.");
        }

        if (comprador.getCpf() == null || comprador.getCpf().trim().isEmpty()) {
            throw new BusinessException("CPF do comprador é obrigatório para cadastro.");
        }

        if (comprador.getEmail() == null || comprador.getEmail().trim().isEmpty()) {
            throw new BusinessException("Email do comprador é obrigatório para cadastro.");
        }
    }

    private double processarItensVenda(VendaModel venda) {
        double total = 0.0;

        for (ItemVendaModel item : venda.getItens()) {
            ProdutoModel produto = produtoRepository.findById(item.getProduto().getId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Produto não encontrado com ID: " + item.getProduto().getId()));

            if (item.getQuantidadeVendida() > produto.getQuantidade()) {
                throw new BusinessException(
                        "Estoque insuficiente para o produto: " + produto.getNome() +
                                ". Disponível: " + produto.getQuantidade() +
                                ", Solicitado: " + item.getQuantidadeVendida());
            }

            produto.setQuantidade(produto.getQuantidade() - item.getQuantidadeVendida());
            produtoRepository.save(produto);

            item.setPrecoVendido(produto.getPreco());
            item.setVenda(venda);

            double subtotal = item.getQuantidadeVendida() * item.getPrecoVendido();
            total += subtotal;
        }

        return total;
    }

    private void devolverEstoqueItensAntigos(VendaModel venda) {
        for (ItemVendaModel itemAntigo : venda.getItens()) {
            ProdutoModel produto = itemAntigo.getProduto();
            produto.setQuantidade(produto.getQuantidade() + itemAntigo.getQuantidadeVendida());
            produtoRepository.save(produto);
        }
    }
}