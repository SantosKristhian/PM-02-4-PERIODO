package com.example.EstoqueManager.repository;

import java.util.Optional;

import com.example.EstoqueManager.model.UsuarioModel;
import org.springframework.data.jpa.repository.JpaRepository;


public interface LoginRepository extends JpaRepository<UsuarioModel, Long>{

	public Optional<UsuarioModel> findByLogin(String login);
	
}
