package springskeleton.controller.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.springframework.beans.factory.annotation.Autowired;
import springskeleton.service.AuthService;

public class EmailExistsValidator implements ConstraintValidator<EmailExists, String> {

    private AuthService authService;

    @Autowired
    public EmailExistsValidator(AuthService authService) {
        this.authService = authService;
    }

    @Override
    public void initialize(final EmailExists constraintAnnotation) {

    }

    @Override
    public boolean isValid(final String email, final ConstraintValidatorContext context) {
        try {
            return authService.existsUser(email);
        } catch (final Exception ex) {
            return false;
        }
    }
}
