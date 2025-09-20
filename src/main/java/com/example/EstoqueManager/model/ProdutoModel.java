    package com.example.EstoqueManager.model;

    import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
    import jakarta.persistence.*;
    import lombok.*;

    import java.time.LocalDateTime;

    @Entity
    @Table(name = "produto_table")
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public class ProdutoModel {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        @Column(nullable = false)
        private String nome;

        @Column(nullable = false)
        private Integer quantidade;

        @Column(nullable = false)
        private Double preco;

        @ManyToOne
        @JsonIgnoreProperties("produtos")
        @JoinColumn(name = "pcategoriaId", nullable = false)
        private CategoriaModel categoria;


        @ManyToOne
        @JsonIgnoreProperties({"cpf", "idade", "login", "senha", "cargo", "vendas"})
        @JoinColumn(name = "usuarioUltimaAlteracaoId")
        private UsuarioModel usuarioUltimaAlteracao;


        private LocalDateTime dataUltimaAlteracao;
    }
