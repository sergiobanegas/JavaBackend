package springskeleton.controller.advice;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import springskeleton.controller.exception.InvalidDataException;
import springskeleton.controller.exception.NotFoundException;
import springskeleton.controller.exception.ServerErrorException;
import springskeleton.controller.exception.UnauthorizedException;
import springskeleton.controller.wrapper.response.ApiResponse;
import springskeleton.util.I18n;

@ControllerAdvice
@Component
public class WebRestControllerAdvice {

    private static final Logger logger = LogManager.getLogger();

    private final I18n i18n;

    @Autowired
    public WebRestControllerAdvice(I18n i18n) {
        this.i18n = i18n;
    }

    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    public ResponseEntity<ApiResponse> handleNotFound(final NotFoundException ex) {
        return this.buildResponseError(ApiResponse.builder().fromException(ex).build());
    }

    @ExceptionHandler(InvalidDataException.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public ResponseEntity<ApiResponse> handleInvalidData(final InvalidDataException ex) {
        return this.buildResponseError(ApiResponse.builder().fromException(ex).build());
    }

    @ExceptionHandler(UnauthorizedException.class)
    @ResponseStatus(value = HttpStatus.UNAUTHORIZED)
    public ResponseEntity<ApiResponse> handleUnauthorized(final UnauthorizedException ex) {
        return this.buildResponseError(ApiResponse.builder().fromException(ex).build());
    }

    @ExceptionHandler(JsonParseException.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public ResponseEntity<ApiResponse> handleWrongJSONFormat() {
        return this.buildResponseError(
                ApiResponse.builder().badRequest().message(this.i18n.get("wrong.json.format")).build());
    }

    @ExceptionHandler(InvalidFormatException.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public ResponseEntity<ApiResponse> handleMessageNotReadable(final InvalidFormatException ex) {
        return this.buildResponseError(
                ApiResponse.builder().badRequest().message(this.i18n.get("wrong.data.provided")).build());
    }

    @ExceptionHandler(ServerErrorException.class)
    @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<ApiResponse> handleServerError(final ServerErrorException ex) {
        logger.error(ex.getMessage());
        return this.buildResponseError(
                ApiResponse.builder().internalServerError().message(this.i18n.get("internal.server.error")).build());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public ResponseEntity<ApiResponse> invalidInput(final MethodArgumentNotValidException ex) {
        List<String> errors = ex.getBindingResult().getAllErrors().stream().map(DefaultMessageSourceResolvable::getDefaultMessage)
                .collect(Collectors.toList());
        String message = this.i18n.get("validation.failed.message", new Object[]{ex.getBindingResult().getErrorCount()});
        return this.buildResponseError(ApiResponse.builder().badRequest().message(message).errors(errors).build());
    }

    private ResponseEntity<ApiResponse> buildResponseError(final ApiResponse response) {
        return new ResponseEntity<>(response, HttpStatus.valueOf(response.getStatus()));
    }

}
