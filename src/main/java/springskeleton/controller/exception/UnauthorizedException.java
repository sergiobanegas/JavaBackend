package springskeleton.controller.exception;

import org.springframework.http.HttpStatus;

public class UnauthorizedException extends GlobalException {

    private static final long serialVersionUID = -3987333668531502635L;

    private static final Integer STATUS = HttpStatus.UNAUTHORIZED.value();

    public UnauthorizedException(final String message) {
        super(STATUS, message);
    }

}
