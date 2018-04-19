package springskeleton.util;

import springskeleton.config.property.JWTProperties;
import springskeleton.config.security.CurrentUser;
import springskeleton.controller.exception.ServerErrorException;
import springskeleton.controller.wrapper.request.UserLoginRequest;
import springskeleton.model.Language;
import springskeleton.model.Role;
import org.springframework.security.core.userdetails.User;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.codehaus.jettison.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@Component
public class AuthUtils {

    private final JWTProperties config;

    private final CookiesUtils cookiesUtils;

    @Autowired
    public AuthUtils(JWTProperties config, CookiesUtils cookiesUtils) {
        this.config = config;
        this.cookiesUtils = cookiesUtils;
    }

    public String generateToken(final String username, final Claims claims) {
        return Jwts.builder().setSubject(username).setClaims(claims)
                .signWith(SignatureAlgorithm.HS512, this.config.getSecret().getBytes()).compact();
    }

    public Claims generateClaimsFromAuth(final Authentication auth) {
        final UserLoginRequest userCredentials = (UserLoginRequest) auth.getDetails();
        final CurrentUser user = (CurrentUser) auth.getPrincipal();
        return this.buildClaims(((User) auth.getPrincipal()).getUsername(), userCredentials.isRemember(), auth, user.getLanguage());
    }

    public void addAuthCookieToResponse(HttpServletResponse response, final String token, final boolean remember) {
        if (remember) {
            this.cookiesUtils.addHttpOnlyPersistentCookieToResponse(response, this.config.getCookieName(), token);
        } else {
            this.cookiesUtils.addHttpOnlyCookieToResponse(response, this.config.getCookieName(), token);
        }
    }

    public boolean requestHasAuthCookie(final HttpServletRequest request) {
        return this.cookiesUtils.requestHasCookie(request, this.config.getCookieName());
    }

    public void removeAuthCookie(HttpServletRequest request, HttpServletResponse response) {
        this.cookiesUtils.removeCookie(request, response, this.config.getCookieName());
    }

    public void updateLanguageInCookies(final HttpServletRequest request, HttpServletResponse response, springskeleton.model.User user) throws ServerErrorException {
        final boolean isRemember = this.isAuthRemember(request);
        LocaleContextHolder.setLocale(Locale.forLanguageTag(user.getLanguage().toString()));
        this.addLanguageToUserInfoTokenToResponse(request, response, user, this.getAuthentication(request));
        Claims claims = this.buildClaims(user.getId().toString(), isRemember, this.getAuthentication(request), user.getLanguage());
        String newJwt = this.generateToken(user.getId().toString(), claims);
        this.addAuthCookieToResponse(response, newJwt, isRemember);
    }

    public Long getUserId(final HttpServletRequest request) {
        final Cookie authCookie = this.getAuthCookieFromRequest(request);
        final String jwt = authCookie.getValue();
        final Claims claims = this.getClaimsFromJWT(jwt);
        return Long.parseLong(claims.getSubject());
    }

    public UsernamePasswordAuthenticationToken getAuthentication(final HttpServletRequest request) {
        final Cookie authCookie = this.getAuthCookieFromRequest(request);
        if (authCookie != null) {
            final String jwt = authCookie.getValue();
            try {
                final Claims claims = this.getClaimsFromJWT(jwt);
                final String userId = claims.getSubject();
                if (userId != null) {
                    final List<GrantedAuthority> authorities = this.generateAuthorities(claims);
                    return new UsernamePasswordAuthenticationToken(userId, null, authorities);
                }
            } catch (Exception e) {
                return null;
            }
        }
        return null;
    }

    public String getUserLanguage(final HttpServletRequest request) {
        final Cookie authCookie = this.getAuthCookieFromRequest(request);
        if (authCookie != null) {
            Claims claims = this.getClaimsFromJWT(authCookie.getValue());
            return (String) claims.get(this.config.getLanguageClaim());
        }
        return null;
    }

