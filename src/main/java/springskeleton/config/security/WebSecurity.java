package springskeleton.config.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import springskeleton.config.Endpoints;
import springskeleton.model.Role;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(securedEnabled = true, prePostEnabled = true)
public class WebSecurity extends WebSecurityConfigurerAdapter {

    public static final Map<String, HttpMethod> NON_PROTECTED_URIS;

    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    private final LogoutHandler logoutHandler;

    private UserDetailsService userDetailsService;

    static {
        Map<String, HttpMethod> requestMap = new HashMap<>();
        requestMap.put(Endpoints.AUTH + Endpoints.SIGN_UP, HttpMethod.POST);
        requestMap.put(Endpoints.AUTH + Endpoints.RESET_PASSWORD, HttpMethod.POST);
        requestMap.put(Endpoints.AUTH + Endpoints.CONFIRMATION, HttpMethod.POST);
        NON_PROTECTED_URIS = Collections.unmodifiableMap(requestMap);
    }

    @Autowired
    public WebSecurity(final UserDetailsService userDetailsService, BCryptPasswordEncoder bCryptPasswordEncoder, LogoutHandler logoutHandler) {
        this.userDetailsService = userDetailsService;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.logoutHandler = logoutHandler;
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Override
    public void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(this.userDetailsService).passwordEncoder(this.bCryptPasswordEncoder);
    }

    @Override
    public void configure(org.springframework.security.config.annotation.web.builders.WebSecurity web) {
        web.ignoring().antMatchers("/swagger-resources/**", "/swagger-ui.html", "/v2/api-docs", "/webjars/**",
                "/resources/**");
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration().applyPermitDefaultValues();
        config.addAllowedMethod("*");
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    @Bean
    public AccessDeniedHandler accessDeniedHandler() {
        return new ForbiddenHandler();
    }

    @Bean
    public JWTAuthenticationFilter jwtAuthenticationFilter() throws Exception {
        JWTAuthenticationFilter jwtAuthenticationFilter = new JWTAuthenticationFilter();
        jwtAuthenticationFilter.setAuthenticationManager(authenticationManagerBean());
        return jwtAuthenticationFilter;
    }

    @Bean
    public JWTAuthorizationFilter jwtAuthorizationFilter() throws Exception {
        return new JWTAuthorizationFilter(authenticationManagerBean());
    }

    @Bean
    public FilterRegistrationBean jwtAuthorizationFilterRegistration(final JWTAuthorizationFilter filter) {
        FilterRegistrationBean registration = new FilterRegistrationBean(filter);
        registration.setEnabled(false);
        return registration;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.cors().and().csrf().disable()
                .authorizeRequests()
                .antMatchers(HttpMethod.POST, Endpoints.AUTH + Endpoints.RESET_PASSWORD + "/**").permitAll()
                .antMatchers(HttpMethod.POST, Endpoints.AUTH + Endpoints.SIGN_UP).permitAll()
                .antMatchers(HttpMethod.POST, Endpoints.AUTH + Endpoints.CONFIRMATION + "/**").permitAll()
                .antMatchers(HttpMethod.GET, Endpoints.USERS).hasRole(Role.ADMIN.name())
                .antMatchers(HttpMethod.DELETE, Endpoints.USERS + "/**").hasRole(Role.ADMIN.name())
                .anyRequest().authenticated().and().exceptionHandling().accessDeniedHandler(accessDeniedHandler())
                .and().logout().logoutUrl(Endpoints.AUTH + Endpoints.LOGOUT).logoutSuccessHandler(logoutHandler)
                .and().addFilterBefore(this.jwtAuthorizationFilter(), BasicAuthenticationFilter.class)
                .addFilter(this.jwtAuthenticationFilter())
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
    }

}
