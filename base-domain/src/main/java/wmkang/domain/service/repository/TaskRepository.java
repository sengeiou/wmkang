package wmkang.domain.service.repository;


import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

import wmkang.domain.jpa.AbstractServiceRepository;
import wmkang.domain.service.entity.Task;

/**
 * 타스크 Repository
 */
@Repository
public interface TaskRepository extends JpaRepository<Task, Integer>,
                                        QuerydslPredicateExecutor<Task>,
										TaskRepositoryExtend {

    List<Task> findByIdIn(List<Integer> idList);

    Page<Task> findByTitleIgnoreCaseContainsAndCompletedAndCreatedDateAfter(String title, Boolean completed, LocalDateTime createdDate, Pageable pageable);
}


// Extend
interface TaskRepositoryExtend {
}


// ExtendImpl
class TaskRepositoryExtendImpl 	extends 	AbstractServiceRepository
								implements 	TaskRepositoryExtend {

    public TaskRepositoryExtendImpl() {
        super(Task.class);
    }
}
