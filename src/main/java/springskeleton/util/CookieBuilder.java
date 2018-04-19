package springskeleton.util;

import javax.servlet.http.Cookie;

public class CookieBuilder {

    private String name;

    private String value;

    private int maxAge = -1;

    private String path;

    private boolean httpOnly;

    public CookieBuilder create(String name, String value) {
        this.name = name;
        this.value = value;
        return this;
    }

    public CookieBuilder maxAge(int maxAge) {
        this.maxAge = maxAge;
        return this;
    }

    public CookieBuilder path(String path) {
        this.path = path;
        return this;
    }

    public CookieBuilder httpOnly() {
        this.httpOnly = true;
        return this;
    }

    public Cookie build() {
        Cookie cookie = new Cookie(this.name, this.value);
        cookie.setMaxAge(this.maxAge);
        cookie.setPath(this.path);
        cookie.setHttpOnly(this.httpOnly);
        return cookie;
    }


}
