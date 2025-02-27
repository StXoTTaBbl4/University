package org.xoeqvdp.backend.controllers;

import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.xoeqvdp.backend.dto.CertificatesResponse;
import org.xoeqvdp.backend.entities.Certificate;
import org.xoeqvdp.backend.entities.CertificateCategory;
import org.xoeqvdp.backend.entities.CertificateSubCategory;
import org.xoeqvdp.backend.entities.Employee;
import org.xoeqvdp.backend.repositories.CertificateCategoryRepository;
import org.xoeqvdp.backend.repositories.CertificateRepository;
import org.xoeqvdp.backend.repositories.CertificateSubCategoryRepository;
import org.xoeqvdp.backend.repositories.EmployeeRepository;
import org.xoeqvdp.backend.services.FileService;
import org.xoeqvdp.backend.services.JwtService;

import java.io.File;
import java.io.IOException;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/files")
public class FileController {
    private final FileService fileService;
    private final EmployeeRepository employeeRepository;
    private final CertificateRepository certificateRepository;
    private final CertificateCategoryRepository certificateCategoryRepository;
    private final CertificateSubCategoryRepository subCategoryRepository;

    private final JwtService jwtService;

    public FileController(FileService fileService, EmployeeRepository employeeRepository, CertificateRepository certificateRepository, CertificateCategoryRepository certificateCategoryRepository, CertificateSubCategoryRepository subCategoryRepository, JwtService jwtService) {
        this.fileService = fileService;
        this.employeeRepository = employeeRepository;
        this.certificateRepository = certificateRepository;
        this.certificateCategoryRepository = certificateCategoryRepository;
        this.subCategoryRepository = subCategoryRepository;
        this.jwtService = jwtService;
    }

    @PostMapping("/upload-certificate")
    public ResponseEntity<String> uploadFile(
            @RequestHeader(value = "Authorization") String authHeader,
            @RequestParam("name") String name,
            @RequestParam("file") MultipartFile file,
            @RequestParam("categoryId") Long categoryId,
            @RequestParam("subCategoryId") Long subCategoryId) {

        if(!jwtService.getExpirationDate(authHeader.substring(7)).isAfter(Instant.now())){
            return null;
        }

        Optional<Employee> employee = employeeRepository.findByEmail(jwtService.extractEmail(authHeader.substring(7)));

        Optional<CertificateSubCategory> subCategory = subCategoryRepository.findById(subCategoryId);

        if (employee.isPresent() && subCategory.isPresent()) {
            try {
                Certificate savedCert = fileService.saveFile(file, employee.get(), subCategory.get(), name);
                return ResponseEntity.ok("Файл загружен: " + savedCert.getFilePath());
            } catch (IOException e) {
                return ResponseEntity.status(500).body("Ошибка загрузки файла");
            }
        } else {
            return ResponseEntity.badRequest().body("Некорректный сотрудник или категория");
        }
    }

    @GetMapping("/certificates")
    public ResponseEntity<List<CertificatesResponse>> getCertificates(@RequestHeader(value = "Authorization") String authHeader){
        if(!jwtService.getExpirationDate(authHeader.substring(7)).isAfter(Instant.now())){
            return null;
        }

        Employee employee = employeeRepository.findByEmail(jwtService.extractEmail(authHeader.substring(7))).orElse(null);
        if (employee == null){
            return null;
        }

        return ResponseEntity.ok().body(certificateRepository.findByEmployee(employee));
    }

    @GetMapping("/categories")
    public ResponseEntity<Map<String, List<String>>> getCertificateCategories(@RequestHeader(value = "Authorization") String authHeader){
        Map<String, List<String>> response = new HashMap<>();
        response.put("categories", certificateCategoryRepository.findAllNames());
        return ResponseEntity.ok().body(response);
    }

    @GetMapping("categories/{categoryId}/subcategories")
    public List<String> getSubcategories(@PathVariable Long categoryId) {
        CertificateCategory category = certificateCategoryRepository.findById(categoryId).orElse(null);
        if (category == null) {
            return null;
        }

        return subCategoryRepository.findAllNames(category);
    }

    @GetMapping("/{certificateId}")
    public ResponseEntity<Resource> getFile(@PathVariable Long certificateId) {
        Optional<File> fileOpt = fileService.getFile(certificateId);
        if (fileOpt.isPresent()) {
            File file = fileOpt.get();
            Resource resource = new FileSystemResource(file);
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getName() + "\"")
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(resource);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
