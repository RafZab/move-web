package pl.rafzab.movielibraryservice.service.movie;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.rafzab.movielibraryservice.client.DigiKatClient;
import pl.rafzab.movielibraryservice.client.DigiKatMovieData;
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

@Slf4j
@Service
@RequiredArgsConstructor
public class MovieService {
    private final MovieRepository movieRepository;
    private final FileStorageService fileStorageService;
    private final DigiKatClient digiKatClient;

    private final MovieProperties movieProperties;

    @Transactional(readOnly=true)
    public MovieListDTO findUserMovies(User user, int page, int limit, MovieFieldSort sortBy, Sort.Direction sortDirection){
        log.info("Starting movie search for user: {} with parameters page={}, limit={}, sortBy={}, sortDirection={}",
                user.getUserId(), page, limit, sortBy, sortDirection);

        var pageable = PageRequest.of(page, limit, Sort.by(sortDirection, sortBy.getValues()));
        var moviePage = movieRepository.findAllByUser(user, pageable);

        log.debug("Found movie page: currentPage={}, totalItems={}, totalPages={}",
                moviePage.getNumber(), moviePage.getTotalElements(), moviePage.getTotalPages());

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
        log.info("Starting to save a movie for user: {} with title: {}", user.getUserId(), requestData.title());

        var movie = Movie.builder()
                .user(user)
                .title(requestData.title())
                .director(requestData.director())
                .yearOfProduction(requestData.yearOfProduction())
                .size(requestData.file().getSize())
                .build();
        log.debug("Built movie object: {}", movie);

        var movieReadyToSave = uploadAndCalculateRanking(movie, requestData);
        log.debug("Movie after upload and ranking calculation: {}", movieReadyToSave);

        movieRepository.save(movieReadyToSave);
        log.info("Movie saved successfully for user: {} with title: {}", user.getUserId(), movieReadyToSave.getTitle());
    }

    @Transactional
    public void updateMovie(User user, Long movieId, MovieModificationDTO requestData){
        log.info("Starting update of movie with ID: {} for user: {}", movieId, user.getUserId());
        var movie = getMovieByIdAndUser(movieId, user);

        movie.setTitle(requestData.title());
        movie.setDirector(requestData.director());
        movie.setYearOfProduction(requestData.yearOfProduction());
        movie.setSize(requestData.file().getSize());
        log.debug("Movie details before recalculating ranking: {}", movie);

        uploadAndCalculateRanking(movie, requestData);
        log.debug("Movie after upload and ranking calculation: {}", movie);

        movieRepository.save(movie);
        log.info("Movie with ID: {} updated successfully for user: {}", movieId, user.getUserId());
    }

    @Transactional(readOnly=true)
    public Resource downloadMovie(User user, Long movieId){
        log.info("Starting download of movie with ID: {} for user: {}", movieId, user.getUserId());
        var movie = getMovieByIdAndUser(movieId, user);
        log.debug("Movie found for download: {}", movie);

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
        if(movie.getSize() < movieProperties.getSmallFileSize()){
            return 100;
        }

        DigiKatMovieData digiKatMovieData = digiKatClient.getMovieData(movie.getTitle());

        int ranking = 0;
        if (digiKatMovieData.isPolishProduction())
            ranking += 200;

        if (digiKatMovieData.isAvailableOnNetflix())
            ranking -= 50;

        if (digiKatMovieData.hasOutstandingUserRating())
            ranking += 100;

        return ranking;
    }

    private Movie getMovieByIdAndUser(Long movieId, User user){
        return movieRepository.findByMovieIdAndUser(movieId, user)
                .orElseThrow(() -> new NotFoundException("Movie not found"));
    }
}
