package pl.rafzab.movielibraryservice.configuration.response;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class ResponseMaker {
    public static <T> ResponseEntity<ApiData<T>> ok(T dto) {
        return response(HttpStatus.OK, dto, ApiMessage.OK);
    }

    public static ResponseEntity<ApiData<Void>> created() {
        return response(HttpStatus.CREATED, null, ApiMessage.CREATED);
    }

    public static ResponseEntity<ApiData<Void>> updated() {
        return response(HttpStatus.NO_CONTENT, null, ApiMessage.UPDATED);
    }

    private static <T> ResponseEntity<ApiData<T>> response(HttpStatus status, T dto, ApiMessage message) {
        ApiData<T> apiData = new ApiData<>(message.name(), dto);
        return ResponseEntity.status(status).body(apiData);
    }
}
