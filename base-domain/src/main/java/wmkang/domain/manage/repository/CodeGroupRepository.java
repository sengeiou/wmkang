package wmkang.domain.manage.repository;


import org.springframework.data.jpa.repository.JpaRepository;

import wmkang.domain.manage.entity.CodeGroup;

/**
 * 공통코드 그룹 Repository
 */
public interface CodeGroupRepository extends JpaRepository<CodeGroup, String> {
}