package com.example.EstoqueManager.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "auditoria_table")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AuditoriaModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String entidade;

    @Column(nullable = false)
    private Long entidadeId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AcaoAuditoria acao;

    @Column(nullable = true)
    private Long usuarioId;

    @Column(nullable = false)
    private String usuarioNome;

    @Column(nullable = false)
    private LocalDateTime dataHora;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String dadosAntes;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String dadosDepois;
}