package wmkang.domain.manage.entity;


import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import wmkang.domain.jpa.Auditable;
import wmkang.domain.manage.entity.Code.CodePk;

/**
 * 공통코드 Entity
 */
@Getter@Setter
@NoArgsConstructor@AllArgsConstructor
@EqualsAndHashCode(of = {"groupCode", "code"}, callSuper = false)
@IdClass(CodePk.class)
@Table(name = "CODE")
@Entity
public class Code extends Auditable {


    @Id
    @Column(name = "GROUP_CODE")
    private String groupCode;

    @Id
    @Column(name = "CODE")
    private String code;

    @Column(name = "NAME")
    private String name;


    @SuppressWarnings("serial")
    @Getter@Setter
    @EqualsAndHashCode
    @NoArgsConstructor@AllArgsConstructor
    public static class CodePk implements Serializable {
        private String groupCode;
        private String code;
    }
}
