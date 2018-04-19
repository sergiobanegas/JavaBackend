package springskeleton.controller.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.springframework.beans.factory.annotation.Autowired;
import springskeleton.service.AuthService;

public class EmailNotExistsValidator implements ConstraintValidator<EmailNotExists, String> {

    private AuthService authService;

    @Autowired
    public EmailNotExistsValidator(AuthService authService) {
        this.authService = authService;
    }

    @Override
    public void initialize(final EmailNotExists constraintAnnotation) {

    }

    @Override
    public boolean isValid(final String email, final ConstraintValidatorContext context) {
        try {
            return !this.authService.existsUser(email);
        } catch (final Exception ex) {
            return false;
        }
    }
}
