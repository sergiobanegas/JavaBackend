package springskeleton.config.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import springskeleton.dao.AuthorizationDao;
import springskeleton.dao.UserDao;
import springskeleton.model.Role;
import springskeleton.util.I18n;

import java.util.List;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

@Service
@Transactional
public class UserDetailsServiceImpl implements UserDetailsService {

    private UserDao userDao;

    private AuthorizationDao authorizationDao;

    private I18n i18n;

    @Autowired
    public UserDetailsServiceImpl(UserDao userDao, AuthorizationDao authorizationDao, I18n i18n) {
        this.userDao = userDao;
        this.authorizationDao = authorizationDao;
        this.i18n = i18n;
    }

    @Override
    public UserDetails loadUserByUsername(final String email) throws UsernameNotFoundException {
        final springskeleton.model.User user = this.userDao.findOneByEmail(email);
        if (user == null) {
            throw new UsernameNotFoundException(this.i18n.get("AbstractUserDetailsAuthenticationProvider.badCredentials"));
        }
        final List<Role> roleList = this.authorizationDao.findRoleByUser(user);
        final List<GrantedAuthority> authorities = roleList.stream().map(Role::roleName).map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
        return new CurrentUser(user, authorities);
    }

}
