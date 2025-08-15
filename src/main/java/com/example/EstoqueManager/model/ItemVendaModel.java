package com.example.EstoqueManager.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "item_venda_table")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ItemVendaModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Relacionamento com a venda
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "venda_id", nullable = false)
    private VendaModel venda;

    // Relacionamento com o produto
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "produto_id", nullable = false)
    private ProdutoModel produto;

    @Column(nullable = false)
    private Integer quantidade;

    // Preço do produto no momento da venda
    @Column(nullable = false)
    private Double precoUnitario;

    // Subtotal calculado (quantidade × preço unitário)
    @Column(nullable = false)
    private Double subtotal;

    // Método utilitário para calcular subtotal
    public void calcularSubtotal() {
        if (precoUnitario != null && quantidade != null) {
            this.subtotal = precoUnitario * quantidade;
        }
    }
}
