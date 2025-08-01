package pl.rafzab.movielibraryservice.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.web.multipart.MultipartFile;

public class FileTypeValidator implements ConstraintValidator<FileType, MultipartFile> {
    private String expectedType;

    @Override
    public void initialize(FileType constraintAnnotation) {
        expectedType = constraintAnnotation.type();
    }

    @Override
    public boolean isValid(MultipartFile file, ConstraintValidatorContext context) {
        if (file == null || file.isEmpty()) return true;
        String contentType = file.getContentType();
        return contentType != null && contentType.startsWith(expectedType + "/");
    }
}