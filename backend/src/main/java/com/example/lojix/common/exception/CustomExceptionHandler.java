package com.example.lojix.common.exception;

import io.micrometer.core.instrument.MeterRegistry;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.web.servlet.HandlerMapping;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class CustomExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(CustomExceptionHandler.class);

    private final MeterRegistry meterRegistry;

    public CustomExceptionHandler(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
    }

    private void recordDatabaseError(Exception ex, HttpServletRequest request) {
        if (meterRegistry == null) {
            return;
        }
        String endpoint = "UNKNOWN";
        String method = "UNKNOWN";
        if (request != null) {
            method = request.getMethod() != null ? request.getMethod() : "UNKNOWN";
            Object bestMatchingPattern = request.getAttribute(HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE);
            endpoint = bestMatchingPattern != null ? bestMatchingPattern.toString() : (request.getRequestURI() != null ? request.getRequestURI() : "UNKNOWN");
        }
        meterRegistry.counter("db.query.errors",
                "exception", ex.getClass().getSimpleName(),
                "method", method,
                "endpoint", endpoint
        ).increment();
    }

    public record ErrorResponse(String message, int status, String timestamp) {}

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<Object> handleBadCredentialsException(BadCredentialsException ex) {
        String safeMessage = "Credenciais de acesso inválidas (usuário ou senha incorretos).";

        ErrorResponse response = new ErrorResponse(
                safeMessage,
                HttpStatus.UNAUTHORIZED.value(),
                new Date().toString()
        );

        return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Object> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {

        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Object> handleResourceNotFoundException(ResourceNotFoundException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Object> handleIllegalArgumentException(IllegalArgumentException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }


    @ExceptionHandler(TokenCreationException.class)
    public ResponseEntity<Object> handleTokenCreationException(TokenCreationException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<Object> handleUserAlreadyExistsException(UserAlreadyExistsException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(AuthorizationDeniedException.class)
    public ResponseEntity<Object> handleAuthorizationDeniedException(AuthorizationDeniedException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(TokenValidationException.class)
    public ResponseEntity<Object> handleTokenValidationException(TokenValidationException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleGeneralException(Exception ex) {
        String genericMessage = "Ocorreu um erro interno inesperado no servidor.";
        return new ResponseEntity<>(genericMessage, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(UnsupportedOperationException.class)
    public ResponseEntity<Object> handleUnsupportedOperationException(UnsupportedOperationException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(UsuarioJaExisteException.class)
    public ResponseEntity<Object> handleUsuarioJaExisteException(UsuarioJaExisteException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(InvalidFileException.class)
    public ResponseEntity<Object> handleInvalidFileException(InvalidFileException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(StorageException.class)
    public ResponseEntity<Object> handleStorageException(StorageException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_GATEWAY);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Object> handleDataIntegrityViolationException(DataIntegrityViolationException ex, HttpServletRequest request) {
        recordDatabaseError(ex, request);
        log.error("Violação de integridade no banco de dados na rota {}: {}", request != null ? request.getRequestURI() : "N/A", ex.getMessage(), ex);

        String safeMessage = "Violação de integridade nos dados: registro duplicado ou chave referencial inválida.";
        ErrorResponse response = new ErrorResponse(
                safeMessage,
                HttpStatus.CONFLICT.value(),
                new Date().toString()
        );
        return new ResponseEntity<>(response, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(DataAccessException.class)
    public ResponseEntity<Object> handleDataAccessException(DataAccessException ex, HttpServletRequest request) {
        recordDatabaseError(ex, request);
        log.error("Erro de acesso ou comunicação no banco de dados na rota {}: {}", request != null ? request.getRequestURI() : "N/A", ex.getMessage(), ex);

        String safeMessage = "Ocorreu uma falha na execução da operação no banco de dados.";
        ErrorResponse response = new ErrorResponse(
                safeMessage,
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                new Date().toString()
        );
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(ObjectOptimisticLockingFailureException.class)
    public ResponseEntity<Object> handleOptimisticLockingException(ObjectOptimisticLockingFailureException ex, HttpServletRequest request) {
        recordDatabaseError(ex, request);
        log.warn("Conflito de concorrência detectado na rota {}: {}", request != null ? request.getRequestURI() : "N/A", ex.getMessage());

        String msg = "Conflito de dados: Este registro foi modificado por outro usuário enquanto você o editava. Recarregue a página e tente novamente.";
        ErrorResponse response = new ErrorResponse(
                msg,
                HttpStatus.CONFLICT.value(),
                new Date().toString()
        );
        return new ResponseEntity<>(response, HttpStatus.CONFLICT);
    }
}