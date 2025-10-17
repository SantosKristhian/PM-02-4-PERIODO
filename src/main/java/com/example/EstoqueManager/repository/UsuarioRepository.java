package com.example.EstoqueManager.repository;

import com.example.EstoqueManager.model.UsuarioModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<UsuarioModel, Long> {
    UsuarioModel findByLoginAndSenha(String login, String senha);

    UsuarioModel findByLogin(String login);
}
