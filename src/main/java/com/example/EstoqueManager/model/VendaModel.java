package com.example.EstoqueManager.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "venda_table")
public class VendaModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDateTime data;

    @Column(nullable = false)
    private double valortotal;

    @Column(nullable = false)
    private boolean ativo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MetodoPagamento metodoPagamento;

    @Column(nullable = true)
    private Double valorPago;

    @Column(nullable = true)
    private Double troco;

    @Column(nullable = false)
    private Boolean itensDevolvidos = false;

    @JsonBackReference("usuario-venda")
    @ManyToOne
    @JoinColumn(name = "usuario_id", nullable = false)
    private UsuarioModel usuario;

    @JsonBackReference("comprador-venda")
    @ManyToOne
    @JoinColumn(name = "comprador_id")
    private CompradorModel comprador;

    @JsonManagedReference("venda-item")
    @OneToMany(mappedBy = "venda", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ItemVendaModel> itens;
}