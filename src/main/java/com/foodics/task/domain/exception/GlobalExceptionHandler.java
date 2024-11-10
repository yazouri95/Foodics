package com.foodics.task.domain.exception;

import com.foodics.task.dto.ProblemDetailDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.time.LocalDateTime;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ProblemDetailDTO> handleGlobalException(Exception ex, WebRequest request) {
        ProblemDetailDTO problemDetail = toProblemDetailDto("An unexpected error occurred.", request);
        logError(problemDetail.getError(), ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(problemDetail);
    }

    @ExceptionHandler(InsufficientStockException.class)
    public ResponseEntity<ProblemDetailDTO> handleInsufficientStockException(DomainException ex, WebRequest request) {
        ProblemDetailDTO problemDetail = toProblemDetailDto(ex.getMessage(), request);
        logError(problemDetail.getError(), ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(problemDetail);
    }

    @ExceptionHandler(ProductNotFoundException.class)
    public ResponseEntity<ProblemDetailDTO> handleProductNotFoundException(DomainException ex, WebRequest request) {
        ProblemDetailDTO problemDetail = toProblemDetailDto(ex.getMessage(), request);
        logError(problemDetail.getError(), ex);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(problemDetail);
    }

    @ExceptionHandler(IngredientNotFoundException.class)
    public ResponseEntity<ProblemDetailDTO> handleIngredientNotFoundException(DomainException ex, WebRequest request) {
        ProblemDetailDTO problemDetail = toProblemDetailDto(ex.getMessage(), request);
        logError(problemDetail.getError(), ex);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(problemDetail);
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ProblemDetailDTO> handleNoResourceFoundException(NoResourceFoundException ex, WebRequest request) {
        ProblemDetailDTO problemDetail = toProblemDetailDto("no routes found.", request);
        logError(problemDetail.getError(), ex);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(problemDetail);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ProblemDetailDTO> handleMissingServletRequestParameterException(MissingServletRequestParameterException ex, WebRequest request) {
        ProblemDetailDTO problemDetail = toProblemDetailDto(ex.getBody().getDetail(), request);
        logError(problemDetail.getError(), ex);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(problemDetail);
    }

    private ProblemDetailDTO toProblemDetailDto(String errorMessage, WebRequest request) {
        return ProblemDetailDTO
                .builder()
                .path(request.getDescription(false))
                .error(errorMessage)
                .timestamp(LocalDateTime.now())
                .build();
    }

    private void logError(String message, Exception e) {
        log.error(message);
    }
}

