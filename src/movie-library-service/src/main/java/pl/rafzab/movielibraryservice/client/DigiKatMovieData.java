package pl.rafzab.movielibraryservice.client;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DigiKatMovieData {
    @JsonProperty("tytul")
    private String title;

    @JsonProperty("produkcja")
    private Integer production;

    @JsonProperty("dostepnosc")
    private List<String> availability;

    @JsonProperty("ocenaUzytkwonikow")
    private String rating;

    @JsonProperty("ostaniaAktualizacja")
    private String lastUpdated;

    public boolean isPolishProduction() {
        return production != null && (production == 0 || production == 1);
    }

    public boolean isAvailableOnNetflix() {
        return availability != null && availability.contains("netflix");
    }

    public boolean hasOutstandingUserRating() {
        return "wybitny".equals(rating);
    }
}
