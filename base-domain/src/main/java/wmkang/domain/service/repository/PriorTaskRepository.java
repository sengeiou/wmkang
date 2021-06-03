package wmkang.domain.service.repository;


import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import wmkang.domain.jpa.AbstractServiceRepository;
import wmkang.domain.service.entity.PriorTask;
import wmkang.domain.service.entity.PriorTask.PriorTaskPk;

/**
 * 상위 타스크 Repository
 */
@Repository
public interface PriorTaskRepository extends JpaRepository<PriorTask, PriorTaskPk>,
                                             QuerydslPredicateExecutor<PriorTask>,
                                             PriorTaskRepositoryExtend {

    List<PriorTask> findByRefId(int refId);

    @Modifying
    @Query("delete PriorTask p where p.srcId = :id or p.refId = :id")
    void deleteSrcOrRef(@Param("id") Integer id);
}


//Extend
interface PriorTaskRepositoryExtend {
}


//ExtendImpl
class PriorTaskRepositoryExtendImpl  extends     AbstractServiceRepository
                                     implements  PriorTaskRepositoryExtend {

    public PriorTaskRepositoryExtendImpl() {
        super(PriorTask.class);
    }
}
