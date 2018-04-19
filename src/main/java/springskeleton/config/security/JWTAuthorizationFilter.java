package springskeleton.config.security;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import springskeleton.util.AuthUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import springskeleton.controller.wrapper.response.ApiResponse;
import springskeleton.util.I18n;
import org.springframework.web.servlet.LocaleResolver;

public class JWTAuthorizationFilter extends BasicAuthenticationFilter {

    private AuthUtils authUtils;

    private I18n i18n;

    private LocaleResolver localeResolver;

    private ServerProperties serverProperties;

    public JWTAuthorizationFilter(final AuthenticationManager authManager) {
        super(authManager);
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

    @Autowired
    public void setServerProperties(ServerProperties serverProperties) {
        this.serverProperties = serverProperties;
    }

    @Override
    protected void doFilterInternal(final HttpServletRequest request, final HttpServletResponse response, final FilterChain chain)
            throws IOException, ServletException {
        LocaleContextHolder.setLocale(this.localeResolver.resolveLocale(request));
        if (!this.authUtils.requestHasAuthCookie(request)) {
            if (this.isProtectedURI(request.getRequestURI(), request.getMethod())) {
                this.throwUnauthorizedError(response);
            } else {
                chain.doFilter(request, response);
            }
        } else {
            final UsernamePasswordAuthenticationToken authentication = this.authUtils.getAuthentication(request);
            if (authentication == null) {
                this.throwUnauthorizedError(response);
            } else {
                SecurityContextHolder.getContext().setAuthentication(authentication);
                chain.doFilter(request, response);
            }
        }
    }

    private void throwUnauthorizedError(HttpServletResponse response) {
        new ResponseUtils().generateResponse(response,
                ApiResponse.builder().unauthorized().message(this.i18n.get("unauthorized")).build());
    }

    private boolean isProtectedURI(final String uri, final String httpMethod) {
        boolean isProtected = true;
        Iterator iterator = WebSecurity.NON_PROTECTED_URIS.entrySet().iterator();
        while (iterator.hasNext() && isProtected) {
            Map.Entry unprotectedUri = (Map.Entry) iterator.next();
            if (this.urisMatch(uri, (String) unprotectedUri.getKey()) && (unprotectedUri.getValue()).equals(HttpMethod.valueOf(httpMethod))) {
                isProtected = false;
            }
        }
        return isProtected;
    }

    private boolean urisMatch(final String uri, final String protectedUri) {
        final String replacedUri = uri.replace(serverProperties.getContextPath(), "");
        return replacedUri.startsWith(protectedUri);
    }

}