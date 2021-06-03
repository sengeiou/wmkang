package wmkang.domain.manage.repository;


import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

import wmkang.domain.jpa.AbstractManageRepository;
import wmkang.domain.manage.entity.User;

/**
 * 사용자 Repository
 */
public interface UserRepository extends JpaRepository<User, Integer>,
                                        QuerydslPredicateExecutor<User>,
                                        UserRepositoryExtend {

    Optional<User> findByIdAndActive(String id, boolean active);

    Optional<User> findByEmailAndActive(String email, boolean active);

    Page<User> findByNameIgnoreCaseContains(String content, Pageable pageable);

    Optional<User> findByEmail(String email);

}

//Extend
interface UserRepositoryExtend {
}

//ExtendImpl
class UserRepositoryExtendImpl extends AbstractManageRepository implements UserRepositoryExtend {


    public UserRepositoryExtendImpl() {
        super(User.class);
    }
}
