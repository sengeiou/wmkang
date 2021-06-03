package wmkang.domain.jpa;


import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.beans.factory.annotation.Autowired;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.querydsl.sql.SQLQueryFactory;
import com.querydsl.sql.SQLTemplates;


public abstract class AbstractServiceRepository extends AbstractCommonRepository {


    public AbstractServiceRepository(Class<?> domainClass) {
        super(domainClass);
    }

    @Override
    @PersistenceContext(unitName = "serviceEntityManager")
    public void setEntityManager(EntityManager entityManager) {
        super.setEntityManager(entityManager);
    }

    @Autowired
    public final void init( JPAQueryFactory serviceJPAQueryFactory, SQLQueryFactory serviceSQLQueryFactory, SQLTemplates templates ) {
        jpaQueryFactory = serviceJPAQueryFactory;
        sqlQueryFactory = serviceSQLQueryFactory;
        sqlTemplates = templates;
    }
}
