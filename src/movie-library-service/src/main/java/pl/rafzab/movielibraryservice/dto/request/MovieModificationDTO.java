package pl.rafzab.movielibraryservice.dto.request;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import org.springframework.web.multipart.MultipartFile;
import pl.rafzab.movielibraryservice.validation.FileType;
import pl.rafzab.movielibraryservice.validation.MaxFileSize;
import pl.rafzab.movielibraryservice.validation.YearMaxCurrent;

public record MovieModificationDTO(
        @NotNull @MaxFileSize(value = 1073741824L, message = "Max file size is 1GB") @FileType(type = "video") MultipartFile file,
        @NotNull @NotBlank @Size(max = 255) String title,
        @NotNull @NotBlank @Size(max = 255) String director,
        @NotNull @Positive @YearMaxCurrent Integer yearOfProduction
) {}
