package springskeleton.config.security;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import springskeleton.util.AuthUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.stereotype.Component;

import springskeleton.controller.wrapper.response.ApiResponse;
import springskeleton.util.I18n;
import org.springframework.web.servlet.LocaleResolver;

@Component
public class LogoutHandler implements LogoutSuccessHandler {

    private final I18n i18n;

    private final AuthUtils authUtils;

    private final LocaleResolver localeResolver;

    @Autowired
    public LogoutHandler(I18n i18n, AuthUtils authUtils, LocaleResolver localeResolver) {
        this.i18n = i18n;
        this.authUtils = authUtils;
        this.localeResolver = localeResolver;
    }

    @Override
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, final Authentication authentication) {
        LocaleContextHolder.setLocale(this.localeResolver.resolveLocale(request));
        if (this.authUtils.requestHasAuthCookie(request)) {
            this.authUtils.removeAuthCookie(request, response);
            this.authUtils.removeUserInfoCookie(request, response);
            new ResponseUtils().generateResponse(response,
                    ApiResponse.builder().ok().message(this.i18n.get("logout.message")).build());
        } else {
            new ResponseUtils().generateResponse(response,
                    ApiResponse.builder().badRequest().message(this.i18n.get("already.logged.out")).build());
        }
    }

}
