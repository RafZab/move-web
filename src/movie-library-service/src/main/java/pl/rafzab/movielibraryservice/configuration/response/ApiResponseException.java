package pl.rafzab.movielibraryservice.configuration.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.time.ZonedDateTime;

@Getter
@AllArgsConstructor
public class ApiResponseException {
    private final HttpStatus httpStatus;
    private final String message;
    private final ZonedDateTime timestamp;
}
