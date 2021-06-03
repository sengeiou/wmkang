package wmkang.domain.annotation;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import wmkang.domain.util.C;


@Target({ ElementType.METHOD, ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Transactional( transactionManager = C.TX_MANAGER_GLOBAL,
                readOnly = false,
                propagation = Propagation.REQUIRED,
                isolation = Isolation.READ_COMMITTED,
                timeout = C.TX_TIMEOUT_SECONDS,
                rollbackFor = Throwable.class)
public @interface GlobalWriteTransactional {
}
