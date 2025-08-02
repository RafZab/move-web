package pl.rafzab.movielibraryservice.client;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

@Data
@Validated
@Configuration
@ConfigurationProperties(prefix = "app.digikat")
public class DigiKatProperties {
    @NotBlank
    @Pattern(regexp = "^https://.*")
    private String baseUrl;

    @NotNull
    private int connectionTimeoutMs;

    @NotNull
    private int readTimeoutMs;

    @NotNull
    private int maxRetries;
}
