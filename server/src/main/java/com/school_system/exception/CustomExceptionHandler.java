package com.school_system.exception;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.school_system.common.ResponseObject;
import com.school_system.service.LoginAttemptService;
import io.jsonwebtoken.JwtException;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.BadRequestException;
import org.hibernate.HibernateException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.jdbc.BadSqlGrammarException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.sql.SQLException;
import java.sql.SQLSyntaxErrorException;



@Slf4j
@RestControllerAdvice
public class CustomExceptionHandler {

    @Autowired
    private HttpServletRequest request;
    @Autowired
    private LoginAttemptService loginAttemptService;
    @ExceptionHandler(value = DisabledException.class)
    public ResponseEntity<ResponseObject<?>> handleDisabledException(DisabledException e, WebRequest request) {

        log.warn("handleDisabledException");
        log.error(e.getMessage(), e);

        ResponseObject<?> response = ResponseObject.builder()
                .status(ResponseObject.ResponseStatus.FAILED)
                .message("Ihr Konto wurde noch nicht verifiziert. " +
                        "Bitte überprüfen Sie Ihre E-Mails und klicken Sie auf den Verifizierungslink, um Ihr Konto zu aktivieren.")
                .build();

        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
    }

    @ExceptionHandler(value = JwtException.class)
    public ResponseEntity<ResponseObject<?>> handleJwtException(JwtException e) {

        log.warn("handleJwtException");
        log.error(e.getMessage(), e);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Access-Control-Allow-Origin", request.getHeader("Origin"));
        headers.add("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        headers.add("Access-Control-Allow-Headers", "Content-Type, Authorization");
        headers.add("Access-Control-Allow-Credentials", "true");

        ResponseObject<?> response =  ResponseObject.builder()
                .status(ResponseObject.ResponseStatus.FAILED)
                .message(e.getMessage())
                .build();

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).headers(headers).body(response);
    }

    @ExceptionHandler(value = AccessDeniedException.class)
    public ResponseEntity<ResponseObject<?>> handleAccessDeniedException(AccessDeniedException e) {

        log.warn("handleAccessDeniedException");
        log.error(e.getMessage(), e);
        ResponseObject<?> response =  ResponseObject.builder()
                .status(ResponseObject.ResponseStatus.FAILED)
                .message("Zugriff verweigert: Sie sind nicht befugt, diese Aktion auszuführen!")
                .build();

        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
    }

    @ExceptionHandler(value = BadCredentialsException.class)
    public ResponseEntity<ResponseObject<?>> handleBadCredentialsException(BadCredentialsException e) {

        log.warn("handleBadCredentialsException");
        log.error(e.getMessage(), e);
/*        HttpServletRequest request = ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getRequest();
        final String ipAddress = getClientIP(request)
        loginAttemptService.loginFailed(ipAddress);
        ;
 */
        ResponseObject<?> response =  ResponseObject.builder()
                .status(ResponseObject.ResponseStatus.FAILED)
                .message(e.getMessage())
                .build();

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }

