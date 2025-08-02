package pl.rafzab.movielibraryservice.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.rafzab.movielibraryservice.configuration.response.ApiData;
import pl.rafzab.movielibraryservice.configuration.response.ResponseMaker;
import pl.rafzab.movielibraryservice.configuration.security.SecurityUtils;
import pl.rafzab.movielibraryservice.dto.request.MovieModificationDTO;
import pl.rafzab.movielibraryservice.dto.response.MovieListDTO;
import pl.rafzab.movielibraryservice.enums.MovieFieldSort;
import pl.rafzab.movielibraryservice.service.movie.MovieService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/movies")
public class MovieController {

    private final MovieService movieService;

    @GetMapping
    public ResponseEntity<ApiData<MovieListDTO>> findUserMovies(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int limit,
            @RequestParam(value = "sort", defaultValue = "ALL") MovieFieldSort sortBy,
            @RequestParam(value = "direction", defaultValue = "DESC") Sort.Direction sortDirection){
        var user = SecurityUtils.getCurrentUser();
        var responseData = movieService.findUserMovies(user, page, limit, sortBy, sortDirection);
        return ResponseMaker.ok(responseData);
    }

    @PostMapping
    public ResponseEntity<ApiData<Void>> saveMovie(@Valid MovieModificationDTO requestData){
        var user = SecurityUtils.getCurrentUser();
        movieService.saveMovie(user, requestData);
        return ResponseMaker.created();
    }

    @PutMapping("/{movieId}")
    public ResponseEntity<ApiData<Void>> updateMovie(@PathVariable Long movieId, @Valid MovieModificationDTO requestData){
        var user = SecurityUtils.getCurrentUser();
        movieService.updateMovie(user, movieId, requestData);
        return ResponseMaker.updated();
    }

    @GetMapping("/{movieId}/download")
    public ResponseEntity<Resource> downloadMovie(@PathVariable Long movieId){
        var user = SecurityUtils.getCurrentUser();
        Resource file = movieService.downloadMovie(user, movieId);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getFilename() + "\"")
                .body(file);
    }
}
