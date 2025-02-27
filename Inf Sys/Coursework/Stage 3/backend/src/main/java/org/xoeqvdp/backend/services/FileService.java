package org.xoeqvdp.backend.services;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.xoeqvdp.backend.entities.Certificate;
import org.xoeqvdp.backend.entities.CertificateSubCategory;
import org.xoeqvdp.backend.entities.Employee;
import org.xoeqvdp.backend.repositories.CertificateRepository;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Optional;

@Service
public class FileService {
    private final CertificateRepository fileRepository;
    private final String uploadDir = "uploads"; // Базовая директория для хранения файлов

    public FileService(CertificateRepository  fileRepository) {
        this.fileRepository = fileRepository;
        File dir = new File(uploadDir);
        if (!dir.exists()) dir.mkdirs(); // Создаём папку uploads, если её нет
    }

    /**
     * Сохранение файла в папку пользователя и запись пути в БД.
     */
    public Certificate saveFile(MultipartFile file, Employee employee, CertificateSubCategory subCategory) throws IOException {
        String userDir = uploadDir + "/" + employee.getId(); // Папка пользователя
        File dir = new File(userDir);
        if (!dir.exists()) dir.mkdirs(); // Создать папку, если её нет

        // Формируем путь к файлу
        String filePath = userDir + "/" + file.getOriginalFilename();
        Path destination = Paths.get(filePath);
        Files.copy(file.getInputStream(), destination, StandardCopyOption.REPLACE_EXISTING);

        // Создаём сущность Certificate и сохраняем в БД
        Certificate certificate = new Certificate();
        certificate.setEmployee(employee);
        certificate.setSubCategory(subCategory);
        certificate.setFilePath(filePath);

        return fileRepository.save(certificate);
    }

    /**
     * Получение файла по ID сертификата.
     */
    public Optional<File> getFile(Long certificateId) {
        return fileRepository.findById(certificateId)
                .map(cert -> new File(cert.getFilePath()));
    }
}
