package com.example.EstoqueManager.service;

import com.example.EstoqueManager.exception.BusinessException;
import com.example.EstoqueManager.exception.ResourceNotFoundException;
import com.example.EstoqueManager.model.Cargo;
import com.example.EstoqueManager.model.UsuarioModel;
import com.example.EstoqueManager.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    UsuarioModel autenticar(String login, String senha) {
        UsuarioModel usuario = usuarioRepository.findByLogin(login);

        if (usuario == null) {
            throw new BusinessException("Login inválido.");
        }

        if (!passwordEncoder.matches(senha, usuario.getSenha())) {
            throw new BusinessException("Senha inválida.");
        }

        return usuario;
    }



    public List<UsuarioModel> findAll() {
        return usuarioRepository.findAll();
    }

    public UsuarioModel findById(Long id) {
        if (id == null || id <= 0) {
            throw new BusinessException("ID inválido. Deve ser um número positivo.");
        }

        return usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado com ID: " + id));
    }

    public UsuarioModel save(UsuarioModel usuario) {
        validarUsuario(usuario);

        if (usuario.getId() != null) {
            throw new BusinessException("ID deve ser nulo ao criar um novo usuário.");
        }

        if (usuarioRepository.findByLogin(usuario.getLogin()) != null) {
            throw new BusinessException("Login já cadastrado no sistema.");
        }


        usuario.setSenha(passwordEncoder.encode(usuario.getSenha()));

        return usuarioRepository.save(usuario);
    }

    public void deleteById(Long id) {
        if (id == null || id <= 0) {
            throw new BusinessException("ID inválido. Deve ser um número positivo.");
        }

        if (!usuarioRepository.existsById(id)) {
            throw new ResourceNotFoundException("Usuário não encontrado com ID: " + id);
        }

        usuarioRepository.deleteById(id);
    }

    public UsuarioModel updateByID(Long id, UsuarioModel usuarioUpdated) {
        if (id == null || id <= 0) {
            throw new BusinessException("ID inválido. Deve ser um número positivo.");
        }

        validarUsuario(usuarioUpdated);

        UsuarioModel usuarioExistente = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado com ID: " + id));

        // Verificar se o novo login já existe (se mudou)
        if (!usuarioExistente.getLogin().equals(usuarioUpdated.getLogin())) {
            if (usuarioRepository.findByLogin(usuarioUpdated.getLogin()) != null) {
                throw new BusinessException("Login já cadastrado no sistema.");
            }
        }

        usuarioExistente.setNome(usuarioUpdated.getNome());
        usuarioExistente.setCpf(usuarioUpdated.getCpf());
        usuarioExistente.setIdade(usuarioUpdated.getIdade());
        usuarioExistente.setLogin(usuarioUpdated.getLogin());
        usuarioExistente.setSenha(passwordEncoder.encode(usuarioUpdated.getSenha()));
        usuarioExistente.setCargo(usuarioUpdated.getCargo());
        return usuarioRepository.save(usuarioExistente);
    }

    private void validarUsuario(UsuarioModel usuario) {
        if (usuario == null) {
            throw new BusinessException("Usuário não pode ser nulo.");
        }

        if (usuario.getNome() == null || usuario.getNome().trim().isEmpty()) {
            throw new BusinessException("Nome do usuário é obrigatório.");
        }

        if (usuario.getLogin() == null || usuario.getLogin().trim().isEmpty()) {
            throw new BusinessException("Login é obrigatório.");
        }

        if (usuario.getSenha() == null || usuario.getSenha().trim().isEmpty()) {
            throw new BusinessException("Senha é obrigatória.");
        }

        if (usuario.getCargo() == null) {
            throw new BusinessException("Cargo é obrigatório.");
        }

        if (usuario.getCpf() == null || usuario.getCpf().trim().isEmpty()) {
            throw new BusinessException("CPF é obrigatório.");
        }
    }

    public boolean existemUsuarios() {
        return usuarioRepository.count() > 0;
    }

    public boolean existeAdministrador() {
        return usuarioRepository.findAll().stream()
                .anyMatch(usuario -> usuario.getCargo() == Cargo.ADM);
    }

}