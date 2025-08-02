package pl.rafzab.movielibraryservice.validation;

import jakarta.validation.ConstraintValidatorContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

class MultipartMaxFileSizeValidatorTest {

    private MultipartMaxFileSizeValidator validator;
    private ConstraintValidatorContext context;

    @BeforeEach
    void setUp() {
        MaxFileSize annotation = new MaxFileSize() {
            @Override
            public long value() {
                return 1000L;
            }

            @Override
            public String message() {
                return "File too large";
            }

            @Override
            public Class<?>[] groups() {
                return new Class[0];
            }

            @Override
            public Class<? extends jakarta.validation.Payload>[] payload() {
                return new Class[0];
            }

            @Override
            public Class<? extends java.lang.annotation.Annotation> annotationType() {
                return MaxFileSize.class;
            }
        };
        validator = new MultipartMaxFileSizeValidator();
        validator.initialize(annotation);

        context = mock(ConstraintValidatorContext.class);
    }

    @Test
    void shouldReturnTrueWhenFileIsNull() {
        assertTrue(validator.isValid(null, context));
    }

    @Test
    void shouldReturnTrueWhenFileSizeEqualsLimit() {
        MockMultipartFile file = new MockMultipartFile(
                "file", "test.bin", "application/octet-stream", new byte[1000]
        );
        assertTrue(validator.isValid(file, context));
    }

    @Test
    void shouldReturnTrueWhenFileSizeBelowLimit() {
        MockMultipartFile file = new MockMultipartFile(
                "file", "small.txt", "text/plain", new byte[500]
        );
        assertTrue(validator.isValid(file, context));
    }

    @Test
    void shouldReturnFalseWhenFileSizeExceedsLimit() {
        MockMultipartFile file = new MockMultipartFile(
                "file", "big.mov", "video/mp4", new byte[1500]
        );
        assertFalse(validator.isValid(file, context));
    }
}
