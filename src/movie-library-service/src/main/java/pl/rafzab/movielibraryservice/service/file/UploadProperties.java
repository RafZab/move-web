package pl.rafzab.movielibraryservice.service.file;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

@Data
@Validated
@Configuration
@ConfigurationProperties(prefix = "app.upload")
public class UploadProperties {
    @NotBlank
    private String directory;
}
