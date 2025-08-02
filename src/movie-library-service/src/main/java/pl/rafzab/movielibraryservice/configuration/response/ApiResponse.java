package pl.rafzab.movielibraryservice.configuration.response;

import lombok.Getter;

@Getter
public class ApiResponse {

    private final int statusCode;

    private ApiData responseData;

    public ApiResponse(int statusCode) {
        this.statusCode = statusCode;
    }

    public ApiResponse(int statusCode, String message) {
        this.statusCode = statusCode;
        this.responseData = new ApiData<String>(message);
    }

    public ApiResponse(int statusCode, String message, Object data) {
        this.statusCode = statusCode;
        this.responseData = new ApiData<>(message, data);
    }
}
