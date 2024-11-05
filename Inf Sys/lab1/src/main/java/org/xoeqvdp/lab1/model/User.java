package org.xoeqvdp.lab1.model;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "users")
@Getter
@Setter
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String passwordHash; // Пароль будет хэшироваться SHA-384 перед сохранением


    public void setPasswordHash(String password) {
        try {
            // Создаем объект MessageDigest с алгоритмом SHA-384
            MessageDigest digest = MessageDigest.getInstance("SHA-384");

            // Преобразуем строку в массив байтов и вычисляем хэш
            byte[] hashBytes = digest.digest(password.getBytes());

            // Конвертируем массив байтов в шестнадцатеричное представление
            StringBuilder hexString = new StringBuilder();
            for (byte b : hashBytes) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }

            this.passwordHash =  hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-384 algorithm not found", e);
        }
    }
}
