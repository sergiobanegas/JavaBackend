package springskeleton.config.property;

import org.springframework.beans.factory.annotation.Qualifier;

import lombok.Data;

@Data
@Qualifier
public abstract class HibernateProperties {

    private String charSet;

    private boolean show_sql;

    private boolean format_sql;

    private String hbm2ddl_auto;

    private String dialect;

}
