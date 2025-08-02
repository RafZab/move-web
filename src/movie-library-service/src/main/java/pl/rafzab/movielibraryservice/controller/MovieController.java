package pl.rafzab.movielibraryservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
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

    @Tag(name = "Movies", description = "Operations related to user movies")
    @Operation(
            summary = "Get user movies",
            description = "Returns a list of movies associated with the logged-in user with pagination and sorting",
            responses = {
                    @ApiResponse(responseCode = "200", description = "The list of movies was returned successfully"),
                    @ApiResponse(responseCode = "401", description = "User is unauthorized")
            }
    )
    @GetMapping
    public ResponseEntity<ApiData<MovieListDTO>> findUserMovies(
            @Parameter(
                    in = ParameterIn.QUERY,
                    description = "Page number for pagination (0-indexed), default is 0",
                    schema = @Schema(type = "integer", defaultValue = "0")
            )
            @RequestParam(defaultValue = "0") int page,

            @Parameter(
                    in = ParameterIn.QUERY,
                    description = "Number of items per page, default is 20",
                    schema = @Schema(type = "integer", defaultValue = "20")
            )
            @RequestParam(defaultValue = "20") int limit,

            @Parameter(
                    in = ParameterIn.QUERY,
                    description = "Field to sort by. Possible values: ALL, RANKING, SIZE",
                    schema = @Schema(implementation = MovieFieldSort.class, defaultValue = "ALL")
            )
            @RequestParam(value = "sort", defaultValue = "ALL") MovieFieldSort sortBy,

            @Parameter(
                    in = ParameterIn.QUERY,
                    description = "Sort direction: ASC (ascending) or DESC (descending), default is DESC",
                    schema = @Schema(implementation = Sort.Direction.class, defaultValue = "DESC")
            )
            @RequestParam(value = "direction", defaultValue = "DESC") Sort.Direction sortDirection){
        var user = SecurityUtils.getCurrentUser();
        var responseData = movieService.findUserMovies(user, page, limit, sortBy, sortDirection);
        return ResponseMaker.ok(responseData);
    }

    @Tag(name = "Movies", description = "Operations related to user movies")
    @Operation(
            summary = "Save a new movie",
            description = "Saves a new movie for the logged-in user",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Movie created successfully"),
                    @ApiResponse(responseCode = "400", description = "Invalid input data"),
                    @ApiResponse(responseCode = "401", description = "User is unauthorized")
            }
    )
    @PostMapping
    public ResponseEntity<ApiData<Void>> saveMovie(@Valid MovieModificationDTO requestData){
        var user = SecurityUtils.getCurrentUser();
        movieService.saveMovie(user, requestData);
        return ResponseMaker.created();
    }

    @Tag(name = "Movies", description = "Operations related to user movies")
    @Operation(
            summary = "Update an existing movie",
            description = "Updates the details of an existing movie for the logged-in user",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Movie updated successfully"),
                    @ApiResponse(responseCode = "400", description = "Invalid input data"),
                    @ApiResponse(responseCode = "401", description = "User is unauthorized"),
                    @ApiResponse(responseCode = "404", description = "Movie not found")
            }
    )
    @PutMapping("/{movieId}")
    public ResponseEntity<ApiData<Void>> updateMovie(@PathVariable Long movieId, @Valid MovieModificationDTO requestData){
        var user = SecurityUtils.getCurrentUser();
        movieService.updateMovie(user, movieId, requestData);
        return ResponseMaker.updated();
    }

    @Tag(name = "Movies", description = "Operations related to user movies")
    @Operation(
            summary = "Download a movie file",
            description = "Allows the logged-in user to download the file of a specific movie by its ID",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Movie file downloaded successfully",
                            content = @Content(
                                    mediaType = "application/octet-stream",
                                    schema = @Schema(type = "string", format = "binary")
                            )
                    ),
                    @ApiResponse(responseCode = "401", description = "User is unauthorized"),
                    @ApiResponse(responseCode = "404", description = "Movie file not found")
            }
    )
    @GetMapping("/{movieId}/download")
    public ResponseEntity<Resource> downloadMovie(@PathVariable Long movieId){
        var user = SecurityUtils.getCurrentUser();
        Resource file = movieService.downloadMovie(user, movieId);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getFilename() + "\"")
                .body(file);
    }
}
