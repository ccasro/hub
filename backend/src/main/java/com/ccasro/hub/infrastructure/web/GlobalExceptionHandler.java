package com.ccasro.hub.infrastructure.web;

import com.ccasro.hub.modules.booking.domain.exception.BookingCancellationNotAllowedException;
import com.ccasro.hub.modules.booking.domain.exception.BookingNotFoundException;
import com.ccasro.hub.modules.booking.domain.exception.SlotNotAvailableException;
import com.ccasro.hub.modules.iam.domain.exception.UserProfileNotFoundException;
import com.ccasro.hub.modules.matching.domain.exception.*;
import com.ccasro.hub.modules.resource.domain.exception.ResourceImageNotFoundException;
import com.ccasro.hub.modules.resource.domain.exception.ResourceNotFoundException;
import com.ccasro.hub.modules.venue.domain.exception.VenueImageNotFoundException;
import com.ccasro.hub.modules.venue.domain.exception.VenueNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.net.URI;
import java.time.Instant;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.NoSuchElementException;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

  @Value("${app.expose-trace-id:false}")
  private boolean exposeTraceId;

  @ExceptionHandler(AccessDeniedException.class)
  public ResponseEntity<ProblemDetail> handleAccessDenied(
      AccessDeniedException ex, HttpServletRequest request) {

    log.warn("Access denied at {}: {}", request.getRequestURI(), ex.getMessage());

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

    log.warn("Authentication failure at {}: {}", request.getRequestURI(), ex.getMessage());

    return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
        .body(
            problem(
                HttpStatus.UNAUTHORIZED,
                "/errors/unauthorized",
                "Unauthorized",
                "Authentication is required to access this resource",
                request));
  }

  @ExceptionHandler(MethodArgumentTypeMismatchException.class)
  public ResponseEntity<ProblemDetail> handleMethodArgumentTypeMismatch(
      MethodArgumentTypeMismatchException ex, HttpServletRequest request) {

    ProblemDetail pd = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
    pd.setType(URI.create("/errors/validation-error"));
    pd.setTitle("Validation Error");
    pd.setDetail("Invalid request parameter");
    pd.setInstance(URI.create(request.getRequestURI()));

    Map<String, Object> errors = new LinkedHashMap<>();
    errors.put("parameter", ex.getName());
    errors.put("rejectedValue", ex.getValue());
    errors.put(
        "expectedType",
        ex.getRequiredType() != null ? ex.getRequiredType().getSimpleName() : "unknown");

    if (ex.getRequiredType() == LocalDate.class) {
      errors.put("message", "Expected format: yyyy-MM-dd (e.g. 2026-02-04)");
    }

    pd.setProperty("errors", errors);

    return ResponseEntity.badRequest().body(pd);
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

  @ExceptionHandler(ConstraintViolationException.class)
  public ResponseEntity<ProblemDetail> constraint(
      ConstraintViolationException ex, HttpServletRequest req) {

    ProblemDetail pd =
        problem(
            HttpStatus.BAD_REQUEST,
            "/errors/validation-error",
            "Validation Error",
            "Request parameter validation failed",
            req);

    Map<String, String> errors = new LinkedHashMap<>();
    ex.getConstraintViolations()
        .forEach(v -> errors.put(v.getPropertyPath().toString(), v.getMessage()));
    pd.setProperty("errors", errors);

    return ResponseEntity.badRequest().body(pd);
  }

  @ExceptionHandler(DataIntegrityViolationException.class)
  public ResponseEntity<ProblemDetail> conflict(
      DataIntegrityViolationException ex, HttpServletRequest req) {

    log.warn("DataIntegrityViolationException at {}: {}", req.getRequestURI(), ex.getMessage());

    ProblemDetail pd =
        problem(
            HttpStatus.CONFLICT,
            "/errors/conflict",
            "Conflict",
            "Operation violates a data constraint",
            req);
    return ResponseEntity.status(HttpStatus.CONFLICT).body(pd);
  }

  @ExceptionHandler(MissingServletRequestParameterException.class)
  public ResponseEntity<ProblemDetail> missingParam(
      MissingServletRequestParameterException ex, HttpServletRequest req) {

    ProblemDetail pd =
        problem(
            HttpStatus.BAD_REQUEST,
            "/errors/missing-parameter",
            "Missing Parameter",
            "Required request parameter is missing",
            req);
    pd.setProperty("parameter", ex.getParameterName());
    pd.setProperty("expectedType", ex.getParameterType());
    return ResponseEntity.badRequest().body(pd);
  }

  @ExceptionHandler(HttpMessageNotReadableException.class)
  public ResponseEntity<ProblemDetail> handleNotReadable(
      HttpMessageNotReadableException ex, HttpServletRequest request) {

    log.warn("Malformed request body at {}: {}", request.getRequestURI(), ex.getMessage());

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

    log.warn("IllegalArgumentException at {}: {}", request.getRequestURI(), ex.getMessage());

    return ResponseEntity.badRequest()
        .body(
            problem(
                HttpStatus.BAD_REQUEST,
                "/errors/bad-request",
                "Bad Request",
                "Invalid request",
                request));
  }

  @ExceptionHandler(UserProfileNotFoundException.class)
  public ResponseEntity<ProblemDetail> handleUserProfileNotFound(
      UserProfileNotFoundException ex, HttpServletRequest request) {

    return ResponseEntity.status(HttpStatus.NOT_FOUND)
        .body(
            problem(
                HttpStatus.NOT_FOUND, "/errors/not-found", "Not Found", ex.getMessage(), request));
  }

  @ExceptionHandler(VenueNotFoundException.class)
  public ResponseEntity<ProblemDetail> handleVenueNotFound(
      VenueNotFoundException ex, HttpServletRequest request) {

    return ResponseEntity.status(HttpStatus.NOT_FOUND)
        .body(
            problem(
                HttpStatus.NOT_FOUND, "/errors/not-found", "Not Found", ex.getMessage(), request));
  }

  @ExceptionHandler(VenueImageNotFoundException.class)
  public ResponseEntity<ProblemDetail> handleVenueImageNotFound(
      VenueImageNotFoundException ex, HttpServletRequest request) {

    return ResponseEntity.status(HttpStatus.NOT_FOUND)
        .body(
            problem(
                HttpStatus.NOT_FOUND, "/errors/not-found", "Not Found", ex.getMessage(), request));
  }

  @ExceptionHandler(ResourceNotFoundException.class)
  public ResponseEntity<ProblemDetail> handleResourceNotFound(
      ResourceNotFoundException ex, HttpServletRequest request) {

    return ResponseEntity.status(HttpStatus.NOT_FOUND)
        .body(
            problem(
                HttpStatus.NOT_FOUND, "/errors/not-found", "Not Found", ex.getMessage(), request));
  }

  @ExceptionHandler(ResourceImageNotFoundException.class)
  public ResponseEntity<ProblemDetail> handleResourceImageNotFound(
      ResourceImageNotFoundException ex, HttpServletRequest request) {

    return ResponseEntity.status(HttpStatus.NOT_FOUND)
        .body(
            problem(
                HttpStatus.NOT_FOUND, "/errors/not-found", "Not Found", ex.getMessage(), request));
  }

  @ExceptionHandler(BookingNotFoundException.class)
  public ResponseEntity<ProblemDetail> handleBookingNotFound(
      BookingNotFoundException ex, HttpServletRequest request) {

    return ResponseEntity.status(HttpStatus.NOT_FOUND)
        .body(
            problem(
                HttpStatus.NOT_FOUND, "/errors/not-found", "Not Found", ex.getMessage(), request));
  }

  @ExceptionHandler(NoSuchElementException.class)
  public ResponseEntity<ProblemDetail> handleNoSuchElement(
      NoSuchElementException ex, HttpServletRequest request) {

    log.warn("NoSuchElementException at {}: {}", request.getRequestURI(), ex.getMessage());

    return ResponseEntity.status(HttpStatus.NOT_FOUND)
        .body(
            problem(
                HttpStatus.NOT_FOUND,
                "/errors/not-found",
                "Not Found",
                "Resource not found",
                request));
  }

  @ExceptionHandler(SlotNotAvailableException.class)
  public ResponseEntity<ProblemDetail> handleSlotNotAvailable(
      SlotNotAvailableException ex, HttpServletRequest request) {

    return ResponseEntity.status(HttpStatus.CONFLICT)
        .body(
            problem(HttpStatus.CONFLICT, "/errors/conflict", "Conflict", ex.getMessage(), request));
  }

  @ExceptionHandler(IllegalStateException.class)
  public ResponseEntity<ProblemDetail> handleIllegalState(
      IllegalStateException ex, HttpServletRequest request) {

    log.warn("IllegalStateException at {}: {}", request.getRequestURI(), ex.getMessage());

    return ResponseEntity.status(HttpStatus.CONFLICT)
        .body(
            problem(
                HttpStatus.CONFLICT,
                "/errors/conflict",
                "Conflict",
                "Operation not allowed in current state",
                request));
  }

  @ExceptionHandler(BookingCancellationNotAllowedException.class)
  public ResponseEntity<ProblemDetail> handleBookingCancellationNotAllowed(
      BookingCancellationNotAllowedException ex, HttpServletRequest request) {

    return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
        .body(
            problem(
                HttpStatus.UNPROCESSABLE_ENTITY,
                "/errors/unprocessable",
                "Unprocessable",
                ex.getMessage(),
                request));
  }

  @ExceptionHandler(MatchNotFoundException.class)
  public ResponseEntity<ProblemDetail> handleMatchNotFound(
      MatchNotFoundException ex, HttpServletRequest request) {

    return ResponseEntity.status(HttpStatus.NOT_FOUND)
        .body(
            problem(
                HttpStatus.NOT_FOUND,
                "/errors/not-found",
                "Match Not Found",
                ex.getMessage(),
                request));
  }

  @ExceptionHandler({
    MatchFullException.class,
    TeamFullException.class,
    PlayerAlreadyJoinedException.class,
    MatchNotOpenException.class
  })
  public ResponseEntity<ProblemDetail> handleMatchConflict(
      RuntimeException ex, HttpServletRequest request) {

    return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
        .body(
            problem(
                HttpStatus.UNPROCESSABLE_ENTITY,
                "/errors/unprocessable",
                "Match Error",
                ex.getMessage(),
                request));
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

    if (exposeTraceId) {
      String traceId = MDC.get("traceId");
      if (traceId != null && !traceId.isBlank()) {
        pd.setProperty("traceId", traceId);
      }
    }

    return pd;
  }
}
