// ProdutoResponseDTO.java
package com.example.EstoqueManager.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class ProdutoResponseDTO {
    private Long id;
    private String nome;
    private Integer quantidade;
    private Double preco;
    private Boolean ativo;
    private LocalDateTime dataUltimaAlteracao;

    private CategoriaResumoDTO categoria;
    private UsuarioResumoDTO usuarioUltimaAlteracao;

    @Getter
    @Setter
    public static class CategoriaResumoDTO {
        private Long id;
        private String nome;
    }

    @Getter
    @Setter
    public static class UsuarioResumoDTO {
        private Long id;
        private String nome;
        private String username;
    }
}