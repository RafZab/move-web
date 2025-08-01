package pl.rafzab.movielibraryservice.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.web.multipart.MultipartFile;

public class MultipartMaxFileSizeValidator implements ConstraintValidator<MaxFileSize, MultipartFile> {
    private long maxSize;

    public void initialize(MaxFileSize constraint) {
        this.maxSize = constraint.value();
    }
    public boolean isValid(MultipartFile file, ConstraintValidatorContext ctx) {
        if(file == null) return true;
        return file.getSize() <= maxSize;
    }
}
