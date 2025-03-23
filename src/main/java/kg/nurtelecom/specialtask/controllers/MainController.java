package kg.nurtelecom.specialtask.controllers;

import jakarta.servlet.http.HttpSession;
import kg.nurtelecom.specialtask.annotation.annotations.BindRequest;
import kg.nurtelecom.specialtask.payload.requests.ProductRequestParam;
import kg.nurtelecom.specialtask.payload.requests.ProductRequestPath;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
public class MainController {

    private static final String UPLOAD_DIR = "uploads";

    @PostMapping("/create")
    public ResponseEntity<String> createProduct(
            @BindRequest ProductRequestParam productRequestParam) {
        System.out.println("Received request to create a product (PARAM): " + productRequestParam);
        saveFile(productRequestParam.multipartFile());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body("Product successfully created with quantity from parameters.");
    }

    @PostMapping("/create/{quantity}")
    public ResponseEntity<String> createProductWithPath(
            @BindRequest ProductRequestPath productRequestPath) {
        System.out.println("Received request to create a product (PATH): " + productRequestPath);
        saveFile(productRequestPath.multipartFile());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body("Product successfully created with quantity from path.");
    }

    @PostMapping("/init-session")
    public ResponseEntity<String> initSession(HttpSession session) {
        session.setAttribute("description", "Premium Product");
        return ResponseEntity.ok("Session initialized");
    }

    private void saveFile(MultipartFile file) {
        if (file != null && !file.isEmpty()) {
            try {
                Path uploadPath = Paths.get(UPLOAD_DIR);
                if (!Files.exists(uploadPath)) {
                    Files.createDirectories(uploadPath);
                }
                Path filePath = uploadPath.resolve(file.getOriginalFilename());
                Files.write(filePath, file.getBytes());
                System.out.println("File saved to: " + filePath.toAbsolutePath());
            } catch (IOException e) {
                throw new RuntimeException("Failed to save file", e);
            }
        }
    }
}