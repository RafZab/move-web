package pl.rafzab.movielibraryservice.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.*;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.mock.web.MockMultipartFile;
import pl.rafzab.movielibraryservice.client.DigiKatClient;
import pl.rafzab.movielibraryservice.client.DigiKatMovieData;
import pl.rafzab.movielibraryservice.dto.request.MovieModificationDTO;
import pl.rafzab.movielibraryservice.dto.response.MovieListDTO;
import pl.rafzab.movielibraryservice.entity.Movie;
import pl.rafzab.movielibraryservice.entity.User;
import pl.rafzab.movielibraryservice.enums.MovieFieldSort;
import pl.rafzab.movielibraryservice.exception.NotFoundException;
import pl.rafzab.movielibraryservice.repository.MovieRepository;
import pl.rafzab.movielibraryservice.service.file.FileStorageService;
import pl.rafzab.movielibraryservice.service.movie.MovieProperties;
import pl.rafzab.movielibraryservice.service.movie.MovieService;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class MovieServiceTest {

    @Mock
    private MovieRepository movieRepository;

    @Mock
    private FileStorageService fileStorageService;

    @Mock
    private DigiKatClient digiKatClient;

    @Mock
    private MovieProperties movieProperties;

    @InjectMocks
    private MovieService movieService;

    private User user;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        user = new User();
        user.setUserId(1L);
        user.setEmail("testuser");
        user.setPassword("password");
    }

    @Test
    void findUserMovies_ShouldReturnPagedList() {
        // Given
        Movie m1 = Movie.builder().movieId(1L).user(user).title("A").director("D").yearOfProduction(2000).size(50L).build();
        Movie m2 = Movie.builder().movieId(2L).user(user).title("B").director("E").yearOfProduction(2001).size(60L).build();
        Page<Movie> page = new PageImpl<>(List.of(m1, m2),
                PageRequest.of(0, 2, Sort.by(Direction.ASC, "size")), 2);

        when(movieRepository.findAllByUser(eq(user), any(Pageable.class))).thenReturn(page);

        // When
        MovieListDTO result = movieService.findUserMovies(user, 0, 2, MovieFieldSort.SIZE, Direction.ASC);

        // Then
        assertEquals(0, result.getCurrentPage());
        assertEquals(2, result.getLimit());
        assertEquals(2, result.getTotalItems());
        assertEquals(1, result.getTotalPages());
        assertEquals(2, result.getMovies().size());
        assertEquals("A", result.getMovies().get(0).title());
        verify(movieRepository).findAllByUser(eq(user), any(Pageable.class));
    }

    @Test
    void saveMovie_ShouldUploadFileCalculateRankingAndSave() {
        MockMultipartFile file = new MockMultipartFile("file", "movie.mp4", "video/mp4", new byte[120]);
        MovieModificationDTO dto = new MovieModificationDTO(file, "T", "D", 2022);
        when(fileStorageService.trySaveFile(any())).thenReturn("/path/movie.mp4");
        when(movieProperties.getSmallFileSize()).thenReturn(200L);

        doAnswer(invocation -> {
            Movie saved = invocation.getArgument(0);
            assertEquals("/path/movie.mp4", saved.getFilePath());
            assertEquals(100, saved.getRanking());
            return null;
        }).when(movieRepository).save(any(Movie.class));

        // when
        movieService.saveMovie(user, dto);

        // Then
        verify(fileStorageService).trySaveFile(eq(file));
        verify(movieRepository).save(any(Movie.class));
        verifyNoInteractions(digiKatClient);
    }

    @Test
    void updateMovie_ShouldThrowNotFound_WhenMovieNotExists() {
        when(movieRepository.findByMovieIdAndUser(99L, user)).thenReturn(Optional.empty());

        NotFoundException ex = assertThrows(NotFoundException.class, () ->
                movieService.updateMovie(user, 99L, new MovieModificationDTO(null, "T", "D", 2022))
        );
        assertEquals("Movie not found", ex.getMessage());
        verify(movieRepository).findByMovieIdAndUser(99L, user);
    }

    @Test
    void updateMovie_ShouldModifyExistingAndSave() {
        // Given
        Movie existing = Movie.builder()
                .movieId(5L).user(user).title("Old").director("X").yearOfProduction(2000).size(500L).filePath("/old")
                .ranking(0).build();
        when(movieRepository.findByMovieIdAndUser(5L, user)).thenReturn(Optional.of(existing));

        MockMultipartFile file = new MockMultipartFile(
                "file", "new.mp4", "video/mp4", new byte[500]
        );
        MovieModificationDTO dto = new MovieModificationDTO(
                file, "New", "Y", 2023
        );

        when(fileStorageService.trySaveFile(any())).thenReturn("/new");
        when(movieProperties.getSmallFileSize()).thenReturn(100L);

        DigiKatMovieData digiData = new DigiKatMovieData("title", 1, List.of("netflix"), "wybitny", "");
        when(digiKatClient.getMovieData("New")).thenReturn(digiData);

        when(movieRepository.save(any(Movie.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        movieService.updateMovie(user, 5L, dto);

        // Then
        assertEquals("New", existing.getTitle());
        assertEquals("Y", existing.getDirector());
        assertEquals(2023, existing.getYearOfProduction());
        assertEquals(500L, existing.getSize());
        assertEquals("/new", existing.getFilePath());
        // ranking = 100 (size>100) +200 -50 +100 =250
        assertEquals(250, existing.getRanking());

        verify(digiKatClient).getMovieData("New");
        verify(movieRepository).save(existing);
    }


    @Test
    void downloadMovie_ShouldLoadResource() {
        Movie existing = Movie.builder().movieId(7L).user(user).filePath("/film.mp4").build();
        when(movieRepository.findByMovieIdAndUser(7L, user)).thenReturn(Optional.of(existing));
        Resource resource = mock(Resource.class);
        when(fileStorageService.loadFileByAbsolutePath("/film.mp4")).thenReturn(resource);

        Resource result = movieService.downloadMovie(user, 7L);

        assertSame(resource, result);
        verify(fileStorageService).loadFileByAbsolutePath("/film.mp4");
    }

}
