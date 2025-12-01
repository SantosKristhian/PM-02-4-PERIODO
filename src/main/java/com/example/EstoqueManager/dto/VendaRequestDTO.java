package com.example.EstoqueManager.dto;

import com.example.EstoqueManager.model.MetodoPagamento;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class VendaRequestDTO {

    private Long compradorId;  // REMOVA @NotNull - pode ser null

    @NotNull(message = "Método de pagamento é obrigatório")
    private MetodoPagamento metodoPagamento;

    private Double valorPago;  // Pode ser null para métodos não-dinheiro

    @NotNull(message = "Itens da venda são obrigatórios")
    @Size(min = 1, message = "A venda deve conter pelo menos um item")
    private List<ItemVendaRequestDTO> itens;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ItemVendaRequestDTO {
        @NotNull(message = "ID do produto é obrigatório")
        private Long produtoId;

        @NotNull(message = "Quantidade é obrigatória")
        private Integer quantidadeVendida;

        private Double precoVendido;  // Pode ser null
    }
}