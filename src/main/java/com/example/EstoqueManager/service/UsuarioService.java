package com.example.EstoqueManager.service;

import com.example.EstoqueManager.model.Cargo;
import com.example.EstoqueManager.model.UsuarioModel;
import com.example.EstoqueManager.repository.UsuarioRepository;
import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;

    public UsuarioModel autenticar(String login, String senha) {
        return usuarioRepository.findByLoginAndSenha(login, senha).orElseThrow(RuntimeException::new);
    }

    public List<UsuarioModel> findAll(){
        return usuarioRepository.findAll();
    }

    public UsuarioModel findById(Long id){
        return usuarioRepository.findById(id).orElseThrow(RuntimeException::new);
    }

    public UsuarioModel save(UsuarioModel usuario){
        return usuarioRepository.save(usuario);
    }

    public void deleteById(Long id){
        var usuario = this.findById(id);
        usuarioRepository.deleteById(id);
    }

    public UsuarioModel updateByID(Long id, UsuarioModel usuario){
        UsuarioModel updatedUser = findById(id);

        if (usuario.getNome() != null && !usuario.getNome().isBlank()){
            updatedUser.setNome(usuario.getNome());
        }

        if (usuario.getCpf() != null && !usuario.getCpf().isBlank()){
            updatedUser.setCpf(usuario.getCpf());
        }

        if (usuario.getIdade() != null ){
            updatedUser.setIdade(usuario.getIdade());
        }

        if (usuario.getLogin() != null && !usuario.getLogin().isBlank()){
            updatedUser.setLogin(usuario.getLogin());
        }


        if (usuario.getSenha() != null && !usuario.getSenha().isBlank()){
            updatedUser.setSenha(usuario.getSenha());
        }

        if (usuario.getCargo() != null){
            updatedUser.setCargo(usuario.getCargo());
        }


        return usuarioRepository.save(updatedUser);
    }

}
