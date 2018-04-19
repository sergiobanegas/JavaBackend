package springskeleton.controller.exception;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.Singular;

@AllArgsConstructor
@RequiredArgsConstructor
public abstract class GlobalException extends RuntimeException {

    private static final long serialVersionUID = 2100517160928910309L;

    @NonNull
    @Getter
    @Setter
    private Integer status;

    @NonNull
    @Getter
    @Setter
    public String message;

    @Getter
    @Setter
    @Singular
    private List<String> errors;

}
