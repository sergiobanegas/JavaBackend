package springskeleton.util;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.util.WebUtils;

import springskeleton.config.property.CookiesProperties;

@Component
public class CookiesUtils {

    private final CookiesProperties config;

    @Autowired
    public CookiesUtils(CookiesProperties config) {
        this.config = config;
    }

    public void addCookieToResponse(HttpServletResponse response, final String cookieName, final String value) {
        Cookie cookie = this.getCookieBuilder(cookieName, value).build();
        response.addCookie(cookie);
    }

    public void addHttpOnlyCookieToResponse(HttpServletResponse response, final String cookieName, final String value) {
        Cookie cookie = this.getCookieBuilder(cookieName, value).httpOnly().build();
        response.addCookie(cookie);
    }

    public void addPersistentCookieToResponse(HttpServletResponse response, final String cookieName, final String value) {
        Cookie cookie = this.getCookieBuilder(cookieName, value).maxAge(this.config.getDefaultMaxAge()).build();
        response.addCookie(cookie);
    }

    public void addHttpOnlyPersistentCookieToResponse(HttpServletResponse response, final String cookieName, final String value) {
        Cookie cookie = this.getCookieBuilder(cookieName, value).maxAge(this.config.getDefaultMaxAge()).httpOnly().build();
        response.addCookie(cookie);
    }

    private CookieBuilder getCookieBuilder(final String name, final String value) {
        return new CookieBuilder().create(name, value).path(this.config.getDefaultPath());
    }

    public boolean requestHasCookie(final HttpServletRequest request, final String name) {
        return this.getCookieFromRequest(request, name) != null;
    }

    public Cookie getCookieFromRequest(final HttpServletRequest request, final String name) {
        return WebUtils.getCookie(request, name);
    }

    public void removeCookie(HttpServletRequest request, HttpServletResponse response, final String name) {
        Cookie cookie = this.getCookieFromRequest(request, name);
        if (cookie != null) {
            cookie.setMaxAge(0);
            cookie.setPath(this.config.getDefaultPath());
            response.addCookie(cookie);
        }
    }

}
