package springskeleton.controller.exception;

import org.springframework.http.HttpStatus;

public class NotFoundException extends GlobalException {

    private static final long serialVersionUID = 4281739815229412747L;

    private static final Integer STATUS = HttpStatus.NOT_FOUND.value();

    public NotFoundException(final String message) {
        super(STATUS, message);
    }

}
