package com.example.EstoqueManager.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "categoria_table")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CategoriaModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nome;

    @JsonIgnoreProperties("categoria")
    @OneToMany(mappedBy = "categoria")
    private List<ProdutoModel> produtos;
}
