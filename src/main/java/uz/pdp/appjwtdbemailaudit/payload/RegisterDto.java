package uz.pdp.appjwtdbemailaudit.payload;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
public class RegisterDto {

    @NotNull
    @Size(min = 3, max = 50) // repository => spring-boot-starter-validation
    private String firstName;

    @NotNull
    @Length(min = 3, max = 50) // repository => spring-boot-starter-validation
    private String lastName;

    @NotNull
    @Email
    private String email;

    @NotNull
    private String password;
}
