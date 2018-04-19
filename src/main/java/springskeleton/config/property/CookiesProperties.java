package springskeleton.config.property;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Data;

@Component
@ConfigurationProperties("cookies")
@Data
public class CookiesProperties {

    private int defaultMaxAge;

    private String defaultPath;

}
