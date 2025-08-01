package pl.rafzab.movielibraryservice.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = YearMaxCurrentValidator.class)
@Target({ ElementType.FIELD, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
public @interface YearMaxCurrent {
    String message() default "The year cannot be greater than the current year";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}