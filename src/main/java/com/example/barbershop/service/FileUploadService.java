package com.example.barbershop.service;

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
            throw new RuntimeException("Could not create upload directory!", e);
        }
    }

    public String uploadFile(MultipartFile file) throws IOException {
        // Clean the filename
        String fileName = StringUtils.cleanPath(file.getOriginalFilename());
        
        // Check if the file's name contains invalid characters
        if (fileName.contains("..")) {
            throw new RuntimeException("Filename contains invalid path sequence " + fileName);
        }

        // Generate unique filename to prevent overwriting
        String uniqueFileName = System.currentTimeMillis() + "_" + fileName;
        // Copy file to the target location
        Path targetLocation = uploadDir.resolve(uniqueFileName);
        Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

        return "images/barber/" + uniqueFileName;
    }

    public void deleteFile(String fileName) throws IOException {
        Path filePath = uploadDir.resolve(fileName);
        Files.deleteIfExists(filePath);
    }
} 