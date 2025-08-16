package com.example.EstoqueManager.repository;

import com.example.EstoqueManager.model.CompradorModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CompradorRepository extends JpaRepository<CompradorModel, Long> {
}
