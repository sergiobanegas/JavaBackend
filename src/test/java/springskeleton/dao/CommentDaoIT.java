package springskeleton.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import springskeleton.config.property.AdminUserProperties;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import springskeleton.model.Comment;
import springskeleton.model.User;
import springskeleton.util.DaoIntegrationTest;

public class CommentDaoIT extends DaoIntegrationTest {

    private CommentDao commentDao;

    private UserDao userDao;

    private AdminUserProperties adminUserProperties;

    @Test
    public void shouldSaveAComment() {
        final User author = this.userDao.findOneByEmail(adminUserProperties.getEmail());
        final Comment comment = this.saveComment(author, "content");

        final Comment savedComment = this.commentDao.findOne(comment.getId());

        assertEquals(savedComment.getContent(), "content");
    }

    @Test
    public void shouldAddAReply() {
        final User author = this.userDao.findOneByEmail(adminUserProperties.getEmail());
        Comment comment = this.saveComment(author, "reply-content");
        Comment parent = this.getCommentByIndex(0);

        final Comment updatedParent = this.addReply(comment, parent);

        assertTrue(updatedParent.containsReply(comment));
        assertNotNull(this.commentDao.findOneThatHasReply(comment));
    }

    @Test
    public void shouldDeleteAComment() {
        Comment comment = this.getCommentByIndex(2);

        this.commentDao.delete(comment);

        assertNull(this.commentDao.findOneThatHasReply(comment));
        assertNull(this.commentDao.findOne(comment.getId()));
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
    public void setAdminUserProperties(AdminUserProperties adminUserProperties) {
        this.adminUserProperties = adminUserProperties;
    }

    private Comment saveComment(User author, String content) {
        Comment comment = new Comment(author, content);
        this.commentDao.save(comment);
        return comment;
    }

    private Comment getCommentByIndex(final int index) {
        return this.commentDao.findAll().get(index);
    }

    private Comment addReply(final Comment comment, Comment parent) {
        List<Comment> replies = parent.getReplies();
        replies.add(comment);
        parent.setReplies(replies);
        return this.commentDao.save(parent);
    }

}
