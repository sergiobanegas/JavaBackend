package springskeleton.controller;

import javax.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
import springskeleton.controller.exception.ServerErrorException;
import springskeleton.controller.wrapper.request.ResetPasswordRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import springskeleton.config.Endpoints;
import springskeleton.controller.exception.InvalidDataException;
import springskeleton.controller.wrapper.request.EmailRequest;
import springskeleton.controller.wrapper.request.UserLoginRequest;
import springskeleton.controller.wrapper.request.UserSignUpRequest;
import springskeleton.controller.wrapper.response.ApiResponse;
import springskeleton.service.AuthService;
import springskeleton.util.I18n;

import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping(Endpoints.AUTH)
public class AuthController {

    private AuthService authService;

    private I18n i18n;

    @Autowired
    public AuthController(AuthService authService, I18n i18n) {
        this.authService = authService;
        this.i18n = i18n;
    }

    @ApiOperation("Sign up")
    @PostMapping(Endpoints.SIGN_UP)
    public ApiResponse signUp(@RequestBody @Valid final UserSignUpRequest user) throws InvalidDataException {
        this.authService.signUp(user);
        return ApiResponse.builder().ok()
                .message(this.i18n.get("signup.welcome.user", new Object[]{user.getName(), user.getEmail()}))
                .build();
    }

    @ApiOperation("Send confirmation mail")
    @PostMapping(Endpoints.CONFIRMATION)
    public ApiResponse sendConfirmationMail(@RequestBody @Valid final EmailRequest emailRequest)
            throws InvalidDataException, ServerErrorException {
        this.authService.sendConfirmationEmail(emailRequest.getEmail());
        return ApiResponse.builder().ok()
                .message(this.i18n.get("confirmation.mail.sent", new Object[]{emailRequest.getEmail()})).build();
    }

    @ApiOperation("Register confirmation")
    @PostMapping(Endpoints.CONFIRMATION + Endpoints.TOKEN)
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse confirmation(@PathVariable final String token) throws InvalidDataException, ServerErrorException {
        this.authService.confirm(token);
        return ApiResponse.builder().created().message(this.i18n.get("account.validated")).build();
    }

    @ApiOperation("Login")
    @PostMapping(Endpoints.LOGIN)
    public void login(@RequestBody final UserLoginRequest body) {
        throw new IllegalStateException(
                "This method shouldn't be called. It's implemented by Spring Security filters.");
    }

    @ApiOperation("Logout")
    @PostMapping(Endpoints.LOGOUT)
    public void logout() {
        throw new IllegalStateException(
                "This method shouldn't be called. It's implemented by Spring Security filters.");
    }

    @ApiOperation("Reset password request")
    @PostMapping(Endpoints.RESET_PASSWORD)
    public ApiResponse resetPasswordRequest(@RequestBody @Valid final EmailRequest emailRequest) throws ServerErrorException {
        this.authService.resetPasswordRequest(emailRequest);
        return ApiResponse.builder().ok().message(this.i18n.get("password.reset.email.sent")).build();
    }

    @ApiOperation("Reset password")
    @PostMapping(Endpoints.RESET_PASSWORD + Endpoints.TOKEN)
    public ApiResponse resetPassword(@PathVariable final String token, @RequestBody @Valid final ResetPasswordRequest resetPasswordRequest) throws InvalidDataException {
        this.authService.resetPassword(token, resetPasswordRequest);
        return ApiResponse.builder().ok().message(this.i18n.get("password.successfully.reset")).build();
    }

}
