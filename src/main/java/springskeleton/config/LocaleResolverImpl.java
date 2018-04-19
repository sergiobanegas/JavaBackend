package springskeleton.config;

import springskeleton.util.AuthUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.LocaleResolver;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Locale;

@Component("localeResolver")
public class LocaleResolverImpl implements LocaleResolver {


    private final AuthUtils authUtils;

    @Autowired
    public LocaleResolverImpl(AuthUtils authUtils) {
        this.authUtils = authUtils;
    }

    @Override
    public Locale resolveLocale(final HttpServletRequest request) {
        String userLanguage = this.authUtils.getUserLanguage(request);
        return userLanguage == null ? request.getLocale() : Locale.forLanguageTag(userLanguage);
    }

    @Override
    public void setLocale(final HttpServletRequest request, HttpServletResponse response, final Locale locale) {
        response.setLocale(locale);
    }
}
