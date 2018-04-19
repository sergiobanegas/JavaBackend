package springskeleton.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import springskeleton.controller.exception.InvalidDataException;
import springskeleton.controller.exception.NotFoundException;
import springskeleton.controller.exception.ServerErrorException;
import springskeleton.controller.exception.UnauthorizedException;
import springskeleton.dao.CommentDao;
import springskeleton.dao.UserDao;
import springskeleton.model.Comment;
import springskeleton.model.User;
import springskeleton.util.I18n;

@Service
public class CommentService {

    private CommentDao commentDao;

    private UserDao userDao;

    private I18n i18n;

    @Autowired
    public CommentService(CommentDao commentDao, UserDao userDao, I18n i18n) {
        this.commentDao = commentDao;
        this.userDao = userDao;
        this.i18n = i18n;
    }

    public List<Comment> getAllComments() {
        return this.commentDao.findAll();
    }

    public Comment findOne(final Long id) {
        final Comment comment = this.commentDao.findOne(id);
        this.checkIfCommentExists(comment);
        return comment;
    }

    public Comment addReply(final Long parentId, final String content, final Long userId) throws NotFoundException, InvalidDataException {
        Comment parent = this.commentDao.findOne(parentId);
        this.checkIfParentCommentExists(parent);
        final Comment comment = this.save(userId, content);
        this.checkIfCommentExists(comment);
        parent.addReply(comment);
        this.commentDao.save(parent);
        return parent.containsReply(comment) ? comment : null;
    }

    public Comment save(final Long authorId, final String content) throws ServerErrorException {
        final User author = this.userDao.findOne(authorId);
        return this.commentDao.save(new Comment(author, content));
    }

    public Comment update(final Long userId, final Long commentId, final String content) throws NotFoundException, UnauthorizedException {
        Comment comment = this.commentDao.findOne(commentId);
        this.checkIfCommentExists(comment);
        this.checkIfCommentsHasAuthor(userId, comment);
        comment.setContent(content);
        return this.commentDao.save(comment);
    }

    public void delete(final Long userId, final Long commentId)
            throws NotFoundException, UnauthorizedException {
        Comment comment = this.commentDao.findOne(commentId);
        this.checkIfCommentExists(comment);
        this.checkIfCommentsHasAuthor(userId, comment);
        Comment parent = this.commentDao.findOneThatHasReply(comment);
        this.removeReply(comment, parent);
        this.commentDao.delete(comment);
    }

    private void removeReply(final Comment comment, final Comment parent) {
        if (parent != null) {
            parent.removeReply(comment);
            this.commentDao.save(parent);
        }
        comment.getReplies().clear();
    }

    private void checkIfCommentExists(final Comment comment) throws NotFoundException {
        if (comment == null) {
            throw new NotFoundException(this.i18n.get("comment.not.exists"));
        }
    }

    private void checkIfParentCommentExists(final Comment parent) throws NotFoundException {
        if (parent == null) {
            throw new NotFoundException(this.i18n.get("parent.comment.not.exists"));
        }
    }

    private void checkIfCommentsHasAuthor(final Long userId, final Comment comment) throws UnauthorizedException {
        if (!comment.hasAuthor(userId)) {
            throw new UnauthorizedException(this.i18n.get("not.author.of.comment"));
        }
    }

}
