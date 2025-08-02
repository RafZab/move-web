package pl.rafzab.movielibraryservice.dto.response;

import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class MovieListDTO extends PaginationDTO {
    private List<MovieDTO> movies;
}
