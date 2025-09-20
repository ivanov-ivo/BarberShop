package com.example.barbershop.service;

import com.example.barbershop.exception.FileUploadException;
import com.example.barbershop.exception.ValidationUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.util.StringUtils;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Service
public class FileUploadService {
    
    private final Path uploadDir;

    public FileUploadService() {
        this.uploadDir = Paths.get("src/main/resources/static/images/barber");
        createUploadDirectory();
    }

    private void createUploadDirectory() {
        try {
            if (!Files.exists(uploadDir)) {
                Files.createDirectories(uploadDir);
            }
        } catch (IOException e) {
            throw new FileUploadException("Could not create upload directory!", e);
        }
    }

    public String uploadFile(MultipartFile file) throws IOException {
        // Check for null file first
        if (file == null) {
            throw FileUploadException.invalidFileName("null file");
        }
        
        // Clean the filename
        String fileName = StringUtils.cleanPath(file.getOriginalFilename());
        
        // Check for null or empty filename
        if (fileName == null || fileName.isEmpty()) {
            throw FileUploadException.invalidFileName("null or empty filename");
        }
        
        // Validate file
        ValidationUtils.validateFileUpload(fileName, file.getSize(), 10 * 1024 * 1024); // 10MB max
        
        // Check if the file's name contains invalid characters
        if (fileName.contains("..")) {
            throw FileUploadException.invalidFileName(fileName);
        }

        // Generate unique filename to prevent overwriting
        String uniqueFileName = System.currentTimeMillis() + "_" + fileName;
        // Copy file to the target location
        Path targetLocation = uploadDir.resolve(uniqueFileName);
        Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

        return "images/barber/" + uniqueFileName;
    }

    public void deleteFile(String fileName) throws IOException {
        try {
            Path filePath = uploadDir.resolve(fileName);
            Files.deleteIfExists(filePath);
        } catch (IOException e) {
            throw FileUploadException.deletionFailed(fileName);
        }
    }
} 