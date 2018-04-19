package springskeleton.config.security;

import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import springskeleton.model.Language;

import lombok.Getter;

public class CurrentUser extends User {

    private static final long serialVersionUID = 8324959971356750229L;

    @Getter
    private String name;

    @Getter
    private Language language;

    public CurrentUser(final springskeleton.model.User user, final Collection<? extends GrantedAuthority> authorities) {
        super(String.valueOf(user.getId()), user.getPassword(), user.isEnabled(), true, true, true, authorities);
        this.language = user.getLanguage();
        this.name = user.getName();
    }

}
