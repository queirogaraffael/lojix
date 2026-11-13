package com.example.lojix.common.exception;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.QueryTimeoutException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.web.servlet.HandlerMapping;

import static org.assertj.core.api.Assertions.assertThat;

class CustomExceptionHandlerTest {

    private MeterRegistry meterRegistry;
    private CustomExceptionHandler handler;

    @BeforeEach
    void setUp() {
        meterRegistry = new SimpleMeterRegistry();
        handler = new CustomExceptionHandler(meterRegistry);
    }

    @Test
    @DisplayName("Deve registrar métrica db.query.errors ao capturar DataIntegrityViolationException")
    void deveRegistrarMetricaAoCapturarDataIntegrityViolationException() {
        MockHttpServletRequest request = new MockHttpServletRequest("POST", "/api/v1/clientes");

        DataIntegrityViolationException ex = new DataIntegrityViolationException("duplicate key value violates unique constraint");

        ResponseEntity<Object> response = handler.handleDataIntegrityViolationException(ex, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);

        Counter counter = meterRegistry.find("db.query.errors")
                .tag("exception", "DataIntegrityViolationException")
                .tag("endpoint", "/api/v1/clientes")
                .tag("method", "POST")
                .counter();

        assertThat(counter).isNotNull();
        assertThat(counter.count()).isEqualTo(1.0);
    }

    @Test
    @DisplayName("Deve registrar métrica db.query.errors ao capturar DataAccessException genérica (timeout/sql)")
    void deveRegistrarMetricaAoCapturarDataAccessException() {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/v1/produtos/1");
        request.setAttribute(HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE, "/api/v1/produtos/{id}");

        QueryTimeoutException ex = new QueryTimeoutException("Query execution exceeded timeout limit");

        ResponseEntity<Object> response = handler.handleDataAccessException(ex, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);

        Counter counter = meterRegistry.find("db.query.errors")
                .tag("exception", "QueryTimeoutException")
                .tag("endpoint", "/api/v1/produtos/{id}")
                .tag("method", "GET")
                .counter();

        assertThat(counter).isNotNull();
        assertThat(counter.count()).isEqualTo(1.0);
    }

    @Test
    @DisplayName("Deve registrar métrica db.query.errors ao capturar ObjectOptimisticLockingFailureException")
    void deveRegistrarMetricaAoCapturarOptimisticLockingFailure() {
        MockHttpServletRequest request = new MockHttpServletRequest("PUT", "/api/v1/produtos/5");
        request.setAttribute(HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE, "/api/v1/produtos/{id}");

        ObjectOptimisticLockingFailureException ex = new ObjectOptimisticLockingFailureException("Produto", 5L);

        ResponseEntity<Object> response = handler.handleOptimisticLockingException(ex, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);

        Counter counter = meterRegistry.find("db.query.errors")
                .tag("exception", "ObjectOptimisticLockingFailureException")
                .tag("endpoint", "/api/v1/produtos/{id}")
                .tag("method", "PUT")
                .counter();

        assertThat(counter).isNotNull();
        assertThat(counter.count()).isEqualTo(1.0);
    }
}

