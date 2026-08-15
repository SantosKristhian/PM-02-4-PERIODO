package com.example.EstoqueManager.dto;

import com.example.EstoqueManager.model.MetodoPagamento;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class VendaResponseCompletaDTO {
    private Long id;
    private LocalDateTime data;
    private double valorTotal;
    private boolean ativo;
    private MetodoPagamento metodoPagamento;
    private Double valorPago;
    private Double troco;
    private Boolean itensDevolvidos;

    // Informações do vendedor
    private UsuarioInfoDTO vendedor;

    // Informações do comprador (pode ser null)
    private CompradorInfoDTO comprador;

    // Itens da venda
    private List<ItemVendaInfoDTO> itens;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UsuarioInfoDTO {
        private Long id;
        private String nome;
        private String email;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CompradorInfoDTO {
        private Long id;
        private String nome;
        private String cpf;
        private String email;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ItemVendaInfoDTO {
        private Long produtoId;
        private String produtoNome;
        private Integer quantidadeVendida;
        private Double precoVendido;
        private Double subtotal;
    }
}