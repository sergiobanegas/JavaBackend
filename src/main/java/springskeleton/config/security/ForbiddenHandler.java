package springskeleton.config.security;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;

import springskeleton.controller.wrapper.response.ApiResponse;
import springskeleton.util.I18n;

public class ForbiddenHandler implements AccessDeniedHandler {

    private I18n i18n;

    @Override
    public void handle(final HttpServletRequest request, HttpServletResponse response, final AccessDeniedException exc) {
        new ResponseUtils().generateResponse(response,
                ApiResponse.builder().forbidden().message(this.i18n.get("access.denied")).build());
    }

    @Autowired
    public void setI18n(I18n i18n) {
        this.i18n = i18n;
    }
}
