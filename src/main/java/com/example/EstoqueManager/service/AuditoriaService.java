package com.example.EstoqueManager.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.example.EstoqueManager.model.AcaoAuditoria;
import com.example.EstoqueManager.model.AuditoriaModel;
import com.example.EstoqueManager.model.UsuarioModel;
import com.example.EstoqueManager.repository.AuditoriaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuditoriaService {

    private final AuditoriaRepository auditoriaRepository;
    private final ObjectMapper objectMapper;

    public void registrar(String entidade, Long entidadeId, AcaoAuditoria acao, Object dadosAntes, Object dadosDepois) {
        UsuarioModel usuarioAutenticado = obterUsuarioAutenticado();

        AuditoriaModel log = new AuditoriaModel();
        log.setEntidade(entidade);
        log.setEntidadeId(entidadeId);
        log.setAcao(acao);
        log.setUsuarioId(usuarioAutenticado != null ? usuarioAutenticado.getId() : null);
        log.setUsuarioNome(usuarioAutenticado != null ? usuarioAutenticado.getNome() : "sistema");
        log.setDataHora(LocalDateTime.now());
        log.setDadosAntes(paraJson(dadosAntes));
        log.setDadosDepois(paraJson(dadosDepois));

        auditoriaRepository.save(log);
    }

    public List<AuditoriaModel> listarTudo() {
        return auditoriaRepository.findAllByOrderByDataHoraDesc();
    }

    public List<AuditoriaModel> listarPorEntidade(String entidade) {
        return auditoriaRepository.findByEntidadeOrderByDataHoraDesc(entidade);
    }

    public List<AuditoriaModel> listarPorRegistro(String entidade, Long entidadeId) {
        return auditoriaRepository.findByEntidadeAndEntidadeIdOrderByDataHoraDesc(entidade, entidadeId);
    }

    private String paraJson(Object dados) {
        if (dados == null) {
            return null;
        }
        try {
            return objectMapper.writeValueAsString(dados);
        } catch (Exception e) {
            log.error("Falha ao serializar snapshot de auditoria", e);
            return null;
        }
    }

    private UsuarioModel obterUsuarioAutenticado() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof UsuarioModel usuario) {
            return usuario;
        }
        return null;
    }
}