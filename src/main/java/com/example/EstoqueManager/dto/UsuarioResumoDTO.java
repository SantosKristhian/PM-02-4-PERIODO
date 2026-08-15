package com.example.EstoqueManager.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UsuarioResumoDTO {
    private Long id;
    private String nome;

    public UsuarioResumoDTO(Long id, String nome) {
        this.id = id;
        this.nome = nome;
    }
}