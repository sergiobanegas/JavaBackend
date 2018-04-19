package springskeleton.controller.wrapper.request;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.Size;

@Data
@NoArgsConstructor
public class ResetPasswordRequest {

    @NotEmpty(message = "{password.required}")
    @Size(min = 7, max = 30, message = "{password.size.error}")
    private String password;

}
