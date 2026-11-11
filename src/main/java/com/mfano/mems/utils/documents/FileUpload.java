package com.mfano.mems.utils.documents;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Set;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.transaction.Transactional;

@Service
public class FileUpload {

    @Value("${app.upload-dir}")
    private String baseDirectory;
    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024; // 10 MB

    /// uploads1
    public String saveFile(MultipartFile file, String path) throws IOException {
        String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
        Path filePath = Paths.get(path + fileName);
        Files.write(filePath, file.getBytes());
        return filePath.toString(); // save this path to DB
    }

    /// uploads2
    @Transactional
    public String uploadFile(MultipartFile file, String path, RedirectAttributes red) {
        Set<String> ALLOWED_DOCUMENT_TYPES = Set.of(
                "application/pdf",
                "application/xls",

                // Images
                "image/jpeg",
                "image/png",
                "image/jpg"

        );
        if (file == null || file.isEmpty()) {
            red.addFlashAttribute("error", "Please select a file.");
        }
        if (file.getContentType() == null ||
                !ALLOWED_DOCUMENT_TYPES.contains(file.getContentType())) {
            red.addFlashAttribute("error", "Unsupported document type.");
        }
        if (file.getSize() > MAX_FILE_SIZE) {
            red.addFlashAttribute("error", "File size must not exceed 10 MB.");
        }

        // Create profile directory
        Path fileDirectory = Paths.get(baseDirectory, path);
        try {
            Files.createDirectories(fileDirectory);
        } catch (IOException e) {
            e.printStackTrace();
            red.addFlashAttribute("error", "Failed to create file path.");
        }

        // Get extension
        String originalName = file.getOriginalFilename();

        String extension = "";

        if (originalName != null && originalName.contains(".")) {
            extension = originalName.substring(
                    originalName.lastIndexOf(".")).toLowerCase();
        }

        // Generate safe unique filename
        String filename = UUID.randomUUID() + extension;
        Path destination = fileDirectory.resolve(filename)
                .normalize();

        // Make sure destination remains inside upload directory
        if (!destination.startsWith(fileDirectory.normalize())) {
            red.addFlashAttribute("error", "Invalid file path.");
        }

        try {
            Files.copy(file.getInputStream(),
                    destination,
                    StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            e.printStackTrace();
            red.addFlashAttribute("error", "Something went wrong, check your file and try again.");
        }
        return filename;
    }

    // Delete Image
    public void deleteFile(String fileName) throws IOException {
        Path path = Path.of(baseDirectory, fileName)
                .toAbsolutePath()
                .normalize();
        Files.delete(path);
    }
}
