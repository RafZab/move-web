package pl.rafzab.movielibraryservice.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = FileTypeValidator.class)
@Target({ ElementType.FIELD, ElementType.PARAMETER, ElementType.RECORD_COMPONENT })
@Retention(RetentionPolicy.RUNTIME)
public @interface FileType {
    String message() default "Wrong file type";
    String type();
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
