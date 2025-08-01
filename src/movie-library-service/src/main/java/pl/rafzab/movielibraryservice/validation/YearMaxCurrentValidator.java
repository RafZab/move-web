package pl.rafzab.movielibraryservice.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.Year;

public class YearMaxCurrentValidator implements ConstraintValidator<YearMaxCurrent, Integer> {

    @Override
    public boolean isValid(Integer value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }
        int currentYear = Year.now().getValue();
        return value <= currentYear;
    }
}
