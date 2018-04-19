package springskeleton.config.property;

import org.springframework.beans.factory.annotation.Qualifier;

import lombok.Data;

@Data
@Qualifier
public abstract class DataSourceProperties {

    private String driver;

    private String url;

    private String username;

    private String password;

    private String dialect;
}
