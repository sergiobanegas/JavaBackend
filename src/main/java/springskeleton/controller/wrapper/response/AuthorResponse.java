package springskeleton.controller.wrapper.response;

import springskeleton.model.Gender;
import springskeleton.model.User;

import lombok.Data;

@Data
class AuthorResponse {

    private Long id;

    private String email;

    private String name;

    private Gender gender;

    AuthorResponse(final User user) {
        this.id = user.getId();
        this.email = user.getEmail();
        this.name = user.getName();
        this.gender = user.getGender();
    }

}
