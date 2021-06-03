package wmkang.domain.manage.repository;


import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import wmkang.domain.manage.entity.Code;
import wmkang.domain.manage.entity.Code.CodePk;

/**
 * 공통코드 Repository
 */
public interface CodeRepository extends JpaRepository<Code, CodePk> {


    List<Code> findAllByOrderByGroupCodeAscCodeAsc();

}
