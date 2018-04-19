package springskeleton.controller.wrapper.response;

import java.util.Date;

import springskeleton.model.Gender;
import springskeleton.model.Language;
import springskeleton.model.User;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class AccountResponse {

    private Long id;

    private String email;

    private String name;

    private Gender gender;

    private String avatar;

    private Language language;

    private Date createdAt;

    private Date updatedAt;

    public AccountResponse(final User user) {
        this.id = user.getId();
        this.avatar = user.getAvatar();
        this.email = user.getEmail();
        this.name = user.getName();
        this.gender = user.getGender();
        this.language = user.getLanguage();
        this.createdAt = user.getCreatedAt();
        this.updatedAt = user.getUpdatedAt();
    }

}
