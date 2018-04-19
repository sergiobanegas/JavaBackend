package springskeleton.controller;

import springskeleton.config.Endpoints;
import springskeleton.controller.exception.InvalidDataException;
import springskeleton.controller.exception.NotFoundException;
import springskeleton.controller.wrapper.response.ApiResponse;
import springskeleton.controller.wrapper.response.UserListItemResponse;
import springskeleton.controller.wrapper.response.UserResponse;
import springskeleton.model.User;
import springskeleton.service.UserService;
import springskeleton.util.AuthUtils;
import springskeleton.util.I18n;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(Endpoints.USERS)
public class UserController {

    private UserService userService;

    private AuthUtils authUtils;

    private I18n i18n;

    @Autowired
    public UserController(UserService userService, AuthUtils authUtils, I18n i18n) {
        this.userService = userService;
        this.i18n = i18n;
        this.authUtils = authUtils;
    }

    @ApiOperation("Get list of users paginated")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", dataType = "integer", paramType = "query",
                    value = "Results page you want to retrieve (0..N)"),
            @ApiImplicitParam(name = "size", dataType = "integer", paramType = "query",
                    value = "Number of records per page."),
            @ApiImplicitParam(name = "sort", allowMultiple = true, dataType = "string", paramType = "query",
                    value = "Sorting criteria in the format: property(,asc|desc). " +
                            "Default sort order is ascending. " +
                            "Multiple sort criteria are supported.")
    })
    @GetMapping
    public Page<UserListItemResponse> get(@ApiParam final Pageable pageable, @RequestParam(required = false) final String email, final HttpServletRequest request) {
        final Page<User> usersPaginated = this.userService.getUsersPaginated(pageable, this.authUtils.getUserId(request), email);
        final List<UserListItemResponse> usersWrapper = usersPaginated.getContent().stream().map(UserListItemResponse::new).collect(Collectors.toList());
        return new PageImpl<>(usersWrapper, pageable, usersPaginated.getTotalElements());
    }

    @GetMapping(Endpoints.ID)
    public UserResponse getUser(@PathVariable final long id, final HttpServletRequest request) throws NotFoundException, InvalidDataException {
        return new UserResponse(this.userService.getUser(this.authUtils.getUserId(request), id));
    }

    @DeleteMapping(Endpoints.ID)
    public ApiResponse delete(@PathVariable final long id, final HttpServletRequest request) throws NotFoundException, InvalidDataException {
        this.userService.deleteUser(this.authUtils.getUserId(request), id);
        return ApiResponse.builder().ok().message(this.i18n.get("user.deleted")).build();
    }

}
