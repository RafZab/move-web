package pl.rafzab.movielibraryservice.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.Resource;
import org.springframework.mock.web.MockMultipartFile;
import pl.rafzab.movielibraryservice.exception.NotFoundException;
import pl.rafzab.movielibraryservice.service.file.FileStorageService;
import pl.rafzab.movielibraryservice.service.file.UploadProperties;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("FileStorageService Tests")
class FileStorageServiceTest {

    @TempDir
    Path tempDir;
    @Mock
    private UploadProperties uploadProperties;
    @InjectMocks
    private FileStorageService fileStorageService;

    @BeforeEach
    void setUp() {
        when(uploadProperties.getDirectory()).thenReturn(tempDir.toString());
    }

    @Nested
    @DisplayName("trySaveFile Tests")
    class TrySaveFileTests {

        @Test
        @DisplayName("It should save the file successfully")
        void shouldSuccessfullySaveFile() throws IOException {
            // Given
            MockMultipartFile mockFile = new MockMultipartFile(
                    "file",
                    "test-movie.mp4",
                    "video/mp4",
                    "test movie content".getBytes()
            );

            // When
            String result = fileStorageService.trySaveFile(mockFile);

            // Then
            assertThat(result).isNotNull();
            assertThat(result).contains("test-movie.mp4");
            assertThat(result).contains(tempDir.toString());

            // Verify file was actually created
            Path savedFilePath = Paths.get(result);
            assertThat(Files.exists(savedFilePath)).isTrue();
            assertThat(Files.readAllBytes(savedFilePath))
                    .isEqualTo("test movie content".getBytes());
        }

        @Test
        @DisplayName("It should create the directory if it doesn't exist")
        void shouldCreateDirectoryIfNotExists() {
            // Given
            Path nonExistentDir = tempDir.resolve("uploads/movies");
            when(uploadProperties.getDirectory()).thenReturn(nonExistentDir.toString());

            MockMultipartFile mockFile = new MockMultipartFile(
                    "file",
                    "new-movie.mp4",
                    "video/mp4",
                    "content".getBytes()
            );

            // When
            String result = fileStorageService.trySaveFile(mockFile);

            // Then
            assertThat(result).isNotNull();
            assertThat(Files.exists(nonExistentDir)).isTrue();
            assertThat(Files.exists(Paths.get(result))).isTrue();
        }

        @Test
        @DisplayName("It should generate a unique filename with timestamp")
        void shouldGenerateUniqueFilenameWithTimestamp() throws InterruptedException {
            // Given
            MockMultipartFile mockFile1 = new MockMultipartFile(
                    "file", "movie.mp4", "video/mp4", "content1".getBytes()
            );
            MockMultipartFile mockFile2 = new MockMultipartFile(
                    "file", "movie.mp4", "video/mp4", "content2".getBytes()
            );

            // When
            String result1 = fileStorageService.trySaveFile(mockFile1);
            Thread.sleep(10); // Ensure different timestamp
            String result2 = fileStorageService.trySaveFile(mockFile2);

            // Then
            assertThat(result1).isNotEqualTo(result2);
            assertThat(result1).contains("movie.mp4");
            assertThat(result2).contains("movie.mp4");

            // Both files should exist
            assertThat(Files.exists(Paths.get(result1))).isTrue();
            assertThat(Files.exists(Paths.get(result2))).isTrue();
        }

        @Test
        @DisplayName("It should handle files with Polish characters in their names")
        void shouldHandleFileWithPolishCharacters() {
            // Given
            MockMultipartFile mockFile = new MockMultipartFile(
                    "file",
                    "film-żółć.mp4",
                    "video/mp4",
                    "polish content".getBytes()
            );

            // When
            String result = fileStorageService.trySaveFile(mockFile);

            // Then
            assertThat(result).isNotNull();
            assertThat(result).contains("film-żółć.mp4");
            assertThat(Files.exists(Paths.get(result))).isTrue();
        }

        @Test
        @DisplayName("It should handle different file types")
        void shouldHandleDifferentFileTypes() {
            // Given
            List<MockMultipartFile> testFiles = Arrays.asList(
                    new MockMultipartFile("file", "movie.mp4", "video/mp4", "mp4 content".getBytes()),
                    new MockMultipartFile("file", "movie.avi", "video/avi", "avi content".getBytes()),
                    new MockMultipartFile("file", "movie.mkv", "video/mkv", "mkv content".getBytes())
            );

            // When & Then
            testFiles.forEach(file -> {
                String result = fileStorageService.trySaveFile(file);
                assertThat(result).isNotNull();
                assertThat(result).contains(file.getOriginalFilename());
                assertThat(Files.exists(Paths.get(result))).isTrue();
            });
        }

