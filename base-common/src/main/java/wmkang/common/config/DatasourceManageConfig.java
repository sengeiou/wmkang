package wmkang.common.config;


import java.util.HashMap;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.Database;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.querydsl.sql.H2Templates;
import com.querydsl.sql.SQLQueryFactory;
import com.querydsl.sql.SQLTemplates;
import com.querydsl.sql.spring.SpringConnectionProvider;
import com.querydsl.sql.spring.SpringExceptionTranslator;
import com.zaxxer.hikari.HikariDataSource;

/**
 * manage 데이터소스 Config
 */
@EnableJpaAuditing(auditorAwareRef = "auditorProvider")
@EnableJpaRepositories(
    basePackages            = "wmkang.domain.manage.repository",
    entityManagerFactoryRef = "manageEntityManager",
    transactionManagerRef   = "manageTransactionManager"
)
@Configuration
public class DatasourceManageConfig {


    public static final String ENTITY_BASE_PACKAGE = "wmkang.domain.manage.entity";
    public static final String ENUM_BASE_PACKAGE   = "wmkang.domain.enums";


    @Bean
    @ConfigurationProperties(prefix = "spring.jpa.properties")
    public Map<String, String> jpaProperties(){
        return new HashMap<>();
    }

    @Primary
    @Bean
    @ConfigurationProperties(prefix = "spring.datasource.hikari.manage")
    public DataSource manageDataSource(){
        return DataSourceBuilder.create().type(HikariDataSource.class).build();
    }

    public static HibernateJpaVendorAdapter getJpaVendorAdapter() {
        HibernateJpaVendorAdapter jpaVendorAdapter = new HibernateJpaVendorAdapter();
        jpaVendorAdapter.setDatabase(Database.H2);
        return jpaVendorAdapter;
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean manageEntityManager() {
        LocalContainerEntityManagerFactoryBean entityManagerFactoryBean = new LocalContainerEntityManagerFactoryBean();
        entityManagerFactoryBean.setPersistenceUnitName("manage");
        entityManagerFactoryBean.setDataSource(manageDataSource());
        entityManagerFactoryBean.setJpaVendorAdapter(getJpaVendorAdapter());
        entityManagerFactoryBean.setPackagesToScan(ENTITY_BASE_PACKAGE, ENUM_BASE_PACKAGE);
        entityManagerFactoryBean.setJpaPropertyMap(jpaProperties());
        return entityManagerFactoryBean;
    }

    @Bean
    public PlatformTransactionManager manageTransactionManager() {
        JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(manageEntityManager().getObject());
        return transactionManager;
    }

    /**
     * querydsl-jpa 모듈 초기화
     */
    @Bean
    public JPAQueryFactory manageJPAQueryFactory(@Qualifier("manageEntityManager") EntityManager manageEntityManager) {
        return new JPAQueryFactory(manageEntityManager);
    }

    /**
     * querydsl-sql 모듈 초기화
     */
    @Bean
    public SQLTemplates sqlTemplates() {
        return new H2Templates();
    }

    @Bean
    public com.querydsl.sql.Configuration querydslSqlConfiguration() {
        com.querydsl.sql.Configuration configuration = new com.querydsl.sql.Configuration(sqlTemplates());
        configuration.setExceptionTranslator(new SpringExceptionTranslator());
        return configuration;
    }

    @Bean
    public SQLQueryFactory manageSQLQueryFactory() {
        return new SQLQueryFactory(querydslSqlConfiguration(), new SpringConnectionProvider(manageDataSource()));
    }
}
