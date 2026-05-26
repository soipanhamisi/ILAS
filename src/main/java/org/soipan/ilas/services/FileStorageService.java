package org.soipan.ilas.services;

import org.soipan.ilas.config.FileStorageProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;

@Service
public class FileStorageService {

    private final Path fileStorageLocation;

    @Autowired
    public FileStorageService(FileStorageProperties fileStorageProperties) {
        this.fileStorageLocation = Paths.get(fileStorageProperties.getUploadDir())
                .toAbsolutePath().normalize();

        try {
            Files.createDirectories(this.fileStorageLocation);
        } catch (Exception ex) {
            throw new RuntimeException("Could not create the directory where the uploaded files will be stored.", ex);
        }
    }

    /**
     * Store a CSV file and return the file path
     */
    public String storeFile(MultipartFile file, String prefix) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("File cannot be empty");
        }

        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || !originalFilename.endsWith(".csv")) {
            throw new IllegalArgumentException("Only CSV files are allowed");
        }

        // Create unique filename
        String fileName = prefix + "_" + UUID.randomUUID() + "_" + sanitizeFilename(originalFilename);

        try {
            Path targetLocation = this.fileStorageLocation.resolve(fileName);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
            return targetLocation.toString();
        } catch (IOException ex) {
            throw new RuntimeException("Could not store file " + fileName, ex);
        }
    }

    /**
     * Read all lines from a file
     */
    public List<String> readAllLines(String fileReference) {
        if (fileReference == null || fileReference.isBlank()) {
            throw new IllegalArgumentException("File reference cannot be empty");
        }

        try {
            Path path = Paths.get(fileReference);
            if (!Files.exists(path)) {
                throw new IllegalArgumentException("Exam question file was not found");
            }
            return Files.readAllLines(path);
        } catch (IOException ex) {
            throw new RuntimeException("Unable to read exam questions", ex);
        }
    }

    /**
     * Get the file storage location path
     */
    public Path getFileStorageLocation() {
        return fileStorageLocation;
    }

    /**
     * Delete a file
     */
    public void deleteFile(String fileReference) {
        try {
            Path path = Paths.get(fileReference);
            Files.deleteIfExists(path);
        } catch (IOException ex) {
            throw new RuntimeException("Could not delete file " + fileReference, ex);
        }
    }

    private String sanitizeFilename(String filename) {
        return filename.replace("\\", "_").replace("/", "_");
    }
}
