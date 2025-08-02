package pl.rafzab.movielibraryservice.dto.response;

import pl.rafzab.movielibraryservice.entity.Movie;

public record MovieDTO(Long movieId, String title, String director, Integer yearOfProduction, Integer ranking) {

    public MovieDTO(Movie movie){
        this(movie.getMovieId(), movie.getTitle(), movie.getDirector(), movie.getYearOfProduction(), movie.getRanking());
    }
}
