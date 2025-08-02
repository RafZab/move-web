package pl.rafzab.movielibraryservice.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import pl.rafzab.movielibraryservice.configuration.security.CustomUserDetails;
import pl.rafzab.movielibraryservice.entity.User;
import pl.rafzab.movielibraryservice.exception.NotFoundException;
import pl.rafzab.movielibraryservice.repository.UserRepository;
import pl.rafzab.movielibraryservice.service.user.CustomUserDetailsService;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("CustomUserDetailsService Tests")
class CustomUserDetailsServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CustomUserDetailsService customUserDetailsService;

    private User createMockUser(String email, String password) {
        User user = new User();
        user.setEmail(email);
        user.setPassword(password);
        return user;
    }

    @Nested
    @DisplayName("loadUserByUsername Tests")
    class LoadUserByUsernameTests {

        @Test
        @DisplayName("Should return CustomUserDetails when user exists")
        void shouldReturnCustomUserDetailsWhenUserExists() {
            // Given
            String email = "test@example.com";
            User mockUser = createMockUser(email, "hashedPassword");

            when(userRepository.findByEmail(email))
                    .thenReturn(Optional.of(mockUser));

            // When
            UserDetails result = customUserDetailsService.loadUserByUsername(email);

            // Then
            assertThat(result).isNotNull();
            assertThat(result).isInstanceOf(CustomUserDetails.class);
            assertThat(result.getUsername()).isEqualTo(email);

            // Verify repository was called exactly once
            verify(userRepository, times(1)).findByEmail(email);
            verifyNoMoreInteractions(userRepository);
        }

        @Test
        @DisplayName("Should throw NotFoundException when user doesn't exist")
        void shouldThrowNotFoundExceptionWhenUserNotExists() {
            // Given
            String nonExistentEmail = "nonexistent@example.com";

            when(userRepository.findByEmail(nonExistentEmail))
                    .thenReturn(Optional.empty());

            // When & Then
            NotFoundException exception = assertThrows(
                    NotFoundException.class,
                    () -> customUserDetailsService.loadUserByUsername(nonExistentEmail)
            );

            assertThat(exception.getMessage()).isEqualTo("User not found");

            // Verify repository was called exactly once
            verify(userRepository, times(1)).findByEmail(nonExistentEmail);
            verifyNoMoreInteractions(userRepository);
        }

        @Test
        @DisplayName("It should handle null email")
        void shouldHandleNullEmail() {
            // Given
            String nullEmail = null;

            when(userRepository.findByEmail(null))
                    .thenReturn(Optional.empty());

            // When & Then
            NotFoundException exception = assertThrows(
                    NotFoundException.class,
                    () -> customUserDetailsService.loadUserByUsername(nullEmail)
            );

            assertThat(exception.getMessage()).isEqualTo("User not found");
            verify(userRepository, times(1)).findByEmail(null);
        }

        @Test
        @DisplayName("It should handle empty email")
        void shouldHandleEmptyEmail() {
            // Given
            String emptyEmail = "";

            when(userRepository.findByEmail(emptyEmail))
                    .thenReturn(Optional.empty());

            // When & Then
            NotFoundException exception = assertThrows(
                    NotFoundException.class,
                    () -> customUserDetailsService.loadUserByUsername(emptyEmail)
            );

            assertThat(exception.getMessage()).isEqualTo("User not found");
            verify(userRepository, times(1)).findByEmail(emptyEmail);
        }

        @Test
        @DisplayName("It should correctly pass the user to CustomUserDetails")
        void shouldCorrectlyPassUserToCustomUserDetails() {
            // Given
            String email = "user@test.com";
            User mockUser = createMockUser(email, "securePassword");
            mockUser.setUserId(123L);

            when(userRepository.findByEmail(email))
                    .thenReturn(Optional.of(mockUser));

            // When
            UserDetails result = customUserDetailsService.loadUserByUsername(email);

            // Then
            assertThat(result).isNotNull();
            CustomUserDetails customUserDetails = (CustomUserDetails) result;

            // Verify all user properties are correctly passed
            assertThat(customUserDetails.getUsername()).isEqualTo(email);
            // Można dodać więcej sprawdzeń w zależności od implementacji CustomUserDetails

            verify(userRepository, times(1)).findByEmail(email);
        }
    }

    @Nested
    @DisplayName("Edge Cases and Integration")
    class EdgeCasesTests {

        @Test
        @DisplayName("It should handle various email formats")
        void shouldHandleDifferentEmailFormats() {
            // Given
            List<String> emailFormats = Arrays.asList(
                    "test@example.com",
                    "user.name@domain.co.uk",
                    "admin+tag@company.org",
                    "123456@numbers.com"
            );

            emailFormats.forEach(email -> {
                User mockUser = createMockUser(email, "password");
                when(userRepository.findByEmail(email))
                        .thenReturn(Optional.of(mockUser));

                // When
                UserDetails result = customUserDetailsService.loadUserByUsername(email);

                // Then
                assertThat(result).isNotNull();
                assertThat(result.getUsername()).isEqualTo(email);
            });

            verify(userRepository, times(emailFormats.size())).findByEmail(anyString());
        }

        @Test
        @DisplayName("It should handle the exception from the repository")
        void shouldHandleRepositoryException() {
            // Given
            String email = "test@example.com";

            when(userRepository.findByEmail(email))
                    .thenThrow(new RuntimeException("Database connection error"));

            // When & Then
            RuntimeException exception = assertThrows(
                    RuntimeException.class,
                    () -> customUserDetailsService.loadUserByUsername(email)
            );

            assertThat(exception.getMessage()).isEqualTo("Database connection error");
            verify(userRepository, times(1)).findByEmail(email);
        }
    }

    @Nested
    @DisplayName("Parametrized Tests")
    class ParametrizedTests {

        @ParameterizedTest
        @DisplayName("Should properly handle various email scenarios")
        @CsvSource({
                "valid@email.com, true",
                "another@test.org, true",
                "nonexistent@fake.com, false",
                "'', false",
                "invalid-format, false"
        })
        void shouldHandleVariousEmailScenarios(String email, boolean userExists) {
            // Given
            if (userExists) {
                User mockUser = createMockUser(email, "password");
                when(userRepository.findByEmail(email))
                        .thenReturn(Optional.of(mockUser));
            } else {
                when(userRepository.findByEmail(email))
                        .thenReturn(Optional.empty());
            }

            // When & Then
            if (userExists) {
                UserDetails result = customUserDetailsService.loadUserByUsername(email);
                assertThat(result).isNotNull();
                assertThat(result.getUsername()).isEqualTo(email);
            } else {
                assertThrows(
                        NotFoundException.class,
                        () -> customUserDetailsService.loadUserByUsername(email)
                );
            }

            verify(userRepository, times(1)).findByEmail(email);
        }
    }
}
