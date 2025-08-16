package com.example.EstoqueManager.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
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

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties({"itens", "usuario", "comprador"})
    @JoinColumn(name = "venda_id", nullable = false)
    private VendaModel venda;

    @ManyToOne(fetch = FetchType.EAGER)
    @JsonIgnoreProperties({"categoria", "usuarioUltimaAlteracao"})
    @JoinColumn(name = "produto_id", nullable = false)
    private ProdutoModel produto;

    @Column(nullable = false)
    private Integer quantidade;

    @Column(nullable = false)
        private Double precoVendido;

}
