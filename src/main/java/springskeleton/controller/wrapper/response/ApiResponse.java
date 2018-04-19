package springskeleton.controller.wrapper.response;

import java.util.List;

import org.springframework.http.HttpStatus;

import springskeleton.controller.exception.GlobalException;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Singular;
import lombok.ToString;

@Builder
@AllArgsConstructor
@RequiredArgsConstructor
@ToString
@Data
public class ApiResponse {

    @NonNull
    private Integer status;

    @NonNull
    private String message;

    @Singular
    private List<String> errors;

    @SuppressWarnings("unused")
    public static class ApiResponseBuilder {

        public ApiResponseBuilder unauthorized() {
            this.setStatus(HttpStatus.UNAUTHORIZED);
            return this;
        }

        public ApiResponseBuilder forbidden() {
            this.setStatus(HttpStatus.FORBIDDEN);
            return this;
        }

        public ApiResponseBuilder ok() {
            this.setStatus(HttpStatus.OK);
            return this;
        }

        public ApiResponseBuilder created() {
            this.setStatus(HttpStatus.CREATED);
            return this;
        }

        public ApiResponseBuilder badRequest() {
            this.setStatus(HttpStatus.BAD_REQUEST);
            return this;
        }

        public ApiResponseBuilder internalServerError() {
            this.setStatus(HttpStatus.INTERNAL_SERVER_ERROR);
            return this;
        }

        public ApiResponseBuilder fromException(final GlobalException exception) {
            this.status = exception.getStatus();
            this.message = exception.getMessage();
            return this;
        }

        private void setStatus(final HttpStatus status) {
            this.status = status.value();
        }
    }

}
