package com.fanus.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class FileStorageService {

    @Value("${app.upload.dir}")
    private String uploadDir;

    @Value("${app.upload.base-url}")
    private String baseUrl;

    public String store(MultipartFile file) {
        if (file.isEmpty()) throw new IllegalArgumentException("File is empty");

        String original = file.getOriginalFilename();
        String ext = (original != null && original.contains("."))
            ? original.substring(original.lastIndexOf('.'))
            : ".jpg";
        String filename = UUID.randomUUID() + ext;

        try {
            Path dir = Paths.get(uploadDir);
            if (!Files.exists(dir)) Files.createDirectories(dir);
            Files.copy(file.getInputStream(), dir.resolve(filename));
        } catch (IOException e) {
            throw new RuntimeException("Could not store file", e);
        }

        return baseUrl + filename;
    }
}