    public void addUserInfoCookieToResponse(HttpServletResponse response, final Authentication auth, final boolean remember) throws ServerErrorException {
        String roles = this.buildRoleStringFromAuthentication(auth.getAuthorities());
        JSONObject info = new JSONObject();
        try {
            info.append(this.config.getRolesClaim(), roles);
            final Language language = ((CurrentUser) auth.getPrincipal()).getLanguage();
            if (language != null) {
                info.put(this.config.getLanguageClaim(), language);
            }
            String encodedInfo = Base64.getEncoder().encodeToString(info.toString().getBytes("utf-8"));
            this.addCookieToResponse(response, this.config.getUserInfoCookieName(), encodedInfo, remember);

        } catch (Exception e) {
            throw new ServerErrorException(e.getMessage());
        }

    }

    public void removeUserInfoCookie(final HttpServletRequest request, HttpServletResponse response) {
        this.cookiesUtils.removeCookie(request, response, this.config.getUserInfoCookieName());
    }

    private Claims buildClaims(final String subject, final boolean remember, final Authentication auth, final Language language) {
        Claims claims = Jwts.claims().setSubject(subject);
        claims.put(this.config.getRememberClaim(), remember);
        claims.put(this.config.getRolesClaim(),
                auth.getAuthorities().stream().map(Object::toString).collect(Collectors.toList()));
        if (language != null) {
            claims.put(this.config.getLanguageClaim(), language);
        }
        return claims;
    }

    private boolean isAuthRemember(final HttpServletRequest request) {
        Cookie authCookie = this.getAuthCookieFromRequest(request);
        final String jwt = authCookie.getValue();
        final Claims claims = this.getClaimsFromJWT(jwt);
        return (boolean) claims.get(this.config.getRememberClaim());
    }

    private Claims getClaimsFromJWT(final String jwt) {
        return Jwts.parser().setSigningKey(this.config.getSecret().getBytes()).parseClaimsJws(jwt).getBody();
    }

    private void addLanguageToUserInfoTokenToResponse(final HttpServletRequest request, HttpServletResponse response, final springskeleton.model.User user, final Authentication auth) throws ServerErrorException {
        String roles = this.buildRoleStringFromAuthentication(auth.getAuthorities());
        JSONObject info = new JSONObject();
        try {
            info.append(this.config.getRolesClaim(), roles);
            info.put(this.config.getLanguageClaim(), user.getLanguage().toString());
            String encodedInfo = Base64.getEncoder().encodeToString(info.toString().getBytes("utf-8"));
            this.addCookieToResponse(response, this.config.getUserInfoCookieName(), encodedInfo, this.isAuthRemember(request));
        } catch (Exception e) {
            throw new ServerErrorException(e.getMessage());
        }
    }

    private List<GrantedAuthority> generateAuthorities(final Claims claims) {
        List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
        if (claims.get(this.config.getRolesClaim()) != null) {
            @SuppressWarnings("unchecked")
            List<String> roles = (ArrayList<String>) claims.get(this.config.getRolesClaim());
            authorities = roles.stream().filter(Role::contains).map(SimpleGrantedAuthority::new)
                    .collect(Collectors.toList());
        }
        return authorities;
    }

    private Cookie getAuthCookieFromRequest(final HttpServletRequest request) {
        return this.cookiesUtils.getCookieFromRequest(request, this.config.getCookieName());
    }

    private void addCookieToResponse(HttpServletResponse response, final String name, final String value, final boolean remember) {
        if (remember) {
            this.cookiesUtils.addPersistentCookieToResponse(response, name, value);
        } else {
            this.cookiesUtils.addCookieToResponse(response, name, value);
        }
    }

    private String buildRoleStringFromAuthentication(final Collection<? extends GrantedAuthority> authorities) {
        return authorities.stream().map(GrantedAuthority::getAuthority).collect(Collectors.joining(","));
    }

}
