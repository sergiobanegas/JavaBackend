package springskeleton.controller.wrapper.request;

import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotEmpty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
@NoArgsConstructor
public class UserLoginRequest {

    @NonNull
    @NotEmpty(message = "{email.required}")
    @Email
    private String email;

    @NonNull
    @NotEmpty(message = "{password.required}")
    private String password;

    private boolean remember = false;

}
