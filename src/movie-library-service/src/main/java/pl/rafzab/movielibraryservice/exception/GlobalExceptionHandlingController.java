package pl.rafzab.movielibraryservice.exception;

import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.connector.ClientAbortException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import pl.rafzab.movielibraryservice.configuration.response.ApiData;
import pl.rafzab.movielibraryservice.configuration.response.ApiResponseException;

import java.time.ZoneId;
import java.time.ZonedDateTime;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandlingController {

    @ExceptionHandler(value = {NotFoundException.class})
    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    public ResponseEntity<ApiData<ApiResponseException>> handlerNotFoundException(Exception e) {
        ZonedDateTime zonedDateTime = ZonedDateTime.now(ZoneId.of("Z"));
        log.warn("ApiExceptionHandler [handlerNotFoundException] Exception -> \n " +
                "Date: " + zonedDateTime + " \n " +
                "Message Exception: " + e.getMessage() + " \n " +
                "Stacktrace Exception: ", e);
        HttpStatus httpStatus = HttpStatus.NOT_FOUND;

        ApiResponseException apiResponseException = new ApiResponseException(
                httpStatus,
                e.getMessage(),
                zonedDateTime
        );
        ApiData<ApiResponseException> response = new ApiData<>(HttpStatus.NOT_FOUND.name(), apiResponseException);
        return new ResponseEntity<>(response, httpStatus);
    }

    @ExceptionHandler(value = {UnauthorizedException.class})
    @ResponseStatus(value = HttpStatus.UNAUTHORIZED)
    public ResponseEntity<ApiData<ApiResponseException>> handlerUnauthorizedException(Exception e) {
        ZonedDateTime zonedDateTime = ZonedDateTime.now(ZoneId.of("Z"));
        log.warn("ApiExceptionHandler [handlerUnauthorizedException] Exception -> \n " +
                "Date: " + zonedDateTime + " \n " +
                "Message Exception: " + e.getMessage() + " \n " +
                "Stacktrace Exception: ", e);
        HttpStatus httpStatus = HttpStatus.UNAUTHORIZED;

        ApiResponseException apiResponseException = new ApiResponseException(
                httpStatus,
                e.getMessage(),
                zonedDateTime
        );
        ApiData<ApiResponseException> response = new ApiData<>(HttpStatus.UNAUTHORIZED.name(), apiResponseException);
        return new ResponseEntity<>(response, httpStatus);
    }

    /**
     * It catches all exceptions from @Valid
     */
    @ExceptionHandler(value = {MethodArgumentNotValidException.class, HttpMessageNotReadableException.class})
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public ResponseEntity<ApiData<ApiResponseException>> handlerApiRequestBadRequestValidException(Exception e) {
        ZonedDateTime zonedDateTime = ZonedDateTime.now(ZoneId.of("Z"));
        log.warn("ApiExceptionHandler [handlerApiRequestBadRequestValidException] Exception -> \n " +
                "Date: " + zonedDateTime + " \n " +
                "Message Exception: " + e.getMessage() + " \n " +
                "Stacktrace Exception: ", e);
        HttpStatus httpStatus = HttpStatus.BAD_REQUEST;

        ApiResponseException apiResponseException = new ApiResponseException(
                httpStatus,
                e.getMessage(),
                zonedDateTime
        );
        ApiData<ApiResponseException> response = new ApiData<>(HttpStatus.BAD_REQUEST.name(), apiResponseException);
        return new ResponseEntity<>(response, httpStatus);
    }

    /**
     * It catches dropped connections
     */
    @ExceptionHandler(value = {ClientAbortException.class})
    public void handlerApiClientAbortException(ClientAbortException e) {
        ZonedDateTime zonedDateTime = ZonedDateTime.now(ZoneId.of("Z"));
        log.debug("ApiExceptionHandler [handlerApiClientAbortException] Exception -> \n " +
                "Date: " + zonedDateTime + " \n " +
                "Message Exception: " + e.getMessage() + " \n " +
                "Stacktrace Exception: ", e);
    }

    /**
     * It catches any unhandled exceptions
     */
    @ExceptionHandler(value = {Exception.class})
    @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<ApiData<ApiResponseException>> handlerApiRequestInternalException(Exception e) {
        ZonedDateTime zonedDateTime = ZonedDateTime.now(ZoneId.of("Z"));
        log.error("Service: [Asset-Service] \n" +
                "ApiExceptionHandler [handlerApiRequestInternalException] Exception -> \n " +
                "Date: " + zonedDateTime + " \n " +
                "Message Exception: " + e.getMessage() + " \n " +
                "Stacktrace Exception: ", e);

        HttpStatus httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;

        ApiResponseException apiResponseException = new ApiResponseException(
                httpStatus,
                e.getMessage(),
                zonedDateTime
        );
        ApiData<ApiResponseException> response = new ApiData<>(HttpStatus.INTERNAL_SERVER_ERROR.name(), apiResponseException);
        return new ResponseEntity<>(response, httpStatus);
    }
}
