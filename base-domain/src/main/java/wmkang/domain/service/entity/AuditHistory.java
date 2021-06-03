package wmkang.domain.service.entity;


import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import wmkang.domain.enums.ActionType;

/**
 * 감사이력 Entity
 */
@Builder
@ToString
@Getter
@EqualsAndHashCode(of = { "seqNo" })
@Table(name = "AUDIT_HISTORY")
@Entity
public class AuditHistory {


    /** 순번 */
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "SEQ_NO")
    private Long       seqNo;

    /** API 아이디 */
    @Column(name = "API")
    private String     api;

    /** 액션타입 */
    @Column(name = "ACTION", columnDefinition = "char(1)")
    private ActionType action;

    /** API 파라미터 */
    @Column(name = "ARGS", columnDefinition = "text")
    private String     args;

    /** 행위자 아이디 */
    @Column(name = "ACTOR")
    private Integer    actor;

    /** 발생시간 */
    @Column(name = "TIME")
    LocalDateTime      time;

}
