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
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.transaction.ChainedTransactionManager;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.querydsl.sql.SQLQueryFactory;
import com.querydsl.sql.spring.SpringConnectionProvider;
import com.zaxxer.hikari.HikariDataSource;

import wmkang.common.util.ShardDataSource;

/**
 * service 데이터소스 Config
 */
@EnableJpaRepositories(
    basePackages            = "wmkang.domain.service.repository",
    entityManagerFactoryRef = "serviceEntityManager",
    transactionManagerRef   = "serviceTransactionManager"
)
@Configuration
public class DatasourceServiceConfig {


    public static final String ENTITY_BASE_PACKAGE = "wmkang.domain.service.entity";
    public static final String ENUM_BASE_PACKAGE   = "wmkang.domain.enums";


    @Bean
    @ConfigurationProperties(prefix = "spring.datasource.hikari.service-1")
    public DataSource dataSource1(){
        return DataSourceBuilder.create().type(HikariDataSource.class).build();
    }

    @Bean
    @ConfigurationProperties(prefix = "spring.datasource.hikari.service-2")
    public DataSource dataSource2(){
        return DataSourceBuilder.create().type(HikariDataSource.class).build();
    }

    @Bean
    public DataSource serviceDataSource() {
        Map<Object, Object> dataSourceMap = new HashMap<>();
        dataSourceMap.put("1", dataSource1());
        dataSourceMap.put("2", dataSource2());

        ShardDataSource shardDataSource = new ShardDataSource();
        shardDataSource.setName("SERVICE-DATASOURCE");
        shardDataSource.setTargetDataSources(dataSourceMap);
        shardDataSource.afterPropertiesSet();

        // JPA 테이블 생성을 위해, 임시로 디폴트 DataSource 지정
        shardDataSource.setDefaultTargetDataSource(dataSourceMap.get("1"));
        return shardDataSource;
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean serviceEntityManager(@Qualifier("jpaProperties") Map<String, String> jpaProperties) {
        LocalContainerEntityManagerFactoryBean entityManagerFactoryBean = new LocalContainerEntityManagerFactoryBean();
        entityManagerFactoryBean.setPersistenceUnitName("service");
        entityManagerFactoryBean.setDataSource(serviceDataSource());
        entityManagerFactoryBean.setJpaVendorAdapter(DatasourceManageConfig.getJpaVendorAdapter());
        entityManagerFactoryBean.setPackagesToScan(ENTITY_BASE_PACKAGE, ENUM_BASE_PACKAGE);
        entityManagerFactoryBean.setJpaPropertyMap(jpaProperties);
        return entityManagerFactoryBean;
    }

    @Bean
    public PlatformTransactionManager serviceTransactionManager(@Qualifier("serviceEntityManager") LocalContainerEntityManagerFactoryBean serviceEntityManagerFactoryBean) {
        JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(serviceEntityManagerFactoryBean.getObject());
        return transactionManager;
    }

    /**
     * querydsl-jpa 모듈 초기화
     */
    @Bean
    public JPAQueryFactory serviceJPAQueryFactory(@Qualifier("serviceEntityManager") EntityManager serviceEntityManager) {
        return new JPAQueryFactory(serviceEntityManager);
    }

    /**
     * querydsl-sql 모듈 초기화
     */
    @Bean
    public SQLQueryFactory serviceSQLQueryFactory(com.querydsl.sql.Configuration configuration) {
        return new SQLQueryFactory(configuration, new SpringConnectionProvider(serviceDataSource()));
    }

    /**
     * manage & service 통합 트랜잭션
     */
    @Primary
    @Bean
    public PlatformTransactionManager globalTransactionManager(PlatformTransactionManager manageTransactionManager, PlatformTransactionManager serviceTransactionManager) {
        return new ChainedTransactionManager(manageTransactionManager, serviceTransactionManager);
    }
}
