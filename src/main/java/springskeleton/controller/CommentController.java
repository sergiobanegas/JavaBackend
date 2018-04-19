package springskeleton.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import springskeleton.util.AuthUtils;
import org.springframework.beans.factory.annotation.Autowired;

import springskeleton.config.Endpoints;
import springskeleton.controller.exception.InvalidDataException;
import springskeleton.controller.exception.NotFoundException;
import springskeleton.controller.exception.ServerErrorException;
import springskeleton.controller.exception.UnauthorizedException;
import springskeleton.controller.wrapper.request.NewCommentRequest;
import springskeleton.controller.wrapper.response.ApiResponse;
import springskeleton.controller.wrapper.response.CommentResponse;
import springskeleton.model.Comment;
import springskeleton.service.CommentService;
import springskeleton.util.I18n;

@RestController
@RequestMapping(Endpoints.COMMENTS)
public class CommentController {

    private CommentService commentService;

    private I18n i18n;

    private AuthUtils authUtils;

    @Autowired
    public CommentController(CommentService commentService, I18n i18n, AuthUtils authUtils) {
        this.commentService = commentService;
        this.i18n = i18n;
        this.authUtils = authUtils;
    }

    @GetMapping
    public List<Comment> getAll() {
        return this.commentService.getAllComments();
    }

    @GetMapping(Endpoints.ID)
    public CommentResponse findOne(@PathVariable final Long id) throws NotFoundException {
        Comment comment = this.commentService.findOne(id);
        return new CommentResponse(comment);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CommentResponse save(@RequestBody @Valid final NewCommentRequest newCommentRequest, final HttpServletRequest request)
            throws ServerErrorException {
        final Long userId = this.authUtils.getUserId(request);
        final Comment comment = this.commentService.save(userId, newCommentRequest.getContent());
        return new CommentResponse(comment);
    }

    @PostMapping(Endpoints.ID + Endpoints.REPLIES)
    @ResponseStatus(HttpStatus.CREATED)
    public CommentResponse saveReply(@PathVariable final Long id, @RequestBody @Valid final NewCommentRequest newCommentRequest,
                                     final HttpServletRequest request) throws NotFoundException, InvalidDataException {
        final Long userId = this.authUtils.getUserId(request);
        final Comment comment = this.commentService.addReply(id, newCommentRequest.getContent(), userId);
        return new CommentResponse(comment);
    }

    @PutMapping(Endpoints.ID)
    public CommentResponse update(@PathVariable final Long id, @RequestBody @Valid final NewCommentRequest newCommentRequest,
                                  final HttpServletRequest request) throws NotFoundException, UnauthorizedException {
        final Long userId = this.authUtils.getUserId(request);
        final Comment updatedComment = this.commentService.update(userId, id, newCommentRequest.getContent());
        return new CommentResponse(updatedComment);
    }

    @DeleteMapping(Endpoints.ID)
    public ApiResponse delete(@PathVariable final Long id, final HttpServletRequest request)
            throws NotFoundException, UnauthorizedException {
        final Long userId = this.authUtils.getUserId(request);
        this.commentService.delete(userId, id);
        return ApiResponse.builder().ok().message(this.i18n.get("comment.deleted")).build();
    }

}
