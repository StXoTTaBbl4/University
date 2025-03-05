package com.xoeqvdp.backend.controllers;

import com.xoeqvdp.backend.dto.CertificatesInfoResponseDTO;
import com.xoeqvdp.backend.dto.CertificatesResponseDTO;
import com.xoeqvdp.backend.entities.*;
import com.xoeqvdp.backend.repositories.CertificateCategoryRepository;
import com.xoeqvdp.backend.repositories.CertificateRepository;
import com.xoeqvdp.backend.repositories.CertificateSubCategoryRepository;
import com.xoeqvdp.backend.repositories.EmployeeRepository;
import com.xoeqvdp.backend.services.FileService;
import com.xoeqvdp.backend.services.JwtService;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.*;

@RestController
@RequestMapping("/api/certificates")
public class CertificatesController {

    private final JwtService jwtService;
    private final FileService fileService;

    private final CertificateRepository certificateRepository;
    private final CertificateCategoryRepository certificateCategoryRepository;
    private final CertificateSubCategoryRepository certificateSubCategoryRepository;
    private final EmployeeRepository employeeRepository;

    public CertificatesController(JwtService jwtService, FileService fileService, CertificateRepository certificateRepository, CertificateCategoryRepository certificateCategoryRepository, EmployeeRepository employeeRepository, CertificateSubCategoryRepository certificateSubCategoryRepository) {
        this.jwtService = jwtService;
        this.fileService = fileService;
        this.certificateRepository = certificateRepository;
        this.certificateCategoryRepository = certificateCategoryRepository;
        this.employeeRepository = employeeRepository;
        this.certificateSubCategoryRepository = certificateSubCategoryRepository;
    }

    @PostMapping("/upload-certificate")
    public ResponseEntity<String> uploadFile(
            @RequestHeader(value = "Authorization") String authHeader,
            @RequestParam("name") String name,
            @RequestParam("file") MultipartFile file,
            @RequestParam("category") Long category,
            @RequestParam("subcategory") String subcategory) {

        Optional<Employee> employee = employeeRepository.findByEmail(jwtService.extractUsername(authHeader.substring(7)));

        Optional<CertificateSubCategory> subCategory = certificateSubCategoryRepository.findByNameAndCategory_Id(subcategory, category);

        if (employee.isPresent() && subCategory.isPresent()) {
            try {
                fileService.saveFile(file, employee.get(), subCategory.get(), name);
                return ResponseEntity.ok().build();
            } catch (IOException e) {
                return ResponseEntity.status(500).body("Ошибка загрузки файла");
            }
        } else {
            return ResponseEntity.badRequest().body("Некорректный сотрудник или категория");
        }
    }

    @GetMapping("/certificates")
    public ResponseEntity<List<CertificatesResponseDTO>> getCertificates(@RequestHeader(value = "Authorization") String authHeader){
        Optional<Employee> employee = employeeRepository.findByEmail(jwtService.extractUsername(authHeader.substring(7)));
        return employee.map(value -> ResponseEntity.ok().body(certificateRepository.findByEmployee(value))).orElseGet(() -> ResponseEntity.status(404).body(null));
    }

    @GetMapping("/categories")
    public ResponseEntity<List<CertificateCategory>> getCertificateCategories(){
        List<CertificateCategory> categories = certificateCategoryRepository.findAll();
        return ResponseEntity.ok().body(categories);
    }

    @GetMapping("/{certificateId}")
    public ResponseEntity<Resource> getCertificateFile(@RequestHeader(value = "Authorization") String authHeader, @PathVariable Long certificateId) throws MalformedURLException {
        if (!haveAccess(authHeader, certificateId)){
            return ResponseEntity.status(403).build();
        }

        Optional<Resource> resource = fileService.getFile(certificateId);
        if (resource.isPresent()) {
            String contentType = fileService.determineContentType(certificateId);

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + resource.get().getFilename() + "\"")
                    .body(resource.get());
        } else {
            throw new RuntimeException("File not found for certificate with id: " + certificateId);
        }
    }

    @GetMapping("/{certificateId}/info")
    public ResponseEntity<CertificatesInfoResponseDTO> getCertificateInfo(@RequestHeader(value = "Authorization") String authHeader, @PathVariable Long certificateId) {
        if (haveAccess(authHeader, certificateId)) {
            String email = jwtService.extractUsername(authHeader.substring(7));
            Optional<CertificatesInfoResponseDTO> certificate = certificateRepository.findByIdAndEmployee_Email(certificateId, email);
            return certificate.map(value -> ResponseEntity.ok().body(value)).orElseGet(() -> ResponseEntity.notFound().build());
        }
        return ResponseEntity.status(403).build();

    }

    @DeleteMapping("/{certificateId}/delete")
    public ResponseEntity<Certificate> deleteCertificate(@RequestHeader(value = "Authorization") String authHeader, @PathVariable Long certificateId) throws IOException {
        if (haveAccess(authHeader, certificateId)){
            Optional<Certificate> certificate = certificateRepository.findById(certificateId);
            if (certificate.isPresent()) {
                fileService.deleteFile(certificate.get());
                return ResponseEntity.ok().build();
            }
        }
        return ResponseEntity.notFound().build();
    }

    private boolean haveAccess(String authHeader, Long certificateId) {
        String email = jwtService.extractUsername(authHeader.substring(7));
        Optional<Employee> employee = employeeRepository.findByEmail(email);
        return employee.isPresent() &&
                (certificateRepository.existsByEmployeeAndId(employee.get(), certificateId) ||
                        (certificateRepository.existsById(certificateId) &&
                                employee.get().getRoles().contains(AccountRoles.ADMINISTRATOR)));
    }
}
