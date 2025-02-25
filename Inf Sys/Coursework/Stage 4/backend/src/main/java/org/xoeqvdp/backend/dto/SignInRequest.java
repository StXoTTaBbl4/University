package org.xoeqvdp.backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Запрос на аутентификацию")
public class SignInRequest {
    @Schema(description = "Адрес электронной почты", example = "example@mail.com")
    @Size(min = 5, max = 50, message = "Имя пользователя должно содержать от 5 до 50 символов")
    @NotBlank(message = "Имя пользователя не может быть пустыми")
    private String email;

    @Schema(description = "Пароль", example = "my_1secret1_password")
    @Size(min = 8, max = 255, message = "Длина пароля должна быть от 8 до 255 символов")
    @NotBlank(message = "Пароль не может быть пустыми")
    private String password;

    public @Size(min = 5, max = 50, message = "Имя пользователя должно содержать от 5 до 50 символов") @NotBlank(message = "Имя пользователя не может быть пустыми") String getEmail() {
        return email;
    }

    public @Size(min = 8, max = 255, message = "Длина пароля должна быть от 8 до 255 символов") @NotBlank(message = "Пароль не может быть пустыми") String getPassword() {
        return password;
    }
}
