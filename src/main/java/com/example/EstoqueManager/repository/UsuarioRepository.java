package com.example.EstoqueManager.repository;

import com.example.EstoqueManager.model.UsuarioModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UsuarioRepository extends
        JpaRepository<UsuarioModel, Long> {
}
