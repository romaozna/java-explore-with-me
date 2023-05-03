package ru.practicum.ewm.exception;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.LocalDateTime;
import java.util.Objects;

@Slf4j
@RestControllerAdvice("ru.practicum.ewm")
public class ErrorController {

    @ExceptionHandler({MethodArgumentNotValidException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleBadRequest(final MethodArgumentNotValidException e) {
        String errorMessage = e.getBindingResult().getAllErrors().get(0).getDefaultMessage();
        String field = Objects.requireNonNull(e.getBindingResult().getFieldError()).getField();
        Object rejectedValue = e.getBindingResult().getFieldValue(field);
        String message = String.format("Field: %s. Error: %s. Value: %s", field, errorMessage, rejectedValue);
        log.error(e.getLocalizedMessage());

        return new ErrorResponse(
                HttpStatus.BAD_REQUEST,
                "Incorrectly made request.",
                message, getStackTraceString(e));
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleMissingParams(MissingServletRequestParameterException e) {
        log.error(e.getLocalizedMessage());

        return new ErrorResponse(
                HttpStatus.BAD_REQUEST,
                "Incorrectly made request.",
                e.getLocalizedMessage(), getStackTraceString(e));
    }

    @ExceptionHandler({NotFoundException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleNotFound(final RuntimeException e) {
        log.error(e.getLocalizedMessage());

        return new ErrorResponse(
                HttpStatus.NOT_FOUND,
                "The required object was not found.",
                e.getLocalizedMessage(), getStackTraceString(e));
    }

    @ExceptionHandler({OperationException.class})
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleOperationException(final RuntimeException e) {
        log.error(e.getLocalizedMessage());

        return new ErrorResponse(
                HttpStatus.CONFLICT,
                "For the requested operation the conditions are not met.",
                e.getLocalizedMessage(), getStackTraceString(e));
    }

    @ExceptionHandler({DataIntegrityViolationException.class})
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleViolationException(final DataIntegrityViolationException e) {
        log.error(e.getLocalizedMessage());

        return new ErrorResponse(
                HttpStatus.CONFLICT,
                "Integrity constraint has been violated.",
                e.getLocalizedMessage(), getStackTraceString(e));
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleThrowable(final Throwable e) {
        log.error(e.getMessage());

        return new ErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR,
                HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(),
                e.getMessage(), getStackTraceString(e));
    }

    private String getStackTraceString(Throwable e) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        e.printStackTrace(pw);
        return sw.toString().replace("\r\n\t", " -> ");
    }

    @Value
    @Builder
    @AllArgsConstructor
    @Jacksonized
    public static class ErrorResponse {
        HttpStatus status;
        String reason;
        String message;
        String errors;
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
        LocalDateTime timestamp = LocalDateTime.now();
    }
}
