package org.xoeqvdp.backend.controllers;

import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.xoeqvdp.backend.entities.Certificate;
import org.xoeqvdp.backend.entities.CertificateSubCategory;
import org.xoeqvdp.backend.entities.Employee;
import org.xoeqvdp.backend.repositories.CertificateSubCategoryRepository;
import org.xoeqvdp.backend.repositories.EmployeeRepository;
import org.xoeqvdp.backend.services.FileService;

import java.io.File;
import java.io.IOException;
import java.util.Optional;

@RestController
@RequestMapping("/api/files")
public class FileController {
    private final FileService fileService;
    private final EmployeeRepository employeeRepository;
    private final CertificateSubCategoryRepository subCategoryRepository;

    public FileController(FileService fileService, EmployeeRepository employeeRepository, CertificateSubCategoryRepository subCategoryRepository) {
        this.fileService = fileService;
        this.employeeRepository = employeeRepository;
        this.subCategoryRepository = subCategoryRepository;
    }


    @PostMapping("/upload-certificate")
    public ResponseEntity<String> uploadFile(
            @RequestParam("file") MultipartFile file,
            @RequestParam("employeeId") Long employeeId,
            @RequestParam("subCategoryId") Long subCategoryId) {

        Optional<Employee> employee = employeeRepository.findById(employeeId);
        Optional<CertificateSubCategory> subCategory = subCategoryRepository.findById(subCategoryId);

        if (employee.isPresent() && subCategory.isPresent()) {
            try {
                Certificate savedCert = fileService.saveFile(file, employee.get(), subCategory.get());
                return ResponseEntity.ok("Файл загружен: " + savedCert.getFilePath());
            } catch (IOException e) {
                return ResponseEntity.status(500).body("Ошибка загрузки файла");
            }
        } else {
            return ResponseEntity.badRequest().body("Некорректный сотрудник или категория");
        }
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
