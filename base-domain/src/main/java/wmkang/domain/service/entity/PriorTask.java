package wmkang.domain.service.entity;


import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.DynamicUpdate;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import wmkang.domain.service.entity.PriorTask.PriorTaskPk;

/**
 * 상위 타스크 Entity
 */
@Cacheable
@Cache(region = "entity.PriorTask", usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("serial")
@ToString
@Getter@Setter
@DynamicUpdate
@EqualsAndHashCode(callSuper = false)
@IdClass(PriorTaskPk.class)
@Table(name = "PRIOR_TASKS")
@Entity
public class PriorTask {


    /** 타스크 아이디 */
    @Id
    @Column(name = "SRC_ID", nullable = false)
    private Integer srcId;

    /** 상위 타스크 아이디 */
    @Id
    @Column(name = "REF_ID", nullable = false)
    private Integer refId;


    @ToString
    @Getter
    @EqualsAndHashCode
    @AllArgsConstructor
    public static class PriorTaskPk implements java.io.Serializable {
        private Integer srcId;
        private Integer refId;
    }
}
