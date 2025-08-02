package pl.rafzab.movielibraryservice.util;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import pl.rafzab.movielibraryservice.configuration.response.ApiData;
import pl.rafzab.movielibraryservice.configuration.response.ResponseMaker;

import static org.junit.jupiter.api.Assertions.*;

class ResponseMakerTest {

    @Test
    void ok_ShouldReturn200AndDto() {
        // given
        String payload = "testDto";

        // when
        ResponseEntity<ApiData<String>> response = ResponseMaker.ok(payload);

        // then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("OK", response.getBody().getMessage());
        assertEquals(payload, response.getBody().getData());
    }

    @Test
    void created_ShouldReturn201AndNoData() {
        // when
        ResponseEntity<ApiData<Void>> response = ResponseMaker.created();

        // then
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("CREATED", response.getBody().getMessage());
        assertNull(response.getBody().getData());
    }

    @Test
    void updated_ShouldReturn204AndNoData() {
        // when
        ResponseEntity<ApiData<Void>> response = ResponseMaker.updated();

        // then
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("UPDATED", response.getBody().getMessage());
        assertNull(response.getBody().getData());
    }
}

