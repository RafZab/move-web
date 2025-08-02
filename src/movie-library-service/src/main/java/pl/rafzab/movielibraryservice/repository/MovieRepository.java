package pl.rafzab.movielibraryservice.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.rafzab.movielibraryservice.entity.Movie;
import pl.rafzab.movielibraryservice.entity.User;

import java.util.Optional;

@Repository
public interface MovieRepository extends JpaRepository<Movie, Long> {
    Page<Movie> findAllByUser(User user, Pageable pageable);
    Optional<Movie> findByMovieIdAndUser(Long movieId, User user);
}

