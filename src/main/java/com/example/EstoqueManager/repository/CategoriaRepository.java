package com.example.EstoqueManager.repository;

import com.example.EstoqueManager.model.CategoriaModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional; // <--- NOVA IMPORTAÇÃO

@Repository
public interface CategoriaRepository extends JpaRepository<CategoriaModel, Long> {

    // NOVO: Método para checar duplicidade no SAVE
    Optional<CategoriaModel> findByNome(String nome);

    // NOVO: Método para checar duplicidade no UPDATE
    Optional<CategoriaModel> findByNomeAndIdNot(String nome, Long id);
}
