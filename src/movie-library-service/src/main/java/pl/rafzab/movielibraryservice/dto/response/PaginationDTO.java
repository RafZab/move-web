package pl.rafzab.movielibraryservice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public abstract class PaginationDTO {
    private Integer currentPage;
    private Integer limit;
    private Long totalItems;
    private Integer totalPages;
}
