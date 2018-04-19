package springskeleton.controller;

import com.google.gson.Gson;
import springskeleton.config.Endpoints;
import springskeleton.config.property.JWTProperties;
import springskeleton.controller.wrapper.request.NewCommentRequest;
import springskeleton.controller.wrapper.response.CommentResponse;
import springskeleton.dao.CommentDao;
import springskeleton.model.Comment;
import springskeleton.util.ControllerTest;
import springskeleton.util.RequestData;
import springskeleton.util.ResponseData;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;

import javax.servlet.http.Cookie;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

public class CommentControllerIT extends ControllerTest {

    private CommentDao commentDao;

    private JWTProperties jwtConfig;

    @Before
    public void init() {
        super.setUpHttpUtils();
    }

    @Test
    public void shouldReturnAnExistingComment() throws Exception {
        final ResponseData responseLogin = super.httpUtils.loginWithAdmin();
        final Cookie authCookie = responseLogin.getCookie(this.jwtConfig.getCookieName());
        Comment comment = this.commentDao.findAll().get(0);

        final ResponseData response = this.getComment(authCookie, comment.getId());

        assertTrue(response.isUnauthorized());
        assertEquals(response.getContent(), new Gson().toJson(new CommentResponse(comment)));
    }

    @Test
    public void shouldThrowAnExceptionWhenSearchForNonExistentComment() throws Exception {
        final ResponseData responseLogin = super.httpUtils.loginWithAdmin();
        final Cookie authCookie = responseLogin.getCookie(this.jwtConfig.getCookieName());

        final ResponseData response = this.getComment(authCookie, 1234L);

        assertTrue(response.isNotFound());
    }

    @Test
    public void shouldCreateANewComment() throws Exception {
        final String content = "new content";
        final ResponseData responseLogin = super.httpUtils.loginWithAdmin();
        final Cookie authCookie = responseLogin.getCookie(this.jwtConfig.getCookieName());
        final NewCommentRequest newCommentRequest = this.buildNewCommentRequest(content);
        RequestData requestData = new RequestData(Endpoints.COMMENTS);
        requestData.setContentType(MediaType.APPLICATION_JSON);
        requestData.addCookie(authCookie);
        requestData.setContent(newCommentRequest);

        final ResponseData response = super.httpUtils.postRequest(requestData);

        assertTrue(response.isCreated());
        final Comment createdComment = this.getCommentByIndex(((int) this.commentDao.count()) - 1);
        assertEquals(createdComment.getContent(), content);
        assertEquals(createdComment.getAuthor().getEmail(), super.adminUserProperties.getEmail());
    }

    @Test
    public void shouldUpdateAComment() throws Exception {
        Comment comment = this.getCommentByIndex(0);
        final String content = "update content";
        final ResponseData responseLogin = super.httpUtils.loginWithCredentials(comment.getAuthor().getEmail(), "1234");
        final Cookie authCookie = responseLogin.getCookie(this.jwtConfig.getCookieName());
        NewCommentRequest newCommentRequest = this.buildNewCommentRequest(content);

        final ResponseData response = this.updateComment(authCookie, comment.getId(), newCommentRequest);

        Comment updatedComment = this.commentDao.findOne(comment.getId());
        assertTrue(response.isOk());
        assertEquals(updatedComment.getContent(), content);
    }

    @Test
    public void shouldThrowAnExceptionWhenNotAuthorUpdatesAComment() throws Exception {
        Comment comment = this.getCommentByIndex(0);
        final ResponseData responseLogin = super.httpUtils.loginWithCredentials("test2@test.com", "1234");
        final Cookie authCookie = responseLogin.getCookie(this.jwtConfig.getCookieName());
        NewCommentRequest newCommentRequest = this.buildNewCommentRequest("update content");

        final ResponseData response = this.updateComment(authCookie, comment.getId(), newCommentRequest);

        assertTrue(response.isUnauthorized());
    }

    @Test
    public void shouldAddANewReply() throws Exception {
        final ResponseData responseLogin = super.httpUtils.loginWithAdmin();
        final Cookie authCookie = responseLogin.getCookie(this.jwtConfig.getCookieName());
        Comment comment = this.getCommentByIndex(0);
        NewCommentRequest newCommentRequest = this.buildNewCommentRequest("reply content");

        final ResponseData response = this.addReply(authCookie, newCommentRequest, comment.getId());

        Comment createdComment = this.commentDao.findAll().get(((int) this.commentDao.count()) - 1);
        Comment parentUpdated = this.commentDao.findOne(comment.getId());
        assertTrue(response.isCreated());
        assertNotNull(createdComment);
        assertTrue(parentUpdated.getReplies().contains(createdComment));
    }


