package springskeleton.config.security;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import springskeleton.controller.exception.ServerErrorException;
import springskeleton.util.AuthUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.google.gson.Gson;
import springskeleton.config.Endpoints;
import springskeleton.controller.wrapper.request.UserLoginRequest;
import springskeleton.controller.wrapper.response.ApiResponse;
import springskeleton.util.I18n;

import io.jsonwebtoken.Claims;
import org.springframework.web.servlet.LocaleResolver;

public class JWTAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private AuthUtils authUtils;

    private I18n i18n;

    private LocaleResolver localeResolver;

    public JWTAuthenticationFilter() {
        this.setFilterProcessesUrl(Endpoints.AUTH + Endpoints.LOGIN);
    }

    @Override
    public Authentication attemptAuthentication(final HttpServletRequest request, final HttpServletResponse response)
            throws AuthenticationException {
        LocaleContextHolder.setLocale(this.localeResolver.resolveLocale(request));
        UserLoginRequest userCredentials = this.buildUserCredentials(request, response);
        UsernamePasswordAuthenticationToken auth = this.buildAuthentication(userCredentials);
        return this.getAuthenticationManager().authenticate(auth);
    }

    @Autowired
    public void setAuthUtils(AuthUtils authUtils) {
        this.authUtils = authUtils;
    }

    @Autowired
    public void setI18n(I18n i18n) {
        this.i18n = i18n;
    }

    @Autowired
    public void setLocaleResolver(LocaleResolver localeResolver) {
        this.localeResolver = localeResolver;
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response,
                                              AuthenticationException exception) {
        new ResponseUtils().generateResponse(response,
                ApiResponse.builder().badRequest().message(exception.getMessage()).build());
    }

    @Override
    protected void successfulAuthentication(final HttpServletRequest request, HttpServletResponse response, final FilterChain chain,
                                            final Authentication auth) throws ServerErrorException {
        final Claims claims = this.authUtils.generateClaimsFromAuth(auth);
        final CurrentUser user = (CurrentUser) auth.getPrincipal();
        final UserLoginRequest userCredentials = (UserLoginRequest) auth.getDetails();
        final String token = this.authUtils.generateToken(user.getUsername(), claims);
        this.authUtils.addAuthCookieToResponse(response, token, userCredentials.isRemember());
        this.setLanguage(user);
        this.authUtils.addUserInfoCookieToResponse(response, auth, userCredentials.isRemember());
        new ResponseUtils().generateResponse(response, ApiResponse.builder().ok()
                .message(this.i18n.get("welcome.user", new Object[]{user.getName()})).build());
    }

    private UserLoginRequest buildUserCredentials(HttpServletRequest request, HttpServletResponse response) {
        try {
            BufferedReader reader = request.getReader();
            final Gson gson = new Gson();
            UserLoginRequest userCredentials = gson.fromJson(reader, UserLoginRequest.class);
            reader.close();
            return userCredentials;
        } catch (IOException e) {
            new ResponseUtils().generateResponse(response,
                    ApiResponse.builder().badRequest().message(this.i18n.get("wrong.json.format")).build());
        }
        return null;
    }

    private UsernamePasswordAuthenticationToken buildAuthentication(final UserLoginRequest userCredentials) {
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(userCredentials.getEmail(),
                userCredentials.getPassword(), new ArrayList<>());
        auth.setDetails(userCredentials);
        return auth;
    }

    private void setLanguage(final CurrentUser user) {
        if (user.getLanguage() != null) {
            LocaleContextHolder.setLocale(Locale.forLanguageTag(user.getLanguage().toString()));
        }
    }

}
