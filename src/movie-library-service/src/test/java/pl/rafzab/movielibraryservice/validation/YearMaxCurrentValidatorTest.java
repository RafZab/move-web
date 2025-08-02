package pl.rafzab.movielibraryservice.validation;

import jakarta.validation.ConstraintValidatorContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Year;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class YearMaxCurrentValidatorTest {

    private YearMaxCurrentValidator validator;
    private ConstraintValidatorContext context;
    private int currentYear;

    @BeforeEach
    void setUp() {
        validator = new YearMaxCurrentValidator();
        context = org.mockito.Mockito.mock(ConstraintValidatorContext.class);
        currentYear = Year.now().getValue();
    }

    @Test
    void shouldReturnTrueWhenValueIsNull() {
        assertTrue(validator.isValid(null, context));
    }

    @Test
    void shouldReturnTrueWhenValueBelowCurrentYear() {
        assertTrue(validator.isValid(currentYear - 1, context));
    }

    @Test
    void shouldReturnTrueWhenValueEqualsCurrentYear() {
        assertTrue(validator.isValid(currentYear, context));
    }

    @Test
    void shouldReturnFalseWhenValueAboveCurrentYear() {
        assertFalse(validator.isValid(currentYear + 1, context));
    }
}

