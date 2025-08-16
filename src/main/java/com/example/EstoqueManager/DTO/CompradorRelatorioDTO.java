package com.example.EstoqueManager.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class CompradorRelatorioDTO {
    private Long id;
    private String nome;
    private String cpf;
    private String email;
    private Integer totalVendas;
    private Double valorTotalComprado;
}
