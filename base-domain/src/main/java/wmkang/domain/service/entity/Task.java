package wmkang.domain.service.entity;


import java.util.ArrayList;
import java.util.List;

import javax.persistence.Cacheable;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.DynamicUpdate;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import wmkang.domain.jpa.Auditable;

/**
 * 타스크 Entity
 */
@Cacheable
@Cache(region = "entity.Task", usage = CacheConcurrencyStrategy.READ_WRITE)
@ToString
@Getter@Setter
@DynamicUpdate
@EqualsAndHashCode(of = {"id"}, callSuper = false)
@Table(name = "TASK")
@Entity
public class Task extends Auditable {


    /** 타스크 아이디 */
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "TASK_ID")
    private Integer id;

    /** 타스크 이름 */
    @Column(name = "TITLE", nullable = false)
    private String  title;

    /** 완료 여부 */
    @Column(name = "COMPLETED")
    private Boolean completed;

    /** 상위 타스크 관리를 위한 릴레이션 정의 */
    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinTable( name = "PRIOR_TASKS",
                joinColumns = @JoinColumn(name = "SRC_ID"),
                inverseJoinColumns = @JoinColumn(name = "REF_ID"))
    private List<Task> priorTasks = new ArrayList<>();

}
