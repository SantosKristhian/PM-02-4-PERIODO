package com.example.EstoqueManager.repository;

import com.example.EstoqueManager.model.AuditoriaModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AuditoriaRepository extends JpaRepository<AuditoriaModel, Long> {

    List<AuditoriaModel> findByEntidadeOrderByDataHoraDesc(String entidade);

    List<AuditoriaModel> findByEntidadeAndEntidadeIdOrderByDataHoraDesc(String entidade, Long entidadeId);

    List<AuditoriaModel> findAllByOrderByDataHoraDesc();
}