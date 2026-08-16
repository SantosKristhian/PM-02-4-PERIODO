package com.example.EstoqueManager.config;

import java.util.HashMap;
import java.util.Map;

import com.example.EstoqueManager.exception.BusinessException;
import com.example.EstoqueManager.exception.ResourceNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

	//TRATAMENTO DE ERROS DE VALIDATIONS
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<Map<String, String>> handle01(MethodArgumentNotValidException ex) {
		Map<String, String> erros = new HashMap<>();
		for (FieldError fildError : ex.getBindingResult().getFieldErrors()) {
			erros.put(fildError.getField(), fildError.getDefaultMessage());
		}
		return new ResponseEntity<Map<String, String>>(erros, HttpStatus.BAD_REQUEST);
	}

	//TRATAMENTO DE ERROS DE VALIDATIONS
	@ExceptionHandler(ConstraintViolationException.class)
	public ResponseEntity<Map<String, String>> handle02(ConstraintViolationException ex) {
		Map<String, String> erros = new HashMap<>();
		for (ConstraintViolation<?> violation : ex.getConstraintViolations()) {
			erros.put(violation.getPropertyPath().toString(), violation.getMessage());
		}
		return new ResponseEntity<Map<String, String>>(erros, HttpStatus.BAD_REQUEST);
	}

	//TRATAMENTO DOS DEMAIS ERROS DA APLICAÇÃO E DE REGRAS DE NEGÓCIO
	@ExceptionHandler(AccessDeniedException.class)
	public ResponseEntity<Map<String, String>> handleAccessDenied(AccessDeniedException ex) {
		Map<String, String> erro = new HashMap<>();
		erro.put("error", "Acesso negado: voce nao tem permissao para executar esta acao.");
		return new ResponseEntity<Map<String, String>>(erro, HttpStatus.FORBIDDEN);
	}

	//TRATAMENTO DE VIOLACAO DE INTEGRIDADE REFERENCIAL (ex: excluir categoria com produtos vinculados)
	@ExceptionHandler(DataIntegrityViolationException.class)
	public ResponseEntity<Map<String, String>> handleDataIntegrityViolation(DataIntegrityViolationException ex) {
		Map<String, String> erro = new HashMap<>();
		erro.put("error", "Não é possível concluir a operação: este registro está vinculado a outros dados do sistema.");
		return new ResponseEntity<Map<String, String>>(erro, HttpStatus.CONFLICT);
	}

	//TRATAMENTO DE REGRAS DE NEGOCIO (mensagem propositalmente descritiva para o usuario)
	@ExceptionHandler(BusinessException.class)
	public ResponseEntity<Map<String, String>> handleBusinessException(BusinessException ex) {
		Map<String, String> erro = new HashMap<>();
		erro.put("error", ex.getMessage());
		return new ResponseEntity<Map<String, String>>(erro, HttpStatus.BAD_REQUEST);
	}

	//TRATAMENTO DE RECURSO NAO ENCONTRADO
	@ExceptionHandler(ResourceNotFoundException.class)
	public ResponseEntity<Map<String, String>> handleResourceNotFound(ResourceNotFoundException ex) {
		Map<String, String> erro = new HashMap<>();
		erro.put("error", ex.getMessage());
		return new ResponseEntity<Map<String, String>>(erro, HttpStatus.NOT_FOUND);
	}

	//FALLBACK GENERICO: nao expor detalhe interno (stack trace, SQL, etc) ao cliente
	@ExceptionHandler(Exception.class)
	public ResponseEntity<Map<String, String>> handle03(Exception ex) {
		log.error("Erro inesperado", ex);
		Map<String, String> erro = new HashMap<>();
		erro.put("error", "Ocorreu um erro inesperado. Tente novamente mais tarde.");
		return new ResponseEntity<Map<String, String>>(erro, HttpStatus.INTERNAL_SERVER_ERROR);
	}

}