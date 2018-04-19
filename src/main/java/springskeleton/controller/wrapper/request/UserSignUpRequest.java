package springskeleton.controller.wrapper.request;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotEmpty;

import springskeleton.controller.validator.EmailNotExists;
import springskeleton.model.Gender;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserSignUpRequest {

    @NotEmpty(message = "{email.required}")
    @Email
    @EmailNotExists
    private String email;

    @NotEmpty(message = "{password.required}")
    @Size(min = 7, max = 30, message = "{password.size.error}")
    private String password;

    @NotEmpty(message = "{name.required}")
    @Size(min = 3, max = 10, message = "{name.size.error}")
    private String name;

    @NotNull(message = "{gender.required}")
    private Gender gender;

}
