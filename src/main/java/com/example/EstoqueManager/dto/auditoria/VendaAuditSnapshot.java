package com.example.EstoqueManager.dto.auditoria;

import com.example.EstoqueManager.model.MetodoPagamento;
import com.example.EstoqueManager.model.VendaModel;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VendaAuditSnapshot {
    private Long id;
    private Double valortotal;
    private Boolean ativo;
    private MetodoPagamento metodoPagamento;
    private Double valorPago;
    private Double troco;
    private Boolean itensDevolvidos;
    private Long usuarioId;
    private String usuarioNome;
    private Long compradorId;
    private String compradorNome;

    public static VendaAuditSnapshot de(VendaModel venda) {
        VendaAuditSnapshot snapshot = new VendaAuditSnapshot();
        snapshot.setId(venda.getId());
        snapshot.setValortotal(venda.getValortotal());
        snapshot.setAtivo(venda.isAtivo());
        snapshot.setMetodoPagamento(venda.getMetodoPagamento());
        snapshot.setValorPago(venda.getValorPago());
        snapshot.setTroco(venda.getTroco());
        snapshot.setItensDevolvidos(venda.getItensDevolvidos());
        if (venda.getUsuario() != null) {
            snapshot.setUsuarioId(venda.getUsuario().getId());
            snapshot.setUsuarioNome(venda.getUsuario().getNome());
        }
        if (venda.getComprador() != null) {
            snapshot.setCompradorId(venda.getComprador().getId());
            snapshot.setCompradorNome(venda.getComprador().getNome());
        }
        return snapshot;
    }
}