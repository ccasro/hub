package com.ccasro.hub.common.api.error;

import jakarta.servlet.http.HttpServletRequest;
import java.net.URI;
import java.time.Instant;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(AccessDeniedException.class)
  public ProblemDetail handleAccessDenied(AccessDeniedException ex, HttpServletRequest request) {
    return problem(
        HttpStatus.FORBIDDEN,
        "/errors/forbidden",
        "Forbidden",
        "You don't have permission to access this resource",
        request);
  }

  @ExceptionHandler(AuthenticationException.class)
  public ProblemDetail handleAuthentication(
      AuthenticationException ex, HttpServletRequest request) {
    return problem(
        HttpStatus.UNAUTHORIZED,
        "/errors/unauthorized",
        "Unauthorized",
        "Authentication is required to access this resource",
        request);
  }

  @ExceptionHandler(Exception.class)
  public ProblemDetail handleGenericException(Exception ex, HttpServletRequest request) {

    log.error("Unhandled exception at {}", request.getRequestURI(), ex);

    return problem(
        HttpStatus.INTERNAL_SERVER_ERROR,
        "/errors/internal-server-error",
        "Internal Server Error",
        "Unexpected error occurred",
        request);
  }

  private ProblemDetail problem(
      HttpStatus status, String type, String title, String detail, HttpServletRequest request) {
    ProblemDetail pd = ProblemDetail.forStatus(status);

    pd.setType(URI.create(type));
    pd.setTitle(title);
    pd.setDetail(detail);
    pd.setInstance(URI.create(request.getRequestURI()));
    pd.setProperty("timestamp", Instant.now().toString());

    String traceId = MDC.get("traceId");
    if (traceId != null && !traceId.isBlank()) {
      pd.setProperty("traceId", traceId);
    }

    return pd;
  }
}
