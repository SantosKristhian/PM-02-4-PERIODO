package com.example.EstoqueManager.exception;

import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.context.request.WebRequest;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();
    private final WebRequest request = mock(WebRequest.class);

    @Test
    void deveTratarResourceNotFoundException() {
        when(request.getDescription(false)).thenReturn("uri=/teste");
        ResourceNotFoundException ex = new ResourceNotFoundException("Item não encontrado");

        ResponseEntity<ErrorResponse> response = handler.handleResourceNotFoundException(ex, request);

        assertEquals(404, response.getBody().getStatus());
        assertEquals("Not Found", response.getBody().getError());
        assertEquals("Item não encontrado", response.getBody().getMessage());
        assertEquals("/teste", response.getBody().getPath());
        assertNotNull(response.getBody().getTimestamp());
        assertTrue(response.getBody().getDetails() == null || response.getBody().getDetails().isEmpty());
    }

    @Test
    void deveTratarBusinessException() {
        when(request.getDescription(false)).thenReturn("uri=/teste");
        BusinessException ex = new BusinessException("Regra de negócio violada");

        ResponseEntity<ErrorResponse> response = handler.handleBusinessException(ex, request);

        assertEquals(400, response.getBody().getStatus());
        assertEquals("Business Rule Violation", response.getBody().getError());
        assertEquals("Regra de negócio violada", response.getBody().getMessage());
        assertEquals("/teste", response.getBody().getPath());
        assertNotNull(response.getBody().getTimestamp());
        assertTrue(response.getBody().getDetails() == null || response.getBody().getDetails().isEmpty());
    }

    @Test
    void deveTratarMethodArgumentNotValidException() {
        when(request.getDescription(false)).thenReturn("uri=/teste");

        // Mock do FieldError
        FieldError fieldError = new FieldError("obj", "campo", "não pode ser nulo");
        BindingResult bindingResult = mock(BindingResult.class);
        when(bindingResult.getFieldErrors()).thenReturn(Collections.singletonList(fieldError));

        MethodArgumentNotValidException ex = mock(MethodArgumentNotValidException.class);
        when(ex.getBindingResult()).thenReturn(bindingResult);

        ResponseEntity<ErrorResponse> response = handler.handleValidationException(ex, request);

        assertEquals(400, response.getBody().getStatus());
        assertEquals("Validation Failed", response.getBody().getError());
        assertEquals("Erro de validação nos campos enviados", response.getBody().getMessage());
        assertEquals("/teste", response.getBody().getPath());
        assertNotNull(response.getBody().getTimestamp());
        assertNotNull(response.getBody().getDetails());
        assertEquals(1, response.getBody().getDetails().size());
        assertEquals("campo: não pode ser nulo", response.getBody().getDetails().get(0));
    }

    @Test
    void deveTratarExcecaoGenerica() {
        when(request.getDescription(false)).thenReturn("uri=/teste");
        Exception ex = new Exception("Erro inesperado");

        ResponseEntity<ErrorResponse> response = handler.handleGlobalException(ex, request);

        assertEquals(500, response.getBody().getStatus());
        assertEquals("Internal Server Error", response.getBody().getError());
        assertEquals("Ocorreu um erro inesperado: Erro inesperado", response.getBody().getMessage());
        assertEquals("/teste", response.getBody().getPath());
        assertNotNull(response.getBody().getTimestamp());
        assertTrue(response.getBody().getDetails() == null || response.getBody().getDetails().isEmpty());
    }
}
