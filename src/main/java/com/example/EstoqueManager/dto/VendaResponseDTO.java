package com.example.EstoqueManager.dto;

import com.example.EstoqueManager.model.MetodoPagamento;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class VendaResponseDTO {
    private Long id;
    private LocalDateTime data;
    private double valortotal;
    private boolean ativo;
    private MetodoPagamento metodoPagamento;
    private Double valorPago;
    private Double troco;
    private Boolean itensDevolvidos;

    private UsuarioResumoDTO usuario;
    private CompradorResumoDTO comprador;
    private List<ItemVendaResumoDTO> itens;

    @Getter
    @Setter
    public static class CompradorResumoDTO {
        private Long id;
        private String nome;
    }

    @Getter
    @Setter
    public static class ProdutoResumoDTO {
        private Long id;
        private String nome;
    }

    @Getter
    @Setter
    public static class ItemVendaResumoDTO {
        private Long id;
        private ProdutoResumoDTO produto;
        private Integer quantidadeVendida;
        private Double precoVendido;
    }
}