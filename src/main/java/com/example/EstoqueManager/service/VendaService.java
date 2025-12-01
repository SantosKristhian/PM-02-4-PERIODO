package com.example.EstoqueManager.service;

import com.example.EstoqueManager.dto.VendaRequestDTO;
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
import java.util.ArrayList;
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


    @Transactional
    public VendaModel criarVendaAPartirDTO(VendaRequestDTO vendaDTO, Long usuarioId) {
        // Validações básicas do DTO
        if (vendaDTO == null) {
            throw new BusinessException("Dados da venda são obrigatórios.");
        }

        if (vendaDTO.getMetodoPagamento() == null) {
            throw new BusinessException("Método de pagamento é obrigatório.");
        }

        if (vendaDTO.getItens() == null || vendaDTO.getItens().isEmpty()) {
            throw new BusinessException("A venda deve conter pelo menos um item.");
        }

        // Validação para pagamento em dinheiro
        if (vendaDTO.getMetodoPagamento() == MetodoPagamento.DINHEIRO) {
            if (vendaDTO.getValorPago() == null) {
                throw new BusinessException("Valor pago é obrigatório para pagamento em dinheiro.");
            }
        }

        // Busca e valida usuário
        UsuarioModel usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado com ID: " + usuarioId));

        // Cria a venda base
        VendaModel venda = new VendaModel();
        venda.setData(LocalDateTime.now());
        venda.setUsuario(usuario);
        venda.setAtivo(true);
        venda.setItensDevolvidos(false);
        venda.setMetodoPagamento(vendaDTO.getMetodoPagamento());
        venda.setValorPago(vendaDTO.getValorPago());

        // Processa comprador (se fornecido)
        if (vendaDTO.getCompradorId() != null) {
            CompradorModel comprador = compradorRepository.findById(vendaDTO.getCompradorId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Comprador não encontrado com ID: " + vendaDTO.getCompradorId()));
            venda.setComprador(comprador);
        }

        // Processa itens da venda
        List<ItemVendaModel> itens = new ArrayList<>();
        double total = 0.0;

        for (VendaRequestDTO.ItemVendaRequestDTO itemDTO : vendaDTO.getItens()) {
            // Busca produto
            ProdutoModel produto = produtoRepository.findById(itemDTO.getProdutoId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Produto não encontrado com ID: " + itemDTO.getProdutoId()));

            // Valida produto
            validarProdutoParaVenda(produto, itemDTO.getQuantidadeVendida());

            // Cria item de venda
            ItemVendaModel item = new ItemVendaModel();
            item.setProduto(produto);
            item.setVenda(venda);
            item.setQuantidadeVendida(itemDTO.getQuantidadeVendida());

            // Define preço: usa o fornecido ou o preço atual do produto
            Double precoVendido = itemDTO.getPrecoVendido() != null ?
                    itemDTO.getPrecoVendido() : produto.getPreco();
            item.setPrecoVendido(precoVendido);

            itens.add(item);

            // Calcula subtotal
            total += precoVendido * itemDTO.getQuantidadeVendida();

            // Atualiza estoque
            produto.setQuantidade(produto.getQuantidade() - itemDTO.getQuantidadeVendida());
            produtoRepository.save(produto);
        }

        // Configura venda
        venda.setItens(itens);
        venda.setValortotal(total);

        // Processa informações de pagamento
        if (venda.getMetodoPagamento() == MetodoPagamento.DINHEIRO) {
            if (venda.getValorPago() == null) {
                throw new BusinessException("Valor pago é obrigatório para pagamento em dinheiro.");
            }

            if (venda.getValorPago() < total) {
                throw new BusinessException(
                        String.format("Valor pago (R$ %.2f) é menor que o total da venda (R$ %.2f)",
                                venda.getValorPago(), total));
            }

            // Calcula troco
            venda.setTroco(venda.getValorPago() - total);
        } else {
            // Para outros métodos, o valor pago é igual ao total
            venda.setValorPago(total);
            venda.setTroco(0.0);
        }

        // Validações finais
        if (venda.getUsuario() == null) {
            throw new BusinessException("Usuário responsável pela venda é obrigatório.");
        }

        return vendaRepository.save(venda);
    }

    // Método auxiliar para validar produto (pode já existir no seu service)
    private void validarProdutoParaVenda(ProdutoModel produto, Integer quantidade) {
        if (!produto.getAtivo()) {
            throw new BusinessException("Produto " + produto.getNome() + " está inativo.");
        }

        if (produto.getQuantidade() < quantidade) {
            throw new BusinessException(
                    String.format("Estoque insuficiente para produto %s. Disponível: %d, Solicitado: %d",
                            produto.getNome(), produto.getQuantidade(), quantidade));
        }
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

        // Verifica se está tentando cancelar uma venda já cancelada
        if (!vendaExistente.isAtivo() && !vendaAtualizada.isAtivo()) {
            throw new BusinessException("Esta venda já está cancelada.");
        }

        // LÓGICA DE CANCELAMENTO
        if (vendaExistente.isAtivo() && !vendaAtualizada.isAtivo()) {
            // Está cancelando a venda

            // Valida se informou sobre devolução de itens
            if (vendaAtualizada.getItensDevolvidos() == null) {
                throw new BusinessException(
                        "Ao cancelar uma venda, é obrigatório informar se os itens foram devolvidos (itensDevolvidos: true/false)."
                );
            }

            // Se for para devolver itens ao estoque
            if (vendaAtualizada.getItensDevolvidos()) {
                devolverEstoqueItensAntigos(vendaExistente);
                vendaExistente.setItensDevolvidos(true);
            } else {
                vendaExistente.setItensDevolvidos(false);
            }

            // Marca a venda como cancelada
            vendaExistente.setAtivo(false);

            return vendaRepository.save(vendaExistente);
        }

        // LÓGICA DE ATUALIZAÇÃO NORMAL (se a venda ainda está ativa)
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