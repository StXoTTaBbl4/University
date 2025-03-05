package com.xoeqvdp.backend.services;

import com.xoeqvdp.backend.entities.Certificate;
import com.xoeqvdp.backend.entities.CertificateSubCategory;
import com.xoeqvdp.backend.entities.Employee;
import com.xoeqvdp.backend.repositories.CertificateRepository;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.beans.Transient;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Optional;

@Service
public class FileService {
    private final CertificateRepository certificateRepository;
    private final String uploadDir = "D:\\uploads";
    private final Path fileStorageLocation = Paths.get(uploadDir).toAbsolutePath().normalize();

    public FileService(CertificateRepository certificateRepository) {
        this.certificateRepository = certificateRepository;
        File dir = new File(uploadDir);
        if (!dir.exists()) dir.mkdirs();
    }

    public Certificate saveFile(MultipartFile file, Employee employee, CertificateSubCategory subCategory, String name) throws IOException {
        String userDir = uploadDir + "/" + employee.getId();
        File dir = new File(userDir);
        if (!dir.exists()) dir.mkdirs();

        String filePath = userDir + "/" + file.getOriginalFilename();
        Path destination = Paths.get(filePath);
        Files.copy(file.getInputStream(), destination, StandardCopyOption.REPLACE_EXISTING);

        Certificate certificate = new Certificate();
        certificate.setEmployee(employee);
        certificate.setSubCategory(subCategory);
        certificate.setFilePath(filePath);
        certificate.setName(name);

        return certificateRepository.save(certificate);
    }


    public Optional<Resource> getFile(Long certificateId) throws MalformedURLException {
        Optional<Certificate> certificate = certificateRepository.findById(certificateId);
        if (certificate.isPresent()) {
            Path filePath = this.fileStorageLocation.resolve(certificate.get().getFilePath()).normalize();
            Resource resource = new UrlResource(filePath.toUri());
            return Optional.of(resource);
        }
        return Optional.empty();
    }

    @Transactional
    public void deleteFile(Certificate certificate) throws IOException {
        try {
            Path path = Paths.get(certificate.getFilePath());
            boolean success = Files.deleteIfExists(path);
            if (!success) {
                throw new IOException("Сертификат для удаления не найден на диске.");
            }
        } catch (IOException e) {
            throw new IOException("Не удалось удалить файл сертификата");
        }

        certificateRepository.delete(certificate);
    }

    public String determineContentType(Long certificateId) {
        Optional<Certificate> certificate = certificateRepository.findById(certificateId);
        if (certificate.isPresent()) {
            String fileName = certificate.get().getFilePath().split("/")[2];
            if (fileName.endsWith(".png")) {
                return "image/png";
            } else if (fileName.endsWith(".jpg") || fileName.endsWith(".jpeg")) {
                return "image/jpeg";
            } else if (fileName.endsWith(".pdf")) {
                return "application/pdf";
            } else {
                return "application/octet-stream";
            }
        }
        return "application/octet-stream";
    }
}