    @Test
    public void shouldThrowAnExceptionWhenAddReplyToNonExistentComment() throws Exception {
        final ResponseData responseLogin = super.httpUtils.loginWithAdmin();
        final Cookie authCookie = responseLogin.getCookie(this.jwtConfig.getCookieName());
        NewCommentRequest newCommentRequest = this.buildNewCommentRequest("reply content");

        final ResponseData response = this.addReply(authCookie, newCommentRequest, 1234L);

        assertTrue(response.isNotFound());

    }

    @Test
    public void shouldDeleteAComment() throws Exception {
        Comment comment = this.getCommentByIndex(3);
        final ResponseData responseLogin = super.httpUtils.loginWithCredentials(comment.getAuthor().getEmail(), "1234");
        final Cookie authCookie = responseLogin.getCookie(this.jwtConfig.getCookieName());

        final ResponseData response = this.deleteComment(authCookie, comment.getId());

        assertTrue(response.isOk());
        assertNull(this.commentDao.findOne(comment.getId()));
    }

    @Test
    public void shouldThrowAnExceptionWhenDeletingNonExistentComment() throws Exception {
        final ResponseData responseLogin = super.httpUtils.loginWithAdmin();
        final Cookie authCookie = responseLogin.getCookie(this.jwtConfig.getCookieName());

        final ResponseData response = this.deleteComment(authCookie, 1234L);

        assertTrue(response.isNotFound());
    }

    @Test
    public void shouldThrowAnExceptionWhenNotAuthorDeletesAComment() throws Exception {
        Comment comment = this.getCommentByIndex(1);
        final ResponseData responseLogin = super.httpUtils.loginWithCredentials("test1@test.com", "1234");
        final Cookie authCookie = responseLogin.getCookie(this.jwtConfig.getCookieName());

        final ResponseData response = this.deleteComment(authCookie, comment.getId());

        assertTrue(response.isUnauthorized());
    }

    @Autowired
    public void setCommentDao(CommentDao commentDao) {
        this.commentDao = commentDao;
    }

    @Autowired
    public void setJwtConfig(JWTProperties jwtConfig) {
        this.jwtConfig = jwtConfig;
    }

    private ResponseData getComment(final Cookie authCookie, final Long commentId) throws Exception {
        RequestData requestData = new RequestData(Endpoints.COMMENTS + "/" + commentId);
        requestData.setContentType(MediaType.APPLICATION_JSON);
        requestData.addCookie(authCookie);
        return super.httpUtils.getRequest(requestData);
    }

    private NewCommentRequest buildNewCommentRequest(final String content) {
        NewCommentRequest newCommentRequest = new NewCommentRequest();
        newCommentRequest.setContent(content);
        return newCommentRequest;
    }

    private Comment getCommentByIndex(final int index) {
        return this.commentDao.findAll().get(index);
    }

    private ResponseData addReply(final Cookie authCookie, final NewCommentRequest newCommentRequest, final Long commentId) throws Exception {
        RequestData requestData = new RequestData(Endpoints.COMMENTS + "/" + commentId + Endpoints.REPLIES);
        requestData.setContentType(MediaType.APPLICATION_JSON);
        requestData.addCookie(authCookie);
        requestData.setContent(newCommentRequest);
        return super.httpUtils.postRequest(requestData);
    }

    private ResponseData updateComment(final Cookie authCookie, final Long commentId, final NewCommentRequest newCommentRequest) throws Exception {
        RequestData requestData = new RequestData(Endpoints.COMMENTS + "/" + commentId);
        requestData.setContentType(MediaType.APPLICATION_JSON);
        requestData.addCookie(authCookie);
        requestData.setContent(newCommentRequest);
        return super.httpUtils.putRequest(requestData);
    }

    private ResponseData deleteComment(final Cookie authCookie, final Long commentId) throws Exception {
        RequestData requestData = new RequestData(Endpoints.COMMENTS + "/" + commentId);
        requestData.setContentType(MediaType.APPLICATION_JSON);
        requestData.addCookie(authCookie);
        return super.httpUtils.deleteRequest(requestData);
    }

}
