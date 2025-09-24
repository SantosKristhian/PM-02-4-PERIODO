package com.example.EstoqueManager.repository;

import com.example.EstoqueManager.model.VendaModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface VendaRepository extends JpaRepository<VendaModel, Long> {

    // Métodos existentes (se houver, adicione-os aqui)
    // Exemplo: List<VendaModel> findByUsuarioId(Long usuarioId);

    // NOVOS MÉTODOS PARA RELATÓRIOS:
    List<VendaModel> findByDataBetweenAndAtivoTrue(LocalDateTime dataInicio, LocalDateTime dataFim);

    List<VendaModel> findByAtivoTrue();

    List<VendaModel> findByUsuarioIdAndAtivoTrue(Long usuarioId);

    List<VendaModel> findByCompradorIdAndAtivoTrue(Long compradorId);

    @Query("SELECT v FROM VendaModel v WHERE v.ativo = true ORDER BY v.data DESC")
    List<VendaModel> findAllByAtivoTrueOrderByDataDesc();

    @Query("SELECT COUNT(v) FROM VendaModel v WHERE v.ativo = true AND v.data >= :dataInicio")
    Long countVendasAPartirDe(@Param("dataInicio") LocalDateTime dataInicio);

    @Query("SELECT SUM(v.valortotal) FROM VendaModel v WHERE v.ativo = true AND v.data BETWEEN :dataInicio AND :dataFim")
    Double sumValorTotalByPeriodo(@Param("dataInicio") LocalDateTime dataInicio, @Param("dataFim") LocalDateTime dataFim);
}
