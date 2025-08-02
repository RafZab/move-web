package pl.rafzab.movielibraryservice.controller;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Sort;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import pl.rafzab.movielibraryservice.configuration.security.SecurityUtils;
import pl.rafzab.movielibraryservice.dto.request.MovieModificationDTO;
import pl.rafzab.movielibraryservice.dto.response.MovieDTO;
import pl.rafzab.movielibraryservice.dto.response.MovieListDTO;
import pl.rafzab.movielibraryservice.entity.User;
import pl.rafzab.movielibraryservice.enums.MovieFieldSort;
import pl.rafzab.movielibraryservice.service.movie.MovieService;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class MovieControllerTest {

    private static MockedStatic<SecurityUtils> securityUtils;
    @Mock
    private MovieService movieService;
    @InjectMocks
    private MovieController movieController;
    private MockMvc mockMvc;
    private User dummyUser;

    @BeforeAll
    static void initStatic() {
        securityUtils = Mockito.mockStatic(SecurityUtils.class);
    }

    @AfterAll
    static void closeStatic() {
        securityUtils.close();
    }

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(movieController).build();
        dummyUser = new User();
        dummyUser.setUserId(42L);
        dummyUser.setEmail("test");
        dummyUser.setPassword("test");
        securityUtils.when(SecurityUtils::getCurrentUser).thenReturn(dummyUser);
    }

    @Test
    void findUserMovies_ShouldReturnOkWithData() throws Exception {
        MovieListDTO listDto = MovieListDTO.builder()
                .currentPage(1).limit(2).totalItems(10L).totalPages(5)
                .movies(List.of(new MovieDTO(1L, "t", "d", 2000, 100)))
                .build();
        when(movieService.findUserMovies(dummyUser, 0, 5, MovieFieldSort.SIZE, Sort.Direction.DESC))
                .thenReturn(listDto);

        mockMvc.perform(get("/api/v1/movies")
                        .param("page", "0")
                        .param("limit", "5")
                        .param("sort", "SIZE")
                        .param("direction", "DESC"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.currentPage").value(1))
                .andExpect(jsonPath("$.data.limit").value(2))
                .andExpect(jsonPath("$.data.totalItems").value(10))
                .andExpect(jsonPath("$.data.totalPages").value(5));

        verify(movieService).findUserMovies(dummyUser, 0, 5, MovieFieldSort.SIZE, Sort.Direction.DESC);
    }

    @Test
    void saveMovie_ShouldReturnCreated() throws Exception {
        byte[] content = "dummy".getBytes();
        MockMultipartFile filePart = new MockMultipartFile(
                "file",
                "movie.mp4",
                "video/mp4",
                content
        );

        mockMvc.perform(multipart("/api/v1/movies")
                        .file(filePart)
                        .param("title", "T")
                        .param("director", "D")
                        .param("yearOfProduction", "2022")
                )
                .andExpect(status().isCreated());

        verify(movieService).saveMovie(eq(dummyUser), any(MovieModificationDTO.class));
    }

    @Test
    void updateMovie_ShouldReturnUpdated() throws Exception {
        byte[] content = "updated".getBytes();
        MockMultipartFile filePart = new MockMultipartFile(
                "file",
                "new.mp4",
                "video/mp4",
                content
        );

        mockMvc.perform(multipart("/api/v1/movies/99")
                        .file(filePart)
                        .with(request -> {
                            request.setMethod("PUT");
                            return request;
                        })
                        .param("title", "N")
                        .param("director", "Y")
                        .param("yearOfProduction", "2023")
                )
                .andExpect(status().isNoContent());

        verify(movieService).updateMovie(eq(dummyUser), eq(99L), any(MovieModificationDTO.class));
    }

    @Test
    void downloadMovie_ShouldReturnFileResource() throws Exception {
        byte[] data = "dummy".getBytes();
        Resource resource = new ByteArrayResource(data) {
            @Override
            public String getFilename() {
                return "file.mp4";
            }
        };
        when(movieService.downloadMovie(dummyUser, 7L)).thenReturn(resource);

        mockMvc.perform(get("/api/v1/movies/7/download"))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Disposition", "attachment; filename=\"file.mp4\""))
                .andExpect(content().bytes(data));

        verify(movieService).downloadMovie(dummyUser, 7L);
    }
}