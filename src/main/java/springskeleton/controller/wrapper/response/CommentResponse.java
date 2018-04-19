package springskeleton.controller.wrapper.response;

import java.util.List;
import java.util.stream.Collectors;

import springskeleton.model.Comment;

import lombok.Data;

@Data
public class CommentResponse {

    private Long id;

    private AuthorResponse author;

    private String content;

    private List<Long> replies;

    public CommentResponse(final Comment comment) {
        this.author = new AuthorResponse(comment.getAuthor());
        this.id = comment.getId();
        this.content = comment.getContent();
        this.replies = comment.getReplies().stream().map(Comment::getId).collect(Collectors.toList());
    }

}
