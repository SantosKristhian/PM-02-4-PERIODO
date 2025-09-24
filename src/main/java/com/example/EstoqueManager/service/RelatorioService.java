package com.example.EstoqueManager.service;

import com.example.EstoqueManager.model.*;
import com.example.EstoqueManager.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RelatorioService {

    private final VendaRepository vendaRepository;
    private final ProdutoRepository produtoRepository;
    private final CompradorRepository compradorRepository;
    private final UsuarioRepository usuarioRepository;

    // Relatório de Vendas por Período
    public Map<String, Object> getVendasPorPeriodo(LocalDate dataInicio, LocalDate dataFim) {
        LocalDateTime inicio = dataInicio.atStartOfDay();
        LocalDateTime fim = dataFim.atTime(23, 59, 59);
        
        List<VendaModel> vendas = vendaRepository.findByDataBetweenAndAtivoTrue(inicio, fim);
        
        Map<String, Object> resultado = new HashMap<>();
        resultado.put("totalVendas", vendas.size());
        resultado.put("valorTotal", vendas.stream().mapToDouble(VendaModel::getValortotal).sum());
        resultado.put("dataInicio", dataInicio);
        resultado.put("dataFim", dataFim);
        resultado.put("vendas", vendas);
        
        return resultado;
    }

    // Vendas Diárias (últimos 30 dias)
    public List<Map<String, Object>> getVendasDiarias() {
        LocalDate hoje = LocalDate.now();
        LocalDate inicio = hoje.minusDays(30);
        
        List<VendaModel> vendas = vendaRepository.findByDataBetweenAndAtivoTrue(
            inicio.atStartOfDay(), 
            hoje.atTime(23, 59, 59)
        );
        
        Map<LocalDate, List<VendaModel>> vendasPorDia = vendas.stream()
            .collect(Collectors.groupingBy(v -> v.getData().toLocalDate()));
        
        List<Map<String, Object>> resultado = new ArrayList<>();
        
        for (LocalDate data = inicio; !data.isAfter(hoje); data = data.plusDays(1)) {
            List<VendaModel> vendasDoDia = vendasPorDia.getOrDefault(data, new ArrayList<>());
            
            Map<String, Object> dadosDia = new HashMap<>();
            dadosDia.put("data", data);
            dadosDia.put("totalVendas", vendasDoDia.size());
            dadosDia.put("valorTotal", vendasDoDia.stream().mapToDouble(VendaModel::getValortotal).sum());
            
            resultado.add(dadosDia);
        }
        
        return resultado;
    }

    // Produtos Mais Vendidos
    public List<Map<String, Object>> getProdutosMaisVendidos(int limite) {
        List<VendaModel> vendas = vendaRepository.findByAtivoTrue();
        
        Map<ProdutoModel, Integer> produtoQuantidade = new HashMap<>();
        Map<ProdutoModel, Double> produtoValor = new HashMap<>();
        
        for (VendaModel venda : vendas) {
            for (ItemVendaModel item : venda.getItens()) {
                ProdutoModel produto = item.getProduto();
                produtoQuantidade.merge(produto, item.getQuantidadeVendida(), Integer::sum);
                produtoValor.merge(produto, item.getQuantidadeVendida() * item.getPrecoVendido(), Double::sum);
            }
        }
        
        return produtoQuantidade.entrySet().stream()
            .sorted(Map.Entry.<ProdutoModel, Integer>comparingByValue().reversed())
            .limit(limite)
            .map(entry -> {
                Map<String, Object> item = new HashMap<>();
                ProdutoModel produto = entry.getKey();
                item.put("produto", produto);
                item.put("quantidadeVendida", entry.getValue());
                item.put("valorTotal", produtoValor.get(produto));
                return item;
            })
            .collect(Collectors.toList());
    }

    // Relatório de Estoque
    public List<Map<String, Object>> getRelatorioEstoque() {
        List<ProdutoModel> produtos = produtoRepository.findAll();
        
        return produtos.stream()
            .map(produto -> {
                Map<String, Object> item = new HashMap<>();
                item.put("produto", produto);
                item.put("quantidadeAtual", produto.getQuantidade());
                item.put("valorEstoque", produto.getQuantidade() * produto.getPreco());
                item.put("status", produto.getQuantidade() <= 10 ? "BAIXO" : 
                               produto.getQuantidade() <= 50 ? "MEDIO" : "ALTO");
                return item;
            })
            .collect(Collectors.toList());
    }

    // Produtos com Baixo Estoque
    public List<Map<String, Object>> getProdutosBaixoEstoque(int limite) {
        List<ProdutoModel> produtos = produtoRepository.findAll();
        
        return produtos.stream()
            .filter(produto -> produto.getQuantidade() <= limite)
            .sorted(Comparator.comparing(ProdutoModel::getQuantidade))
            .map(produto -> {
                Map<String, Object> item = new HashMap<>();
                item.put("produto", produto);
                item.put("quantidadeAtual", produto.getQuantidade());
                item.put("valorEstoque", produto.getQuantidade() * produto.getPreco());
                return item;
            })
            .collect(Collectors.toList());
    }

    // Vendas por Vendedor
    public List<Map<String, Object>> getVendasPorVendedor(LocalDate dataInicio, LocalDate dataFim) {
        List<VendaModel> vendas;
        
        if (dataInicio != null && dataFim != null) {
            vendas = vendaRepository.findByDataBetweenAndAtivoTrue(
                dataInicio.atStartOfDay(), 
                dataFim.atTime(23, 59, 59)
            );
        } else {
            vendas = vendaRepository.findByAtivoTrue();
        }
        
        Map<UsuarioModel, List<VendaModel>> vendasPorUsuario = vendas.stream()
            .collect(Collectors.groupingBy(VendaModel::getUsuario));
        
        return vendasPorUsuario.entrySet().stream()
            .map(entry -> {
                UsuarioModel usuario = entry.getKey();
                List<VendaModel> vendasUsuario = entry.getValue();
                
                Map<String, Object> item = new HashMap<>();
                item.put("usuario", usuario);
                item.put("totalVendas", vendasUsuario.size());
                item.put("valorTotal", vendasUsuario.stream().mapToDouble(VendaModel::getValortotal).sum());
                item.put("ticketMedio", vendasUsuario.stream().mapToDouble(VendaModel::getValortotal).average().orElse(0.0));
                
                return item;
            })
            .sorted((a, b) -> Double.compare((Double) b.get("valorTotal"), (Double) a.get("valorTotal")))
            .collect(Collectors.toList());
    }

    // Compradores Mais Ativos
    public List<Map<String, Object>> getCompradoresAtivos(int limite) {
        List<VendaModel> vendas = vendaRepository.findByAtivoTrue();
        
        Map<CompradorModel, List<VendaModel>> vendasPorComprador = vendas.stream()
            .filter(venda -> venda.getComprador() != null)
            .collect(Collectors.groupingBy(VendaModel::getComprador));
        
        return vendasPorComprador.entrySet().stream()
            .map(entry -> {
                CompradorModel comprador = entry.getKey();
                List<VendaModel> vendasComprador = entry.getValue();
                
                Map<String, Object> item = new HashMap<>();
                item.put("comprador", comprador);
                item.put("totalCompras", vendasComprador.size());
                item.put("valorTotal", vendasComprador.stream().mapToDouble(VendaModel::getValortotal).sum());
                item.put("ticketMedio", vendasComprador.stream().mapToDouble(VendaModel::getValortotal).average().orElse(0.0));
                item.put("ultimaCompra", vendasComprador.stream()
                    .map(VendaModel::getData)
                    .max(LocalDateTime::compareTo)
                    .orElse(null));
                
                return item;
            })
            .sorted((a, b) -> Double.compare((Double) b.get("valorTotal"), (Double) a.get("valorTotal")))
            .limit(limite)
            .collect(Collectors.toList());
    }

    // Dashboard - Resumo Geral
    public Map<String, Object> getDashboard() {
        LocalDate hoje = LocalDate.now();
        LocalDate inicioMes = hoje.withDayOfMonth(1);
        LocalDate inicioAno = hoje.withDayOfYear(1);
        
        List<VendaModel> todasVendas = vendaRepository.findByAtivoTrue();
        List<VendaModel> vendasMes = vendaRepository.findByDataBetweenAndAtivoTrue(
            inicioMes.atStartOfDay(), 
            hoje.atTime(23, 59, 59)
        );
        List<VendaModel> vendasAno = vendaRepository.findByDataBetweenAndAtivoTrue(
            inicioAno.atStartOfDay(), 
            hoje.atTime(23, 59, 59)
        );
        
        List<ProdutoModel> produtos = produtoRepository.findAll();
        List<CompradorModel> compradores = compradorRepository.findAll();
        
        Map<String, Object> dashboard = new HashMap<>();
        
        // Estatísticas gerais
        dashboard.put("totalVendas", todasVendas.size());
        dashboard.put("totalProdutos", produtos.size());
        dashboard.put("totalCompradores", compradores.size());
        dashboard.put("produtosBaixoEstoque", produtos.stream().filter(p -> p.getQuantidade() <= 10).count());
        
        // Vendas do mês
        dashboard.put("vendasMes", vendasMes.size());
        dashboard.put("valorTotalMes", vendasMes.stream().mapToDouble(VendaModel::getValortotal).sum());
        
        // Vendas do ano
        dashboard.put("vendasAno", vendasAno.size());
        dashboard.put("valorTotalAno", vendasAno.stream().mapToDouble(VendaModel::getValortotal).sum());
        
        // Ticket médio
        dashboard.put("ticketMedio", todasVendas.stream().mapToDouble(VendaModel::getValortotal).average().orElse(0.0));
        
        return dashboard;
    }

    // Vendas Mensais (últimos 12 meses)
    public List<Map<String, Object>> getVendasMensais() {
        LocalDate hoje = LocalDate.now();
        LocalDate inicio = hoje.minusMonths(12).withDayOfMonth(1);
        
        List<VendaModel> vendas = vendaRepository.findByDataBetweenAndAtivoTrue(
            inicio.atStartOfDay(), 
            hoje.atTime(23, 59, 59)
        );
        
        Map<String, List<VendaModel>> vendasPorMes = vendas.stream()
            .collect(Collectors.groupingBy(v -> 
                v.getData().getYear() + "-" + String.format("%02d", v.getData().getMonthValue())
            ));
        
        List<Map<String, Object>> resultado = new ArrayList<>();
        
        for (LocalDate data = inicio; !data.isAfter(hoje); data = data.plusMonths(1)) {
            String chave = data.getYear() + "-" + String.format("%02d", data.getMonthValue());
            List<VendaModel> vendasDoMes = vendasPorMes.getOrDefault(chave, new ArrayList<>());
            
            Map<String, Object> dadosMes = new HashMap<>();
            dadosMes.put("mes", data.format(DateTimeFormatter.ofPattern("yyyy-MM")));
            dadosMes.put("mesNome", data.format(DateTimeFormatter.ofPattern("MMM/yyyy")));
            dadosMes.put("totalVendas", vendasDoMes.size());
            dadosMes.put("valorTotal", vendasDoMes.stream().mapToDouble(VendaModel::getValortotal).sum());
            
            resultado.add(dadosMes);
        }
        
        return resultado;
    }
}
