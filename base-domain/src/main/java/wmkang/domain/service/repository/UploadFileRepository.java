package wmkang.domain.service.repository;


import org.springframework.data.jpa.repository.JpaRepository;

import wmkang.domain.service.entity.UploadFile;

/**
 * 업로드파일 Repository
 */
public interface UploadFileRepository extends JpaRepository<UploadFile, Long> {

}
