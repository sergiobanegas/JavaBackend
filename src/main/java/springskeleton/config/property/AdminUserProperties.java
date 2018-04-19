package springskeleton.config.property;

import springskeleton.model.Gender;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties("admin")
@Data
public class AdminUserProperties {

    private String email;

    private String password;

    private String name;

    private Gender gender;

}