        @Test
        @DisplayName("It should handle the file without extension")
        void shouldHandleFileWithoutExtension() {
            // Given
            MockMultipartFile mockFile = new MockMultipartFile(
                    "file",
                    "filename_without_extension",
                    "application/octet-stream",
                    "content".getBytes()
            );

            // When
            String result = fileStorageService.trySaveFile(mockFile);

            // Then
            assertThat(result).isNotNull();
            assertThat(result).contains("filename_without_extension");
            assertThat(Files.exists(Paths.get(result))).isTrue();
        }

        @Test
        @DisplayName("It should handle an empty file")
        void shouldHandleEmptyFile() throws IOException {
            // Given
            MockMultipartFile mockFile = new MockMultipartFile(
                    "file",
                    "empty.mp4",
                    "video/mp4",
                    new byte[0]
            );

            // When
            String result = fileStorageService.trySaveFile(mockFile);

            // Then
            assertThat(result).isNotNull();
            Path savedPath = Paths.get(result);
            assertThat(Files.exists(savedPath)).isTrue();
            assertThat(Files.size(savedPath)).isEqualTo(0);
        }

        @Test
        @DisplayName("Should handle very large file (according to NFR1)")
        void shouldHandleLargeFile() throws IOException {
            // Given
            int sizeInMB = 50;
            byte[] largeContent = new byte[sizeInMB * 1024 * 1024];
            Arrays.fill(largeContent, (byte) 'A');

            MockMultipartFile mockFile = new MockMultipartFile(
                    "file",
                    "large-movie.mp4",
                    "video/mp4",
                    largeContent
            );

            // When
            String result = fileStorageService.trySaveFile(mockFile);

            // Then
            assertThat(result).isNotNull();
            Path savedPath = Paths.get(result);
            assertThat(Files.exists(savedPath)).isTrue();
            assertThat(Files.size(savedPath)).isEqualTo(largeContent.length);
        }
    }

    @Nested
    @DisplayName("loadFileByAbsolutePath Tests")
    class LoadFileByAbsolutePathTests {

        @BeforeEach
        void setUp() {
            lenient().when(uploadProperties.getDirectory()).thenReturn(tempDir.toString());
        }

        @Test
        @DisplayName("It should load an existing file")
        void shouldLoadExistingFile() throws IOException {
            // Given
            Path testFile = tempDir.resolve("existing-movie.mp4");
            Files.write(testFile, "test content".getBytes());
            String absolutePath = testFile.toAbsolutePath().toString();

            // When
            Resource result = fileStorageService.loadFileByAbsolutePath(absolutePath);

            // Then
            assertThat(result).isNotNull();
            assertThat(result.exists()).isTrue();
            assertThat(result.isReadable()).isTrue();
            assertThat(result.getFilename()).isEqualTo("existing-movie.mp4");

            // Verify content
            try (InputStream inputStream = result.getInputStream()) {
                String content = new String(inputStream.readAllBytes());
                assertThat(content).isEqualTo("test content");
            }
        }

        @Test
        @DisplayName("Should throw NotFoundException for non-existent file")
        void shouldThrowNotFoundExceptionForNonExistentFile() {
            // Given
            String nonExistentPath = tempDir.resolve("non-existent.mp4").toAbsolutePath().toString();

            // When & Then
            NotFoundException exception = assertThrows(
                    NotFoundException.class,
                    () -> fileStorageService.loadFileByAbsolutePath(nonExistentPath)
            );

            assertThat(exception.getMessage())
                    .isEqualTo("File not found: " + nonExistentPath);
        }

        @Test
        @DisplayName("It should handle a path with Polish characters")
        void shouldHandlePathWithPolishCharacters() throws IOException {
            // Given
            Path testFile = tempDir.resolve("film-żółć.mp4");
            Files.write(testFile, "polish content".getBytes());
            String absolutePath = testFile.toAbsolutePath().toString();

            // When
            Resource result = fileStorageService.loadFileByAbsolutePath(absolutePath);

            // Then
            assertThat(result).isNotNull();
            assertThat(result.exists()).isTrue();
            assertThat(result.getFilename()).isEqualTo("film-żółć.mp4");
        }

        @Test
        @DisplayName("It should handle different track formats")
        void shouldHandleDifferentPathFormats() throws IOException {
            // Given
            Path testFile = tempDir.resolve("path-test.mp4");
            Files.write(testFile, "content".getBytes());

            List<String> pathVariants = Arrays.asList(
                    testFile.toAbsolutePath().toString(),
                    testFile.toAbsolutePath().normalize().toString()
            );

            // When & Then
            pathVariants.forEach(path -> {
                Resource result = fileStorageService.loadFileByAbsolutePath(path);
                assertThat(result).isNotNull();
                assertThat(result.exists()).isTrue();
            });
        }

