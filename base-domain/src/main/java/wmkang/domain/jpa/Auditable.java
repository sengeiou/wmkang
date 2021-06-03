package wmkang.domain.jpa;


import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;

import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import lombok.Getter;

/**
 * 테이블 레코드 등록, 수정 이력 주적을 위한 공통 감사 Entity
 */
@Getter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class Auditable {


    @CreatedDate
    @Column(name = "CREATE_DT", updatable = false)
    protected LocalDateTime createdDate;

    @CreatedBy
    @Column(name = "CREATOR", updatable = false)
    protected Integer creator;

    @LastModifiedDate
    @Column(name = "MODIFY_DT")
    protected LocalDateTime modifiedDate;

    @LastModifiedBy
    @Column(name = "MODIFIER")
    protected Integer modifier;
}
