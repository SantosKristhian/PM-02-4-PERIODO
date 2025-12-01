package com.example.EstoqueManager.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
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

    @NotBlank(message = "Nome do produto é obrigatório")
    @Size(min = 3, max = 200, message = "Nome deve ter entre 3 e 200 caracteres")
    @Column(nullable = false)
    private String nome;

    @NotNull(message = "Quantidade é obrigatória")
    @Min(value = 0, message = "Quantidade não pode ser negativa")
    @Column(nullable = false)
    private Integer quantidade;

    @NotNull(message = "Preço é obrigatório")
    @DecimalMin(value = "0.01", message = "Preço deve ser maior que zero")
    @Column(nullable = false)
    private Double preco;

    @Column(nullable = false)
    private Boolean ativo = true;

    @NotNull(message = "Categoria é obrigatória")
    @JsonBackReference("categoria-produto")
    @ManyToOne
    @JoinColumn(name = "pcategoriaId", nullable = false)
    private CategoriaModel categoria;

    @ManyToOne
    @JsonIgnoreProperties({"cpf", "idade", "login", "senha", "cargo", "vendas"})
    @JoinColumn(name = "usuarioUltimaAlteracaoId")
    private UsuarioModel usuarioUltimaAlteracao;

    private LocalDateTime dataUltimaAlteracao;
}