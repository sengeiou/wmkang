package wmkang.domain.manage.entity;


import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import wmkang.domain.jpa.Auditable;

/**
 * 공통코드 그룹 Entity
 */
@Getter@Setter
@NoArgsConstructor@AllArgsConstructor
@EqualsAndHashCode(of = {"groupCode"}, callSuper = false)
@Table(name = "CODE_GROUP")
@Entity
public class CodeGroup extends Auditable {


    @Id
    @Column(name = "GROUP_CODE")
    private String groupCode;

    @Column(name = "GROUP_NAME")
    private String groupName;

}
