package pl.rafzab.movielibraryservice.validation;

import jakarta.validation.ConstraintValidatorContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

class FileTypeValidatorTest {

    private FileTypeValidator validator;
    private ConstraintValidatorContext context;

    @BeforeEach
    void setUp() {
        FileType annotation = new FileType() {
            @Override
            public String type() {
                return "video";
            }

            @Override
            public String message() {
                return "Wrong file type";
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
                return FileType.class;
            }
        };
        validator = new FileTypeValidator();
        validator.initialize(annotation);

        context = mock(ConstraintValidatorContext.class);
    }

    @Test
    void shouldReturnTrueWhenFileIsNull() {
        assertTrue(validator.isValid(null, context));
    }

    @Test
    void shouldReturnTrueWhenFileIsEmpty() {
        MockMultipartFile file = new MockMultipartFile("file", new byte[0]);
        assertTrue(validator.isValid(file, context));
    }

    @Test
    void shouldReturnTrueWhenContentTypeMatches() {
        MockMultipartFile file = new MockMultipartFile(
                "file", "movie.mp4", "video/mp4", new byte[]{1, 2, 3}
        );
        assertTrue(validator.isValid(file, context));
    }

    @Test
    void shouldReturnFalseWhenContentTypeDoesNotMatch() {
        MockMultipartFile file = new MockMultipartFile(
                "file", "image.png", "image/png", new byte[]{1, 2, 3}
        );
        assertFalse(validator.isValid(file, context));
    }

    @Test
    void shouldReturnFalseWhenContentTypeIsNull() {
        MockMultipartFile file = spy(new MockMultipartFile("file", "unknown", null, new byte[]{1}));
        when(file.getContentType()).thenReturn(null);
        assertFalse(validator.isValid(file, context));
    }
}
