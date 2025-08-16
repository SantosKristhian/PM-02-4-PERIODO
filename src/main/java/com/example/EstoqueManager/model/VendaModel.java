// VendaModel.java
package com.example.EstoqueManager.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Entity
@Getter
@Setter
@Table(name="venda_table")
public class VendaModel {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDate data;

    @Column(nullable = false)
    private double valortotal;

    @ManyToOne
    @JoinColumn(name = "usuario_ID", nullable = false)
    private UsuarioModel usuario;

    // 🔽 ADICIONE AQUI 👇
    @ManyToOne
    @JoinColumn(name = "comprador_id", nullable = false)
    private CompradorModel comprador;

    @OneToMany(mappedBy = "venda", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ItemVendaModel> itens;
}
