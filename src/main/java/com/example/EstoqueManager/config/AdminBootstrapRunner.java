package com.example.EstoqueManager.config;

import com.example.EstoqueManager.model.Cargo;
import com.example.EstoqueManager.model.UsuarioModel;
import com.example.EstoqueManager.service.UsuarioService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;
import java.util.Base64;

@Slf4j
@Component
@RequiredArgsConstructor
public class AdminBootstrapRunner implements CommandLineRunner {

    private final UsuarioService usuarioService;

    @Value("${ADMIN_LOGIN:admin}")
    private String adminLogin;

    @Value("${ADMIN_PASSWORD:}")
    private String adminPasswordConfigurada;

    @Override
    public void run(String... args) {
        try {
            if (usuarioService.existeAdministrador()) {
                return;
            }

            String senha = adminPasswordConfigurada != null && !adminPasswordConfigurada.isBlank()
                    ? adminPasswordConfigurada
                    : gerarSenhaAleatoria();

            UsuarioModel admin = new UsuarioModel();
            admin.setNome("Administrador");
            admin.setCpf("00000000000");
            admin.setIdade(99);
            admin.setLogin(adminLogin);
            admin.setSenha(senha);
            admin.setCargo(Cargo.ADM);

            usuarioService.save(admin);

            log.warn("=====================================================================");
            log.warn("Nenhum administrador encontrado. Usuario ADM inicial criado:");
            log.warn("  login: {}", adminLogin);
            log.warn("  senha: {}", senha);
            log.warn("Troque essa senha assim que possivel.");
            log.warn("=====================================================================");
        } catch (Exception e) {
            log.error("Falha ao criar o administrador inicial automaticamente. " +
                    "Se necessario, crie um usuario ADM manualmente.", e);
        }
    }

    private String gerarSenhaAleatoria() {
        byte[] bytes = new byte[12];
        new SecureRandom().nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }
}