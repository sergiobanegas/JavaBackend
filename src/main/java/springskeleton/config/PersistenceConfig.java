package springskeleton.config;

import java.util.Properties;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

import org.apache.commons.dbcp2.BasicDataSource;
import org.hibernate.jpa.HibernatePersistenceProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import springskeleton.config.property.DataSourceProperties;
import springskeleton.config.property.HibernateProperties;

@Configuration
@EnableJpaRepositories(basePackages = {ResourceNames.DAOS}, repositoryImplementationPostfix = "Impl")
@EnableTransactionManagement
public class PersistenceConfig {

    private final DataSourceProperties dataSourceConfig;

    private final HibernateProperties hibernateConfig;

    @Autowired
    public PersistenceConfig(DataSourceProperties dataSourceConfig, HibernateProperties hibernateConfig) {
        this.dataSourceConfig = dataSourceConfig;
        this.hibernateConfig = hibernateConfig;
    }

    @Bean
    public DataSource dataSource() {
        BasicDataSource dataSource = new BasicDataSource();
        dataSource.setDriverClassName(this.dataSourceConfig.getDriver());
        dataSource.setUrl(this.dataSourceConfig.getUrl());
        dataSource.setUsername(this.dataSourceConfig.getUsername());
        dataSource.setPassword(this.dataSourceConfig.getPassword());
        return dataSource;
    }

    @Bean
    public EntityManagerFactory entityManagerFactory() {
        LocalContainerEntityManagerFactoryBean entityManagerFactoryBean = new LocalContainerEntityManagerFactoryBean();
        entityManagerFactoryBean.setPersistenceProviderClass(HibernatePersistenceProvider.class);
        Properties properties = new Properties();
        properties.put("hibernate.connection.charSet", this.hibernateConfig.getCharSet());
        properties.put("hibernate.show_sql", this.hibernateConfig.isShow_sql());
        properties.put("hibernate.format_sql", this.hibernateConfig.isFormat_sql());
        properties.put("hibernate.hbm2ddl.auto", this.hibernateConfig.getHbm2ddl_auto());
        properties.put("hibernate.dialect", this.hibernateConfig.getDialect());
        entityManagerFactoryBean.setJpaProperties(properties);
        entityManagerFactoryBean.setPackagesToScan(ResourceNames.MODELS);
        entityManagerFactoryBean.setDataSource(this.dataSource());
        entityManagerFactoryBean.afterPropertiesSet();
        return entityManagerFactoryBean.getObject();
    }

    @Bean
    public PlatformTransactionManager transactionManager() {
        JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(this.entityManagerFactory());
        return transactionManager;
    }

}
