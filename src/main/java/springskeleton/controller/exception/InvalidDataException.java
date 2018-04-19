package springskeleton.controller.exception;

import java.util.List;

import org.springframework.http.HttpStatus;

public class InvalidDataException extends GlobalException {

    private static final long serialVersionUID = 4281739815229412747L;

    private static final Integer STATUS = HttpStatus.BAD_REQUEST.value();

    public InvalidDataException(final String message) {
        super(STATUS, message);
    }

    public InvalidDataException(final String message, final List<String> errors) {
        super(STATUS, message, errors);
    }

}
