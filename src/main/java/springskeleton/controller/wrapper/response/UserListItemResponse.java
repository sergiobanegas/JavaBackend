package springskeleton.controller.wrapper.response;

import springskeleton.model.User;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UserListItemResponse {

    private Long id;

    private String email;

    private String name;

    private String avatar;

    public UserListItemResponse(final User user) {
        this.id = user.getId();
        this.email = user.getEmail();
        this.name = user.getName();
        this.avatar = user.getAvatar();
    }

}
