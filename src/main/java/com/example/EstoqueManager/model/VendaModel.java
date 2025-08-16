    // VendaModel.java
    package com.example.EstoqueManager.model;

    import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
    import jakarta.persistence.*;
    import lombok.Getter;
    import lombok.Setter;

    import java.time.LocalDate;
    import java.time.LocalDateTime;
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
        private LocalDateTime data;

        @Column(nullable = false)
        private double valortotal;

        @Column(nullable = false)
        private boolean ativo;

        @ManyToOne
        @JsonIgnoreProperties("vendas")
        @JoinColumn(name = "usuario_id", nullable = false)
        private UsuarioModel usuario;

        @ManyToOne
        @JsonIgnoreProperties("vendas")
        @JoinColumn(name = "comprador_id")
        private CompradorModel comprador;


        @JsonIgnoreProperties("venda")
        @OneToMany(mappedBy = "venda", cascade = CascadeType.ALL, orphanRemoval = true)
        private List<ItemVendaModel> itens;
    }
