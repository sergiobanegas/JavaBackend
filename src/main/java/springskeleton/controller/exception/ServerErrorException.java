package springskeleton.controller.exception;

import java.util.List;

import org.springframework.http.HttpStatus;

public class ServerErrorException extends GlobalException {

    private static final long serialVersionUID = 4281739815229412747L;

    private static final Integer STATUS = HttpStatus.INTERNAL_SERVER_ERROR.value();

    public ServerErrorException(final String message) {
        super(STATUS, message);
    }

    public ServerErrorException(final String message, final List<String> errors) {
        super(STATUS, message, errors);
    }

}
