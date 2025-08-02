package pl.rafzab.movielibraryservice.configuration.response;

import lombok.Getter;

@Getter
public class ApiData<T> {

    private final String message;

    private T data;

    public ApiData(String message) {
        this.message = message;
    }

    public ApiData(String message, T data) {
        this.message = message;
        this.data = data;
    }
}
