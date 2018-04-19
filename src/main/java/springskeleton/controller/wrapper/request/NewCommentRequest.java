package springskeleton.controller.wrapper.request;

import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.NotEmpty;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class NewCommentRequest {

    @NotEmpty(message = "{content.required}")
    @Size(min = 3, max = 500, message = "{comment.size.error}")
    private String content;

}
