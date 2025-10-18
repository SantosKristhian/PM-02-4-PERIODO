package com.example.EstoqueManager.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProdutoCurvaABCDTO {
    private Long id;
    private String nome;
    private Double valorTotalVendido; // quantidade * preço
    private Double percentualFaturamento;
    private Double percentualAcumulado;
    private String classificacao; // "A", "B" ou "C"
}