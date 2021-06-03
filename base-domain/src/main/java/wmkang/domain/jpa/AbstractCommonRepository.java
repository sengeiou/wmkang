package wmkang.domain.jpa;


import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;

import com.querydsl.core.Tuple;
import com.querydsl.core.types.EntityPath;
import com.querydsl.core.types.Expression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.querydsl.jpa.sql.JPASQLQuery;
import com.querydsl.sql.SQLQuery;
import com.querydsl.sql.SQLQueryFactory;
import com.querydsl.sql.SQLTemplates;


public abstract class AbstractCommonRepository extends QuerydslRepositorySupport {


    protected JPAQueryFactory jpaQueryFactory;
    protected SQLQueryFactory sqlQueryFactory;
    protected SQLTemplates    sqlTemplates;


    public AbstractCommonRepository(Class<?> domainClass) {
        super(domainClass);
    }

    //----------------------------
    // 1. JPAQuery

    protected JPAQuery<Tuple> select(Expression<?>... exprs) {
        return jpaQueryFactory.select(exprs);
    }

    protected <T> JPAQuery<T> select(Expression<T> expr) {
        return jpaQueryFactory.select(expr);
    }

    protected <T> JPAQuery<T> selectDistinct(Expression<T> expr) {
        return jpaQueryFactory.selectDistinct(expr);
    }

    protected JPAQuery<Tuple> selectDistinct(Expression<?>... exprs) {
        return jpaQueryFactory.selectDistinct(exprs);
    }

    protected JPAQuery<Integer> selectOne() {
        return jpaQueryFactory.selectOne();
    }

    protected JPAQuery<Integer> selectZero() {
        return jpaQueryFactory.selectZero();
    }

    protected <T> JPAQuery<T> selectFrom(EntityPath<T> from) {
        return jpaQueryFactory.selectFrom(from);
    }

/*
    // QuerydslRepositorySupport 메소드와 중복

    protected JPAQuery<?> from(EntityPath<?> from) {
        return jpaQueryFactory.from(from);
    }

    protected JPAQuery<?> from(EntityPath<?>... from) {
        return jpaQueryFactory.from(from);
    }
*/

    protected JPAQuery<?> getJPAQuery() {
        return jpaQueryFactory.query();
    }


    //----------------------------
    // 2. SQLQuery

    protected SQLQuery<?> getSQLQuery() {
        return sqlQueryFactory.query();
    }

    //----------------------------
    // 3. JPASQLQuery

    protected <T> JPASQLQuery<T> getJPASQLQuery(Class<T> clazz) {
        return new JPASQLQuery<T>(getEntityManager(), sqlTemplates);
    }
}
