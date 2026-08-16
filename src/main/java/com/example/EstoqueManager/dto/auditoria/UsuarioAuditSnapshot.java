package com.example.EstoqueManager.dto.auditoria;

import com.example.EstoqueManager.model.UsuarioModel;
import lombok.Getter;
import lombok.Setter;

// Nao inclui a senha (nem hash) de proposito - log de auditoria nao deve guardar credenciais.
@Getter
@Setter
public class UsuarioAuditSnapshot {
    private Long id;
    private String nome;
    private String cpf;
    private Integer idade;
    private String login;
    private String cargo;

    public static UsuarioAuditSnapshot de(UsuarioModel usuario) {
        UsuarioAuditSnapshot snapshot = new UsuarioAuditSnapshot();
        snapshot.setId(usuario.getId());
        snapshot.setNome(usuario.getNome());
        snapshot.setCpf(usuario.getCpf());
        snapshot.setIdade(usuario.getIdade());
        snapshot.setLogin(usuario.getLogin());
        snapshot.setCargo(usuario.getCargo() != null ? usuario.getCargo().name() : null);
        return snapshot;
    }
}