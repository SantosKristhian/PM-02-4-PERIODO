package com.example.EstoqueManager.repository;

import com.example.EstoqueManager.model.ItemVendaModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ItemVendaRepository extends
        JpaRepository<ItemVendaModel, Long> {

}
