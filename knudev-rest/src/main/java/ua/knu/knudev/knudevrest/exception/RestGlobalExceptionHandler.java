package ua.knu.knudev.knudevrest.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ua.knu.knudev.assessmentmanagerapi.exception.TaskAssignmentException;
import ua.knu.knudev.assessmentmanagerapi.exception.TaskException;
import ua.knu.knudev.knudevcommon.constant.Expertise;
import ua.knu.knudev.knudevcommon.exception.FileException;
import ua.knu.knudev.knudevsecurityapi.exception.AccountAuthException;
import ua.knu.knudev.knudevsecurityapi.exception.TokenException;
import ua.knu.knudev.teammanagerapi.exception.AccountException;
import ua.knu.knudev.teammanagerapi.exception.DepartmentException;
import ua.knu.knudev.teammanagerapi.exception.RecruitmentException;

import java.nio.charset.StandardCharsets;
import java.util.*;

@RestControllerAdvice
public class RestGlobalExceptionHandler {

    @ExceptionHandler
    public ResponseEntity<String> handleAccountException(AccountException exception) {
        return new ResponseEntity<>(exception.getMessage(), exception.getStatus());
    }

    @ExceptionHandler(DepartmentException.class)
    public ResponseEntity<String> handleDepartmentException(DepartmentException exception) {
        MediaType mediaType = new MediaType(MediaType.TEXT_PLAIN, StandardCharsets.UTF_8);
        HttpStatus statusCode = exception.getStatusCode() == null ? HttpStatus.CONFLICT : exception.getStatusCode();

        return ResponseEntity
                .status(statusCode)
                .contentType(mediaType)
                .body(exception.getMessage());
    }

    @ExceptionHandler
    public ResponseEntity<String> handleTaskException(TaskException ex) {
        return new ResponseEntity<>(ex.getMessage(), ex.getStatusCode());
    }

    @ExceptionHandler
    public ResponseEntity<String> handleTokenException(TokenException ex) {
        return new ResponseEntity<>(ex.getMessage(), ex.getStatusCode());
    }

    @ExceptionHandler
    public ResponseEntity<String> handleTaskAssignmentException(TaskAssignmentException ex) {
        return new ResponseEntity<>(ex.getMessage(), ex.getStatusCode());
    }

    @ExceptionHandler(FileException.class)
    public String handleFileException(FileException exception) {
        return exception.getMessage();
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, List<String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, List<String>> errors = new HashMap<>();

        ex.getBindingResult().getFieldErrors().forEach(fieldError -> {
            String fieldKey = fieldError.getField() + "Errors";
            String errorMessage = buildErrorMessage(fieldError);

            List<String> fieldErrorList = errors.computeIfAbsent(fieldKey, k -> new ArrayList<>());
            fieldErrorList.add(errorMessage);
        });

        return errors;
    }

    @ExceptionHandler
    public String handleRecruitmentException(RecruitmentException recruitmentException) {
        return recruitmentException.getMessage();
    }

    @ExceptionHandler
    public String handleHttpMessageNotReadableException(HttpMessageNotReadableException exception) {
        return exception.getMessage();
    }

    @ExceptionHandler
    public String handleHttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException exception) {
        return exception.getMessage();
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<String> handleBadCredentialsException(AccountAuthException exception) {
        return new ResponseEntity<>(exception.getMessage(), exception.getStatus());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleIllegalArgumentException(IllegalArgumentException exception) {
        return exception.getMessage();
    }

    private String buildErrorMessage(FieldError fieldError) {
        if ("expertise".equals(fieldError.getField())
                && "typeMismatch".equals(fieldError.getCode())) {
            return "Invalid Expertise value. Possible values are: " + Arrays.toString(Expertise.values());
        }
        return fieldError.getDefaultMessage();
    }

}
