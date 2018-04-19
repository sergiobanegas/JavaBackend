package springskeleton.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import springskeleton.config.property.DataSourceProperties;

@ConfigurationProperties("test.datasource")
@Profile("test")
@Component
public class DataSourceTestsProperties extends DataSourceProperties {

}
