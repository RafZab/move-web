package pl.rafzab.movielibraryservice.service.movie;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

@Data
@Validated
@Configuration
@ConfigurationProperties(prefix = "app.movie")
public class MovieProperties {
    @NotNull
    private Long smallFileSize;
}
