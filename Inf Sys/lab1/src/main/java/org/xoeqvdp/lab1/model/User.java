package org.xoeqvdp.lab1.model;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.xoeqvdp.lab1.database.RoleConverter;

@Entity
@Table(name = "users")
@Getter
@Setter
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, name = "username")
    private String username;

    @Column(nullable = false, unique = true, name = "password_hash")
    private String passwordHash; // Пароль будет хэшироваться SHA-384 перед сохранением

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, name = "role")
    private Roles role = Roles.USER;


    public void setPasswordHash(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-384");

            byte[] hashBytes = digest.digest(password.getBytes());

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
