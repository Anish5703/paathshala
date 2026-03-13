package com.paathshala.service;

import com.paathshala.entity.Content;
import com.paathshala.exception.FileUploadFailedException;
import com.paathshala.model.ErrorType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;
import java.util.UUID;

@Service
public class ContentService {

    private final Logger logger = LoggerFactory.getLogger(ContentService.class);


    /*
        Generate unique file url
        save to the directory
        save content properties i.e. url,type,size
        returns null object if operation fails and content object if succeeds
         */
    @Transactional
    public <T extends Content> T saveContentFileAndProperties(
            T content,
            MultipartFile file,
            String uploadDirectory) {

        String uniqueFileName;
        String newHash;

        try {
            newHash = calculateHash(file);

            // Same file uploaded
            if (newHash.equals(content.getContentHash())) {
                logger.info("Same file detected, skipping save");
                return content;
            }

            uniqueFileName = storeFile(file, uploadDirectory, content.getContentUrl());

        } catch (IOException e) {
            logger.error(ErrorType.FILE_UPLOAD_FAILED.toString(), e.getMessage());
            throw new FileUploadFailedException("File upload failed");
        }

        // DB-related changes (transaction-safe)
        content.setContentUrl(uniqueFileName);
        content.setContentType(file.getContentType());
        content.setContentSize(file.getSize());
        content.setContentHash(newHash);

        return content;
    }



    public String storeFile(
            MultipartFile file,
            String uploadDirectory,   // e.g., "uploads/notes"
            String oldFileName
    ) throws IOException {

        // Convert directory to relative path
        Path directoryPath = Paths.get(uploadDirectory);
        logger.info("Resolved upload directory: {}", directoryPath);

        // Create directories if they do not exist
        if (!Files.exists(directoryPath)) {
            Files.createDirectories(directoryPath);
        }

        // Delete old file if provided
        if (oldFileName != null && !oldFileName.isEmpty()) {
            Path oldFilePath = directoryPath.resolve(oldFileName);
            logger.info("Deleted old file if exists: {}", oldFilePath);
            Files.deleteIfExists(oldFilePath);
        }
        // Check file is not empty
        if (file.isEmpty()) {
            throw new IOException("Uploaded file is empty!");
        }

        // Get file extension
        String originalName = file.getOriginalFilename();
        String extension = "";
        if (originalName != null && originalName.contains(".")) {
            extension = originalName.substring(originalName.lastIndexOf('.') + 1);
        }

        // Generate unique filename
        String uniqueFileName = UUID.randomUUID().toString() + (extension.isEmpty() ? "" : "." + extension);

        // Save the file to disk
        Path filePath = directoryPath.resolve(uniqueFileName);
        Files.write(filePath, file.getBytes());
        logger.info("File saved successfully at: {}", filePath.toAbsolutePath());

        return uniqueFileName;
    }

    public  boolean isHashEqual(String contentHash,MultipartFile file){

        String newHash = calculateHash(file);
        return contentHash != null &&
                contentHash.equals(newHash);
    }


    public String calculateHash(MultipartFile file)  {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(file.getBytes());
            return HexFormat.of().formatHex(hash);
        }
        catch(NoSuchAlgorithmException | IOException ex)
        {
            logger.error("File hash calculation error : {}",ex.getMessage());
            throw new FileUploadFailedException("File hash calculation error");
        }
    }

}
