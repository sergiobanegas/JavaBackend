package springskeleton.util;

import java.util.ArrayList;
import java.util.List;

import springskeleton.model.Comment;
import springskeleton.model.User;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class TestPopulationData {

    private List<User> userList = new ArrayList<>();

    private List<Comment> commentList = new ArrayList<>();

}
