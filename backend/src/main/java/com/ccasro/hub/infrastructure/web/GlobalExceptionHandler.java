package com.ccasro.hub.infrastructure.web;

import jakarta.servlet.http.HttpServletRequest;
import java.net.URI;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(AccessDeniedException.class)
  public ResponseEntity<ProblemDetail> handleAccessDenied(
      AccessDeniedException ex, HttpServletRequest request) {

    return ResponseEntity.status(HttpStatus.FORBIDDEN)
        .body(
            problem(
                HttpStatus.FORBIDDEN,
                "/errors/forbidden",
                "Forbidden",
                "You don't have permission to access this resource",
                request));
  }

  @ExceptionHandler(AuthenticationException.class)
  public ResponseEntity<ProblemDetail> handleAuthentication(
      AuthenticationException ex, HttpServletRequest request) {

    return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
        .body(
            problem(
                HttpStatus.UNAUTHORIZED,
                "/errors/unauthorized",
                "Unauthorized",
                "Authentication is required to access this resource",
                request));
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ProblemDetail> handleMethodArgumentNotValid(
      MethodArgumentNotValidException ex, HttpServletRequest request) {

    ProblemDetail pd =
        problem(
            HttpStatus.BAD_REQUEST,
            "/errors/validation-error",
            "Validation Error",
            "Request validation failed",
            request);

    Map<String, String> errors = new LinkedHashMap<>();
    ex.getBindingResult()
        .getFieldErrors()
        .forEach(fe -> errors.put(fe.getField(), fe.getDefaultMessage()));

    pd.setProperty("errors", errors);

    return ResponseEntity.badRequest().body(pd);
  }

  @ExceptionHandler(HttpMessageNotReadableException.class)
  public ResponseEntity<ProblemDetail> handleNotReadable(
      HttpMessageNotReadableException ex, HttpServletRequest request) {

    return ResponseEntity.badRequest()
        .body(
            problem(
                HttpStatus.BAD_REQUEST,
                "/errors/malformed-json",
                "Malformed JSON",
                "Request body is not readable or has invalid JSON",
                request));
  }

  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<ProblemDetail> handleIllegalArgument(
      IllegalArgumentException ex, HttpServletRequest request) {

    return ResponseEntity.badRequest()
        .body(
            problem(
                HttpStatus.BAD_REQUEST,
                "/errors/bad-request",
                "Bad Request",
                ex.getMessage(),
                request));
  }

  @ExceptionHandler(NoSuchElementException.class)
  public ResponseEntity<ProblemDetail> handleNoSuchElement(
      NoSuchElementException ex, HttpServletRequest request) {

    return ResponseEntity.status(HttpStatus.NOT_FOUND)
        .body(
            problem(
                HttpStatus.NOT_FOUND, "/errors/not-found", "Not Found", ex.getMessage(), request));
  }

  @ExceptionHandler(IllegalStateException.class)
  public ResponseEntity<ProblemDetail> handleIllegalState(
      IllegalStateException ex, HttpServletRequest request) {

    return ResponseEntity.status(HttpStatus.CONFLICT)
        .body(
            problem(HttpStatus.CONFLICT, "/errors/conflict", "Conflict", ex.getMessage(), request));
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ProblemDetail> handleGenericException(
      Exception ex, HttpServletRequest request) {

    log.error("Unhandled exception at {}", request.getRequestURI(), ex);

    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
        .body(
            problem(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "/errors/internal-server-error",
                "Internal Server Error",
                "Unexpected error occurred",
                request));
  }

  private ProblemDetail problem(
      HttpStatus status, String type, String title, String detail, HttpServletRequest request) {

    ProblemDetail pd = ProblemDetail.forStatusAndDetail(status, detail);
    pd.setType(URI.create(type));
    pd.setTitle(title);
    pd.setInstance(URI.create(request.getRequestURI()));
    pd.setProperty("timestamp", Instant.now().toString());

    String traceId = MDC.get("traceId");
    if (traceId != null && !traceId.isBlank()) {
      pd.setProperty("traceId", traceId);
    }

    return pd;
  }
}
