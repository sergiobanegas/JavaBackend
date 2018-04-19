package springskeleton.config.property;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Data;

@Component
@ConfigurationProperties("spring.mail")
@Data
public class MailProperties {

    private String protocol;

    private String host;

    private String port;

    private String username;

    private String password;

}
