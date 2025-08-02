package pl.rafzab.movielibraryservice.client;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import pl.rafzab.movielibraryservice.exception.DigiKatClientException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

class DigiKatClientTest {

    private DigiKatProperties properties;
    private DigiKatClient client;
    private RestClient restClientMock;

    @BeforeEach
    void setUp() {
        properties = mock(DigiKatProperties.class);
        when(properties.getBaseUrl()).thenReturn("https://api.digikat.test");

        restClientMock = mock(RestClient.class, RETURNS_DEEP_STUBS);

        client = new DigiKatClient(properties) {
            @Override
            RestClient createRestClient() {
                return restClientMock;
            }
        };
    }

    @Test
    @DisplayName("getMovieData() – correct data download")
    void shouldReturnMovieDataWhenResponseIsOk() {
        // Given
        DigiKatMovieData expected = new DigiKatMovieData();
        when(restClientMock.get()
                .uri("/ranking?film={title}", "Inception")
                .retrieve()
                .onStatus(any(), any())
                .onStatus(any(), any())
                .body(DigiKatMovieData.class))
                .thenReturn(expected);

        // When
        DigiKatMovieData actual = client.getMovieData("Inception");

        // Then
        assertThat(actual).isSameAs(expected);
    }

    @Nested
    @DisplayName("Title validation")
    class ValidationTests {

        @Test
        @DisplayName("null as title throws IllegalArgumentException")
        void nullTitleThrows() {
            assertThatThrownBy(() -> client.getMovieData(null))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Movie title cannot be empty");
        }

        @Test
        @DisplayName("empty title throws IllegalArgumentException")
        void emptyTitleThrows() {
            assertThatThrownBy(() -> client.getMovieData("   "))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Movie title cannot be empty");
        }

        @Test
        @DisplayName("too long title throws IllegalArgumentException")
        void tooLongTitleThrows() {
            String longTitle = "a".repeat(301);
            assertThatThrownBy(() -> client.getMovieData(longTitle))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("The title of the video cannot exceed 300 characters.");
        }
    }

    @Nested
    @DisplayName("HTTP error handling")
    class HttpErrorTests {

        @Test
        @DisplayName("RestClientException throws DigiKatClientException")
        void restClientExceptionThrowsDigiKatClientException() {
            when(restClientMock.get()
                    .uri("/ranking?film={title}", "Toy Story")
                    .retrieve()
                    .onStatus(any(), any())
                    .onStatus(any(), any())
                    .body(DigiKatMovieData.class))
                    .thenThrow(new RestClientException("IO error"));

            assertThatThrownBy(() -> client.getMovieData("Toy Story"))
                    .isInstanceOf(DigiKatClientException.class)
                    .hasMessage("DigiKat API communication error");
        }
    }
}
