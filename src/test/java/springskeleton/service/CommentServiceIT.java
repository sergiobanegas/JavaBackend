package springskeleton.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import springskeleton.controller.exception.NotFoundException;
import springskeleton.controller.exception.UnauthorizedException;
import springskeleton.dao.CommentDao;
import springskeleton.model.User;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import springskeleton.dao.UserDao;
import springskeleton.model.Comment;
import springskeleton.util.IntegrationTest;

public class CommentServiceIT extends IntegrationTest {

    private CommentService commentService;

    private UserDao userDao;

    private CommentDao commentDao;

    private Long authorId;

    private Long anotherUserId;

    @Before
    public void init() {
        final User user = this.userDao.findOneByEmail("test1@test.com");
        this.authorId = user.getId();
        this.anotherUserId = this.userDao.findAll().get(0).getId();
    }

    @Test
    public void shouldReturnAnExistingComment() {
        Comment comment = this.findCommentByIndex(0);

        Comment commentFound = this.commentService.findOne(comment.getId());

        assertEquals(comment.getId(), commentFound.getId());
    }

    @Test(expected = NotFoundException.class)
    public void shouldThrowAnExceptionWhenSearchForNonExistentComment() {
        this.commentService.findOne((long) 1234);
    }

    @Test
    public void shouldCreateANewComment() {
        final Comment comment = this.commentService.save(this.authorId, "test-content");

        assertNotNull(comment);
        assertEquals(comment.getContent(), "test-content");
    }

    @Test
    public void shouldAddANewReply() {
        Comment parent = this.findCommentByIndex(2);

        final Comment addedContent = this.commentService.addReply(parent.getId(), "reply content", this.authorId);

        assertEquals(addedContent.getContent(), "reply content");
    }

    @Test(expected = NotFoundException.class)
    public void shouldThrowAnExceptionWhenReplyToNonExistentComment() {
        this.commentService.addReply((long) 1234, "reply content", this.authorId);
    }

    @Test
    public void shouldUpdateAComment() {
        final String content = "updated content";
        Comment comment = this.findCommentByIndex(0);
        this.commentService.update(this.authorId, comment.getId(), content);

        final Comment updatedComment = this.commentDao.findOne(comment.getId());

        assertEquals(updatedComment.getContent(), content);
    }

    @Test(expected = UnauthorizedException.class)
    public void shouldThrowAnExceptionWhenUserUpdatesANotOwnedComment() {
        Comment comment = this.findCommentByIndex(1);

        this.commentService.update(this.anotherUserId, comment.getId(), "updated content");
    }

    @Test
    public void shouldDeleteAComment() {
        Comment comment = this.findCommentByIndex(3);

        this.commentService.delete(comment.getAuthor().getId(), comment.getId());

        assertNull(this.commentDao.findOne(comment.getId()));
    }

    @Test(expected = NotFoundException.class)
    public void shouldThrowAnExceptionIfUserDeletesNonExistentComment() {
        this.commentService.delete(this.authorId, 123L);
    }

    @Test(expected = UnauthorizedException.class)
    public void shouldThrowAnExceptionWhenUserDeletesANotOwnedComment() {
        Comment comment = this.findCommentByIndex(1);

        this.commentService.delete(this.anotherUserId, comment.getId());
    }

    @Autowired
    public void setUserDao(UserDao userDao) {
        this.userDao = userDao;
    }

    @Autowired
    public void setCommentDao(CommentDao commentDao) {
        this.commentDao = commentDao;
    }

    @Autowired
    public void setCommentService(CommentService commentService) {
        this.commentService = commentService;
    }

    private Comment findCommentByIndex(final int index) {
        return this.commentService.getAllComments().get(index);
    }


}
