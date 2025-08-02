package pl.rafzab.movielibraryservice.service.file;

import lombok.RequiredArgsConstructor;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import pl.rafzab.movielibraryservice.exception.NotFoundException;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
@RequiredArgsConstructor
public class FileStorageService {

    private final UploadProperties uploadProperties;

    public String trySaveFile(MultipartFile file){
        try {
            return saveFile(file);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private String saveFile(MultipartFile file) throws IOException {
        Path dirPath = Paths.get(uploadProperties.getDirectory());
        if (Files.notExists(dirPath)) {
            Files.createDirectories(dirPath);
        }

        String originalFilename = file.getOriginalFilename();
        String savedFileName = System.currentTimeMillis() + "_" + originalFilename;

        Path filePath = dirPath.resolve(savedFileName);
        file.transferTo(filePath);
        return filePath.toAbsolutePath().toString();
    }

    public Resource loadFileByAbsolutePath(String absolutePath) {
        File file = new File(absolutePath);
        if (!file.exists()) {
            throw new NotFoundException("File not found: " + absolutePath);
        }
        return new FileSystemResource(file);
    }
}
