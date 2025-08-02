package pl.rafzab.movielibraryservice.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.Objects;

@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "movies")
public class Movie {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "movie_id")
    private Long movieId;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id")
    private User user;

    @NotNull
    @Column
    private String title;

    @NotNull
    @Column
    private String director;

    @NotNull
    @Column(name = "year_of_production")
    private Integer yearOfProduction;

    @NotNull
    @Column
    private Integer ranking;

    @NotNull
    @Column
    private Long size;

    @NotNull
    @Column(name = "file_path")
    private String filePath;

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Movie movie = (Movie) o;
        return Objects.equals(movieId, movie.movieId) && Objects.equals(user, movie.user) && Objects.equals(title, movie.title) && Objects.equals(director, movie.director) && Objects.equals(yearOfProduction, movie.yearOfProduction) && Objects.equals(ranking, movie.ranking) && Objects.equals(size, movie.size) && Objects.equals(filePath, movie.filePath);
    }

    @Override
    public int hashCode() {
        return Objects.hash(movieId, user, title, director, yearOfProduction, ranking, size, filePath);
    }

    @Override
    public String toString() {
        return "Movie{" +
                "movieId=" + movieId +
                ", user=" + user +
                ", title='" + title + '\'' +
                ", director='" + director + '\'' +
                ", yearOfProduction=" + yearOfProduction +
                ", ranking=" + ranking +
                ", size=" + size +
                ", filePath='" + filePath + '\'' +
                '}';
    }
}
