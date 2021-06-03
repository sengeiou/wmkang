package wmkang.domain.manage.entity;


import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.DynamicUpdate;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import wmkang.domain.enums.Role;
import wmkang.domain.enums.Shard;

/**
 * 사용자 Entity
 */
@ToString
@Getter@Setter
@NoArgsConstructor@AllArgsConstructor
@DynamicUpdate
@EqualsAndHashCode(of = {"id"}, callSuper = false)
@Table(name = "USER")
@Entity
public class User {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column
    private String  passwd;

    @Column
    private String  name;

    @Column(unique = true)
    private String  email;

    @Column(columnDefinition = "char(1)")
    private Role    role;

    @Column(columnDefinition = "char(1)")
    private Shard   shard;

    @Column
    private boolean active = true;
}
