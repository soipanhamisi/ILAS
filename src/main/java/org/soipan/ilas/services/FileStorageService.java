package org.soipan.ilas.services;

import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobContainerClientBuilder;
import org.soipan.ilas.config.FileStorageProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;

@Service
public class FileStorageService {

    private static final String PROVIDER_AZURE = "azure";
    private static final String AZURE_REF_PREFIX = "azure://";

    private final Path fileStorageLocation;
    private final String provider;
    private final String azureContainerName;
    private final BlobContainerClient blobContainerClient;

    @Autowired
    public FileStorageService(FileStorageProperties fileStorageProperties) {
        this.provider = normalizeProvider(fileStorageProperties.getProvider());

        if (isAzureProvider()) {
            String connectionString = fileStorageProperties.getAzure().getConnectionString();
            this.azureContainerName = fileStorageProperties.getAzure().getContainer();
            validateAzureConfig(connectionString, azureContainerName);

            this.blobContainerClient = new BlobContainerClientBuilder()
                    .connectionString(connectionString)
                    .containerName(azureContainerName)
                    .buildClient();
            this.blobContainerClient.createIfNotExists();
            this.fileStorageLocation = null;
        } else {
            this.azureContainerName = null;
            this.blobContainerClient = null;
            this.fileStorageLocation = Paths.get(fileStorageProperties.getUploadDir())
                    .toAbsolutePath().normalize();

            try {
                Files.createDirectories(this.fileStorageLocation);
            } catch (Exception ex) {
                throw new RuntimeException("Could not create the directory where the uploaded files will be stored.", ex);
            }
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
            if (isAzureProvider()) {
                BlobClient blobClient = blobContainerClient.getBlobClient(fileName);
                blobClient.upload(file.getInputStream(), file.getSize(), true);
                return toAzureReference(fileName);
            }

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
            if (isAzureReference(fileReference)) {
                String blobName = extractBlobName(fileReference);
                BlobClient blobClient = blobContainerClient.getBlobClient(blobName);
                if (!blobClient.exists()) {
                    throw new IllegalArgumentException("Exam question file was not found");
                }

                try (BufferedReader reader = new BufferedReader(
                        new InputStreamReader(blobClient.openInputStream(), StandardCharsets.UTF_8))) {
                    return reader.lines().toList();
                }
            }

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
            if (isAzureReference(fileReference)) {
                String blobName = extractBlobName(fileReference);
                blobContainerClient.getBlobClient(blobName).deleteIfExists();
                return;
            }

            Path path = Paths.get(fileReference);
            Files.deleteIfExists(path);
        } catch (IOException ex) {
            throw new RuntimeException("Could not delete file " + fileReference, ex);
        }
    }

    private boolean isAzureProvider() {
        return PROVIDER_AZURE.equals(provider);
    }

    private String normalizeProvider(String providerValue) {
        if (providerValue == null || providerValue.isBlank()) {
            return "local";
        }
        return providerValue.trim().toLowerCase();
    }

    private void validateAzureConfig(String connectionString, String containerName) {
        if (connectionString == null || connectionString.isBlank()) {
            throw new IllegalArgumentException("AZURE_STORAGE_CONNECTION_STRING is required when file.storage.provider=azure");
        }
        if (containerName == null || containerName.isBlank()) {
            throw new IllegalArgumentException("AZURE_STORAGE_CONTAINER is required when file.storage.provider=azure");
        }
    }

    private String sanitizeFilename(String filename) {
        return filename.replace("\\", "_").replace("/", "_");
    }

    private String toAzureReference(String blobName) {
        return AZURE_REF_PREFIX + azureContainerName + "/" + blobName;
    }

    private boolean isAzureReference(String fileReference) {
        return fileReference.startsWith(AZURE_REF_PREFIX);
    }

    private String extractBlobName(String fileReference) {
        String withoutPrefix = fileReference.substring(AZURE_REF_PREFIX.length());
        int slashIndex = withoutPrefix.indexOf('/');
        if (slashIndex < 0 || slashIndex + 1 >= withoutPrefix.length()) {
            throw new IllegalArgumentException("Invalid Azure file reference: " + fileReference);
        }
        return withoutPrefix.substring(slashIndex + 1);
    }
}
