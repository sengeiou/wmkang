package wmkang.domain.jpa;


import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.beans.factory.annotation.Autowired;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.querydsl.sql.SQLQueryFactory;
import com.querydsl.sql.SQLTemplates;


public abstract class AbstractManageRepository extends AbstractCommonRepository {


    public AbstractManageRepository(Class<?> domainClass) {
        super(domainClass);
    }


    @Override
    @PersistenceContext(unitName = "manageEntityManager")
    public void setEntityManager(EntityManager entityManager) {
        super.setEntityManager(entityManager);
    }

    @Autowired
    public final void init(JPAQueryFactory manageJPAQueryFactory, SQLQueryFactory manageSQLQueryFactory, SQLTemplates template) {
        jpaQueryFactory = manageJPAQueryFactory;
        sqlQueryFactory = manageSQLQueryFactory;
        sqlTemplates = template;
    }
}
