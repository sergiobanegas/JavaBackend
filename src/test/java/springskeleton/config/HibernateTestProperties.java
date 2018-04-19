package springskeleton.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import springskeleton.config.property.HibernateProperties;

@ConfigurationProperties("test.hibernate")
@Profile("test")
@Component
public class HibernateTestProperties extends HibernateProperties {

}
