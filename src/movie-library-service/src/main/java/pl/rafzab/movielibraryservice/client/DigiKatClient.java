package pl.rafzab.movielibraryservice.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;
import pl.rafzab.movielibraryservice.exception.DigiKatClientException;
import pl.rafzab.movielibraryservice.exception.DigiKatServerException;

import java.io.IOException;

@Slf4j
@Component
public class DigiKatClient {

    private final RestClient restClient;
    private final DigiKatProperties properties;

    public DigiKatClient(DigiKatProperties properties) {
        this.properties = properties;
        this.restClient = createRestClient();
    }

    RestClient createRestClient() {
        return RestClient.builder()
                .baseUrl(properties.getBaseUrl())
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .requestFactory(new SimpleClientHttpRequestFactory())
                .build();
    }

    public DigiKatMovieData getMovieData(String title) {
        validateTitle(title);

        try {
            log.debug("Downloading movie data: {}", title);

            DigiKatMovieData response = restClient.get()
                    .uri("/ranking?film={title}", title)
                    .retrieve()
                    .onStatus(HttpStatusCode::is4xxClientError, this::handleClientError)
                    .onStatus(HttpStatusCode::is5xxServerError, this::handleServerError)
                    .body(DigiKatMovieData.class);

            log.debug("Data downloaded for the movie: {}", title);
            return response;
        } catch (RestClientResponseException e) {
            log.error("HTTP error while downloading movie data {}: status={}, body={}",
                    title, e.getStatusCode(), e.getResponseBodyAsString());
            throw new DigiKatClientException("Error while downloading movie data:" + title);

        } catch (RestClientException e) {
            log.error("Error communicating with DigiKat API for video {}: {}", title, e.getMessage());
            throw new DigiKatClientException("DigiKat API communication error");
        }
    }

    private void validateTitle(String title) {
        if (title == null || title.trim().isEmpty()) {
            throw new IllegalArgumentException("Movie title cannot be empty");
        }
        if (title.length() > 300) {
            throw new IllegalArgumentException("The title of the video cannot exceed 300 characters.");
        }
    }

    private void handleClientError(HttpRequest request, ClientHttpResponse response) throws IOException {
        log.warn("Customer error 4xx: {} for request: {}", response.getStatusCode(), request.getURI());
        throw new DigiKatClientException("Customer error: " + response.getStatusCode());
    }

    private void handleServerError(HttpRequest request, ClientHttpResponse response) throws IOException {
        log.error("Server error 5xx: {} for request: {}", response.getStatusCode(), request.getURI());
        throw new DigiKatServerException("Server error DigiKat: " + response.getStatusCode());
    }
}
