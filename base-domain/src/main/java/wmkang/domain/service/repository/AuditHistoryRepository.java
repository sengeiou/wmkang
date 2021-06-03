package wmkang.domain.service.repository;


import org.springframework.data.jpa.repository.JpaRepository;

import wmkang.domain.service.entity.AuditHistory;

/**
 * 감사이력 Repository
 */
public interface AuditHistoryRepository extends JpaRepository<AuditHistory, Long> {
}
