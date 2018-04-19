package springskeleton.config.property;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Data
public class FrontEndProperties {

    @Value("${front-end.url}")
    private String url;

}