        @Test
        @DisplayName("It should handle an empty file")
        void shouldHandleEmptyFileForLoading() throws IOException {
            // Given
            Path emptyFile = tempDir.resolve("empty.mp4");
            Files.createFile(emptyFile);
            String absolutePath = emptyFile.toAbsolutePath().toString();

            // When
            Resource result = fileStorageService.loadFileByAbsolutePath(absolutePath);

            // Then
            assertThat(result).isNotNull();
            assertThat(result.exists()).isTrue();
            assertThat(result.contentLength()).isEqualTo(0);
        }
    }

    @Nested
    @DisplayName("Integration Tests")
    class IntegrationTests {

        @Test
        @DisplayName("It should save and load the file in a complete cycle")
        void shouldSaveAndLoadFileInFullCycle() throws IOException {
            // Given
            MockMultipartFile originalFile = new MockMultipartFile(
                    "file",
                    "integration-test.mp4",
                    "video/mp4",
                    "integration test content".getBytes()
            );

            // When - Save file
            String savedPath = fileStorageService.trySaveFile(originalFile);

            // And - Load file
            Resource loadedFile = fileStorageService.loadFileByAbsolutePath(savedPath);

            // Then
            assertThat(loadedFile).isNotNull();
            assertThat(loadedFile.exists()).isTrue();
            assertThat(loadedFile.getFilename()).contains("integration-test.mp4");

            try (InputStream inputStream = loadedFile.getInputStream()) {
                String content = new String(inputStream.readAllBytes());
                assertThat(content).isEqualTo("integration test content");
            }
        }

        @Test
        @DisplayName("It should handle multiple files at the same time")
        void shouldHandleMultipleFilesSimultaneously() throws IOException {
            // Given
            List<MockMultipartFile> files = IntStream.range(0, 5)
                    .mapToObj(i -> new MockMultipartFile(
                            "file",
                            "movie-" + i + ".mp4",
                            "video/mp4",
                            ("content-" + i).getBytes()
                    ))
                    .toList();

            // When
            List<String> savedPaths = files.stream()
                    .map(fileStorageService::trySaveFile)
                    .toList();

            // Then
            assertThat(savedPaths.stream().distinct().count()).isEqualTo(5); // All unique

            savedPaths.forEach(path -> {
                Resource resource = fileStorageService.loadFileByAbsolutePath(path);
                assertThat(resource.exists()).isTrue();
            });
        }
    }

    @Nested
    @DisplayName("Edge Cases and Error Handling")
    class EdgeCasesTests {

        @Test
        @DisplayName("Should handle null originalFilename")
        void shouldHandleNullOriginalFilename() throws IOException {
            // Given
            MockMultipartFile mockFile = mock(MockMultipartFile.class);
            when(mockFile.getOriginalFilename()).thenReturn(null);
            doNothing().when(mockFile).transferTo(any(Path.class));

            // When
            String result = fileStorageService.trySaveFile(mockFile);

            // Then
            assertThat(result).isNotNull();
            assertThat(result).contains("null"); // timestamp + "_" + null
        }

        @Test
        @DisplayName("Should handle directory creation error")
        void shouldHandleDirectoryCreationError() {
            // Given
            String invalidPath = tempDir.toString() + File.separator + "invalid:path";
            when(uploadProperties.getDirectory()).thenReturn(invalidPath);

            MockMultipartFile mockFile = new MockMultipartFile(
                    "file", "test.mp4", "video/mp4", "content".getBytes()
            );

            // When & Then
            RuntimeException exception = assertThrows(
                    RuntimeException.class,
                    () -> fileStorageService.trySaveFile(mockFile)
            );

            Throwable cause = exception.getCause();
            assertThat(cause).satisfiesAnyOf(
                    c -> assertThat(c).isInstanceOf(IOException.class),
                    c -> assertThat(c).isInstanceOf(InvalidPathException.class)
            );
        }

        @Test
        @DisplayName("Should handle very long filename")
        void shouldHandleVeryLongFilename() {
            // Given
            String longFilename = "a".repeat(200) + ".mp4";
            MockMultipartFile mockFile = new MockMultipartFile(
                    "file",
                    longFilename,
                    "video/mp4",
                    "content".getBytes()
            );

            // When
            String result = fileStorageService.trySaveFile(mockFile);

            // Then
            assertThat(result).isNotNull();
            assertThat(Files.exists(Paths.get(result))).isTrue();
        }
    }
}
