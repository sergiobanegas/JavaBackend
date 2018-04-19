package springskeleton.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import springskeleton.controller.wrapper.request.ChangePasswordRequest;
import springskeleton.util.AuthUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.multipart.MultipartFile;

import springskeleton.config.Endpoints;
import springskeleton.controller.exception.InvalidDataException;
import springskeleton.controller.exception.ServerErrorException;
import springskeleton.controller.wrapper.request.UserPatchRequest;
import springskeleton.controller.wrapper.response.AccountResponse;
import springskeleton.controller.wrapper.response.ApiResponse;
import springskeleton.model.User;
import springskeleton.service.AccountService;
import springskeleton.util.I18n;

@RestController
@RequestMapping(Endpoints.ACCOUNT)
public class AccountController {

    private AccountService accountService;

    private I18n i18n;

    private AuthUtils authUtils;

    @Autowired
    public AccountController(AccountService accountService, I18n i18n, AuthUtils authUtils) {
        this.accountService = accountService;
        this.i18n = i18n;
        this.authUtils = authUtils;
    }

    @GetMapping
    public AccountResponse get(final HttpServletRequest request) {
        final Long userId = this.authUtils.getUserId(request);
        return new AccountResponse(this.accountService.getAccount(userId));
    }

    @DeleteMapping
    public ApiResponse deleteRequest(final HttpServletRequest request) throws ServerErrorException {
        final Long userId = this.authUtils.getUserId(request);
        this.accountService.deleteRequest(userId);
        return ApiResponse.builder().ok().message(this.i18n.get("delete.account.email.sent")).build();
    }

    @DeleteMapping(Endpoints.TOKEN)
    public ApiResponse delete(@PathVariable final String token, final HttpServletRequest request, final HttpServletResponse response) throws InvalidDataException, ServerErrorException {
        this.accountService.delete(token, request, response);
        return ApiResponse.builder().ok().message(this.i18n.get("account.deleted")).build();
    }

    @PostMapping(Endpoints.AVATAR)
    public AccountResponse handleFileUpload(@RequestParam("file") final MultipartFile file, final HttpServletRequest request)
            throws InvalidDataException, ServerErrorException {
        final Long userId = this.authUtils.getUserId(request);
        final User user = this.accountService.updateAvatar(file, userId);
        return new AccountResponse(user);
    }

    @PatchMapping
    public AccountResponse update(@RequestBody @Valid final UserPatchRequest newData, final HttpServletRequest request, HttpServletResponse response)
            throws ServerErrorException, InvalidDataException {
        final Long userId = this.authUtils.getUserId(request);
        final User updated = this.accountService.update(userId, newData, request, response);
        return new AccountResponse(updated);
    }

    @PutMapping(Endpoints.PASSWORD)
    public ApiResponse changePassword(@RequestBody @Valid final ChangePasswordRequest changePasswordRequest, final HttpServletRequest request)
            throws InvalidDataException {
        final Long userId = this.authUtils.getUserId(request);
        this.accountService.changePassword(userId, changePasswordRequest);
        return ApiResponse.builder().ok().message(this.i18n.get("password.changed")).build();
    }

    @PutMapping(Endpoints.EMAIL + Endpoints.TOKEN)
    public ApiResponse updateEmail(@PathVariable final String token, final HttpServletRequest request)
            throws ServerErrorException, InvalidDataException {
        final Long userId = this.authUtils.getUserId(request);
        final String newEmail = this.accountService.changeEmail(userId, token);
        return ApiResponse.builder().ok().message(this.i18n.get("email.changed", new Object[]{newEmail})).build();
    }

}
