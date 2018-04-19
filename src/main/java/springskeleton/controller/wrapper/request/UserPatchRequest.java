package springskeleton.controller.wrapper.request;

import springskeleton.controller.validator.EmailNotExists;
import springskeleton.model.Gender;

import springskeleton.model.Language;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UserPatchRequest {

    private String name;

    private Gender gender;

    private Language language;

    @EmailNotExists
    private String email;

    public boolean isEmpty() {
        return this.name == null && this.gender == null && this.language == null && this.email == null;
    }

}