    @ExceptionHandler(value = EntityNotFoundException.class)
    public ResponseEntity<ResponseObject<?>> handleEntityNotFoundException(EntityNotFoundException e) {

        log.warn("handleEntityNotFoundException");
        log.error(e.getMessage(), e);

        ResponseObject<?> response = ResponseObject.builder()
                .status(ResponseObject.ResponseStatus.FAILED)
                .message(e.getMessage())
                .build();

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @ExceptionHandler(UserNotVerifiedException.class)
    public ResponseEntity<ResponseObject<?>> handleUserNotVerifiedException(UserNotVerifiedException e) {
        String errorMessage = "Bei der Verarbeitung Ihrer Anfrage ist ein Fehler aufgetreten.";
        // You can customize the error message further if needed
        log.warn("handleUserNotVerifiedException");
        log.error(e.getMessage(), e);

        ResponseObject<?> response = ResponseObject.builder()
                .status(ResponseObject.ResponseStatus.FAILED)
                .message(errorMessage)
                .build();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
    @ExceptionHandler(BadSqlGrammarException.class)
    public ResponseEntity<ResponseObject<?>> handleBadSqlGrammarException(BadSqlGrammarException e) {
        String errorMessage = "Bei der Verarbeitung Ihrer Anfrage ist ein Fehler aufgetreten.";
        // You can customize the error message further if needed
        log.warn("handleBadSqlGrammarException");
        log.error(e.getMessage(), e);

        ResponseObject<?> response = ResponseObject.builder()
                .status(ResponseObject.ResponseStatus.FAILED)
                .message(errorMessage)
                .build();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
    @ExceptionHandler(DataIntegrityViolationException.class)
    @ResponseBody
    public ResponseEntity<ResponseObject<?>> handleDuplicateKeyException(DataIntegrityViolationException e) {
        // Customize the error response for duplicate key violation
        String errorMessage = "Etwas ist Schiefgelaufen, bitte kontaktieren Sie uns (ID: 2001).";
        log.warn("handleDuplicateKeyException");
        log.error(errorMessage, e);

        ResponseObject<?> response = ResponseObject.builder()
                .status(ResponseObject.ResponseStatus.FAILED)
                .message(errorMessage)
                .build();
        // FIXME Internal Server Erro while user can not cause a DataIntegrity Error normally
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }
    @ExceptionHandler({SQLException.class, DataAccessException.class, HibernateException.class})
    @ResponseBody
    public ResponseEntity<ResponseObject<?>> handleDatabaseException(Exception e) {
        // Customize the error response as needed
        log.warn("handleDatabaseException");
        log.error(e.getMessage(), e);

        ResponseObject<?> response = ResponseObject.builder()
                .status(ResponseObject.ResponseStatus.FAILED)
                .message("Bei der Verarbeitung Ihrer Anfrage ist ein Fehler(DataAccess) aufgetreten.")
                .build();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ResponseObject<?>> handleMethodNotAllowedException(HttpRequestMethodNotSupportedException e) {

        log.warn("handleMethodNotAllowedException");
        log.error(e.getMessage(), e);

        ResponseObject<?> response = ResponseObject.builder()
                .status(ResponseObject.ResponseStatus.FAILED)
                .message(e.getMessage())
                .build();
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).body(response);
    }
    @ExceptionHandler(value = EntityExistsException.class)
    public ResponseEntity<ResponseObject<?>> handleEntityExistsException(EntityExistsException e) {

        log.warn("handleEntityExistsException");
        log.error(e.getMessage(), e);

        ResponseObject<?> response = ResponseObject.builder()
                .status(ResponseObject.ResponseStatus.FAILED)
                .message(e.getMessage())
                .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(value = ConstraintViolationException.class)
    public ResponseEntity<ResponseObject<?>> handleConstraintViolationException(ConstraintViolationException e) {

        log.warn("handleConstraintViolationException");
        log.error(e.getMessage(), e);

        ResponseObject<?> response = ResponseObject.builder()
                .status(ResponseObject.ResponseStatus.FAILED)
                .message(e.getMessage())
                .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public ResponseEntity<ResponseObject<?>> handleInvalidMethodArgumentException(MethodArgumentNotValidException e) {

        log.warn("handleInvalidMethodArgumentException");
        log.error(e.getMessage(), e);

        ResponseObject<?> response = ResponseObject.builder()
                .status(ResponseObject.ResponseStatus.FAILED)
                .message(e.getBindingResult().getAllErrors().get(0).getDefaultMessage())
                .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }
    @ExceptionHandler(value = LessonValidationException.class)
    public ResponseEntity<ResponseObject<?>> handleInvalidMethodArgumentException(LessonValidationException e) {

        log.warn("handleInvalidMethodArgumentException");
        log.error(e.getMessage(), e);

        ResponseObject<?> response = ResponseObject.builder()
                .status(ResponseObject.ResponseStatus.FAILED)
                .message(e.getMessage())
                .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(value = TokenException.class)
    public ResponseEntity<ResponseObject<?>> handleConfirmationTokenException(TokenException e) {

        log.warn("handleConfirmationTokenException");
        log.error(e.getMessage(), e);

        ResponseObject<?> response = ResponseObject.builder()
                .status(ResponseObject.ResponseStatus.FAILED)
                .message(e.getMessage())
                .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }
    @ExceptionHandler(value = ExistByNameException.class)
    public ResponseEntity<ResponseObject<?>> handleConfirmationTokenException(ExistByNameException e) {

        log.warn("handleConfirmationTokenException");
        log.error(e.getMessage(), e);

        ResponseObject<?> response = ResponseObject.builder()
                .status(ResponseObject.ResponseStatus.FAILED)
                .message(e.getMessage())
                .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }


    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ResponseObject<?>> handleMethodArgumentTypeMismatch(MethodArgumentTypeMismatchException e) {
        // Handle the specific exception and return a custom response
        log.warn("handleMethodArgumentTypeMismatch");
        log.error(e.getMessage(), e);

        ResponseObject<?> response = ResponseObject.builder()
                .status(ResponseObject.ResponseStatus.FAILED)
                .message("Endpunkt nicht gefunden oder fehlerhafte Params .")
                .build();
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);

    }
    @ExceptionHandler(value = TenantException.class)
    public ResponseEntity<ResponseObject<?>> handleTenantException(TenantException e) {

        log.warn("handleTenantException");
        log.error(e.getMessage(), e);

        ResponseObject<?> response = ResponseObject.builder()
                .status(ResponseObject.ResponseStatus.FAILED)
                .message(e.getMessage())
                .build();

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }
    @ExceptionHandler(value = NoResourceFoundException.class)
    public ResponseEntity<ResponseObject<?>> handleNoResourceFoundException(NoResourceFoundException e) {

        log.warn("handleNoResourceFoundException");
        log.error(e.getMessage(), e);

        ResponseObject<?> response = ResponseObject.builder()
                .status(ResponseObject.ResponseStatus.FAILED)
                .message("404 Nicht gefunden!")
                .build();

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ResponseObject<?>> handleHttpMessageNotReadableException(HttpMessageNotReadableException e) {
        if (e.getCause() instanceof JsonMappingException jsonEx) {
            String propertyName = jsonEx.getPath().stream()
                    .map(JsonMappingException.Reference::getFieldName)
                    .findFirst()
                    .orElse("Unbekannte Eigenschaft.");
            String errorMessage = "Ungültiger Wert für Eigenschaft: " + propertyName;
            ResponseObject<?> response = ResponseObject.builder()
                    .status(ResponseObject.ResponseStatus.FAILED)
                    .message(errorMessage)
                    .build();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        } else {
            ResponseObject<?> response = ResponseObject.builder()
                    .status(ResponseObject.ResponseStatus.FAILED)
                    .message("Ungültige Eingabedaten. Bitte geben Sie gültiges JSON an.")
                    .build();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    @ExceptionHandler(value = BadRequestException.class)
    public ResponseEntity<ResponseObject<?>> handleGenericBadRequestException(BadRequestException e) {

        log.warn("handleGenericBadRequestException");
        log.error(e.getMessage(), e);

        ResponseObject<?> response = ResponseObject.builder()
                .status(ResponseObject.ResponseStatus.FAILED)
                .message(e.getMessage())
                .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }
    @ExceptionHandler(value = BankDataRequiredException.class)
    public ResponseEntity<ResponseObject<?>> handleBankDataException(BankDataRequiredException e) {

        log.warn("handleBankDataException");
        log.error(e.getMessage(), e);

        ResponseObject<?> response = ResponseObject.builder()
                .status(ResponseObject.ResponseStatus.FAILED)
                .message(e.getMessage())
                .build();

        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(response);
    }
    @ExceptionHandler(value = MaxUploadSizeExceededException.class)
    public ResponseEntity<ResponseObject<?>> handleMaxUploadSizeExceededException(MaxUploadSizeExceededException e) {

        log.warn("handleMaxUploadSizeExceededException");
        log.error(e.getMessage(), e);

        ResponseObject<?> response = ResponseObject.builder()
                .status(ResponseObject.ResponseStatus.FAILED)
                .message("Maximale Dateigroße ist 20MB")
                .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }
    @ExceptionHandler(StorageFileNotFoundException.class)
    public ResponseEntity<?> handleStorageFileNotFound(StorageFileNotFoundException exc) {
        log.warn("handleStorageFileNotFound");
        log.error(exc.getMessage(), exc);
        ResponseObject<?> response = ResponseObject.builder()
                .status(ResponseObject.ResponseStatus.FAILED)
                .message("Datei wurde nicht gefunden")
                .build();
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }


    @ExceptionHandler(value = SQLSyntaxErrorException.class)
    public  ResponseEntity<ResponseObject<?>> handleSQLSyntaxErrorException(SQLSyntaxErrorException e) {
        log.warn("handleSQLSyntaxErrorException");
        log.error(e.getMessage(), e);

        ResponseObject<?> response = ResponseObject.builder()
                .status(ResponseObject.ResponseStatus.FAILED)
                .message("Bei der Verarbeitung Ihrer Anfrage ist ein Fehler(SyntaxError) aufgetreten. Bitte kontaktieren Sie uns!")
                .build();

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
    @ExceptionHandler(value = RateLimitingException.class)
    public  ResponseEntity<ResponseObject<?>> handleRateLimitingException(RateLimitingException e) {
        log.warn("handleRateLimitingException");
        log.error(e.getMessage(), e);

        ResponseObject<?> response = ResponseObject.builder()
                .status(ResponseObject.ResponseStatus.FAILED)
                .message(e.getMessage())
                .build();

        return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body(response);
    }

    @ExceptionHandler(value = InternalAuthenticationServiceException.class)
    public ResponseEntity<ResponseObject<?>> handleInternalAuthenticationServiceException(InternalAuthenticationServiceException e) {
        // Check if the cause is a RateLimitingException
        Throwable cause = e.getCause();
        if (cause instanceof RateLimitingException) {
            return handleRateLimitingException((RateLimitingException) cause);
        }

        log.warn("handleInternalAuthenticationServiceException");
        log.error(e.getMessage(), e);

        ResponseObject<?> response = ResponseObject.builder()
                .status(ResponseObject.ResponseStatus.FAILED)
                .message("Bei der Verarbeitung Ihrer Anfrage ist ein Fehler aufgetreten.")
                .build();

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
    @ExceptionHandler(value = Exception.class)
    public ResponseEntity<ResponseObject<?>> handleGenericException(Exception e) {

        log.warn("handleGenericException");
        log.error(e.getMessage(), e);

        ResponseObject<?> response = ResponseObject.builder()
                .status(ResponseObject.ResponseStatus.FAILED)
                .message("Bei der Verarbeitung Ihrer Anfrage ist ein Fehler aufgetreten.")
                .build();

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }


}
