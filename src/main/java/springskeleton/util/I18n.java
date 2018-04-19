package springskeleton.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

@Component
public class I18n {

    private final MessageSource messageSource;

    @Autowired
    public I18n(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    public String get(final String key, final Object[] arguments) {
        return this.messageSource.getMessage(key, arguments, LocaleContextHolder.getLocale());
    }

    public String get(final String key) {
        return this.messageSource.getMessage(key, null, LocaleContextHolder.getLocale());
    }

}
