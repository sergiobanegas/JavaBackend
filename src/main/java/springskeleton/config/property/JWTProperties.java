package springskeleton.config.property;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Data;

@Component
@ConfigurationProperties("jwt")
@Data
public class JWTProperties {

    private String secret;

    private String cookieName;

    private String userInfoCookieName;

    private String rolesClaim;

    private String languageClaim;

    private String rememberClaim;

}
