package pl.rafzab.movielibraryservice.service.movie;

import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.rafzab.movielibraryservice.dto.request.MovieModificationDTO;
import pl.rafzab.movielibraryservice.dto.response.MovieDTO;
import pl.rafzab.movielibraryservice.dto.response.MovieListDTO;
import pl.rafzab.movielibraryservice.entity.Movie;
import pl.rafzab.movielibraryservice.entity.User;
import pl.rafzab.movielibraryservice.enums.MovieFieldSort;
import pl.rafzab.movielibraryservice.exception.NotFoundException;
import pl.rafzab.movielibraryservice.repository.MovieRepository;
import pl.rafzab.movielibraryservice.service.file.FileStorageService;

import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MovieService {
    private final MovieRepository movieRepository;
    private final FileStorageService fileStorageService;

    @Transactional(readOnly=true)
    public MovieListDTO findUserMovies(User user, int page, int limit, MovieFieldSort sortBy, Sort.Direction sortDirection){
        var pageable = PageRequest.of(page, limit, Sort.by(sortDirection, sortBy.getValues()));
        var moviePage = movieRepository.findAllByUser(user, pageable);

        return MovieListDTO.builder()
                .currentPage(moviePage.getNumber())
                .limit(moviePage.getNumberOfElements())
                .totalItems(moviePage.getTotalElements())
                .totalPages(moviePage.getTotalPages())
                .movies(moviePage.getContent().stream().map(MovieDTO::new).collect(Collectors.toList()))
                .build();
    }

    @Transactional
    public void saveMovie(User user, MovieModificationDTO requestData){
        var movie = Movie.builder()
                .user(user)
                .title(requestData.title())
                .director(requestData.director())
                .yearOfProduction(requestData.yearOfProduction())
                .size(requestData.file().getSize())
                .build();

        var movieReadyToSave = uploadAndCalculateRanking(movie, requestData);
        movieRepository.save(movieReadyToSave);
    }

    @Transactional
    public void updateMovie(User user, Long movieId, MovieModificationDTO requestData){
        var movie = getMovieByIdAndUser(movieId, user);

        movie.setTitle(requestData.title());
        movie.setDirector(requestData.director());
        movie.setYearOfProduction(requestData.yearOfProduction());
        movie.setSize(requestData.file().getSize());
        uploadAndCalculateRanking(movie, requestData);

        movieRepository.save(movie);
    }

    @Transactional(readOnly=true)
    public Resource downloadMovie(User user, Long movieId){
        var movie = getMovieByIdAndUser(movieId, user);
        return fileStorageService.loadFileByAbsolutePath(movie.getFilePath());
    }


    private Movie uploadAndCalculateRanking(Movie movie, MovieModificationDTO requestData){
        var filePath = fileStorageService.trySaveFile(requestData.file());
        movie.setFilePath(filePath);

        var ranking = calculateRanking(movie);
        movie.setRanking(ranking);

        return movie;
    }

    private int calculateRanking(Movie movie) {
        if(movie.getSize() < 200){
            return 100;
        }


        return 0;
    }

    private Movie getMovieByIdAndUser(Long movieId, User user){
        return movieRepository.findByMovieIdAndUser(movieId, user)
                .orElseThrow(() -> new NotFoundException("Movie not found"));
    }
}
