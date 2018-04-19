package springskeleton.controller.wrapper.response;

import springskeleton.model.Gender;
import springskeleton.model.User;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
public class UserResponse {

    private Long id;

    private String email;

    private String name;

    private Gender gender;

    private String avatar;

    private Date createdAt;

    private Date updatedAt;

    public UserResponse(final User user) {
        this.id = user.getId();
        this.avatar = user.getAvatar();
        this.email = user.getEmail();
        this.name = user.getName();
        this.gender = user.getGender();
        this.createdAt = user.getCreatedAt();
        this.updatedAt = user.getUpdatedAt();
    }

}
