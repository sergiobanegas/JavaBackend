package springskeleton.controller.wrapper.request;

import org.hibernate.validator.constraints.NotEmpty;

import springskeleton.controller.validator.EmailExists;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class EmailRequest {

    @EmailExists
    @NotEmpty(message = "{email.required}")
    private String email;

}
