package springskeleton.controller;

import springskeleton.controller.wrapper.response.CommentResponse;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.util.NestedServletException;

import com.google.gson.Gson;
import springskeleton.config.Endpoints;
import springskeleton.model.Comment;
import springskeleton.model.Gender;
import springskeleton.model.User;
import springskeleton.service.CommentService;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(MockitoJUnitRunner.class)
public class CommentControllerUT {

    @Mock
    private CommentService commentService;

    @InjectMocks
    private CommentController commentController;

    private MockMvc mockMvc;

    private final Gson gson = new Gson();

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);
        this.mockMvc = MockMvcBuilders.standaloneSetup(commentController).build();
    }

    @Test
    public void shouldSearchForOneComment() throws Exception {
        final long userId = Mockito.anyLong();
        User user = this.buildUser(userId);
        final CommentResponse result = this.mockComment(userId, user);
        final String expectedResult = this.gson.toJson(result);

        this.mockMvc.perform(get(Endpoints.COMMENTS + "/" + userId)).andExpect(status().isOk())
                .andExpect(content().json(expectedResult));
    }

    @Test(expected = NestedServletException.class)
    public void shouldThrowAnErrorWhenWrongIdIsGiven() throws Exception {
        final Long id = Mockito.anyLong();
        when(this.commentService.findOne(id)).thenReturn(null);

        this.mockMvc.perform(get(Endpoints.COMMENTS + "/" + id));
    }

    private CommentResponse mockComment(long userId, User user) {
        final Comment comment = new Comment(user, "comment");
        when(this.commentService.findOne(userId)).thenReturn(comment);
        return new CommentResponse(comment);
    }

    private User buildUser(long userId) {
        User user = new User("email", "pwd", "name", Gender.MALE);
        user.setAvatar("avatar");
        user.setId(userId);
        return user;
    }

}
