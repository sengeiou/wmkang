
package wmkang.domain.service.entity;


import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.DynamicUpdate;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import wmkang.domain.enums.FileCategory;
import wmkang.domain.jpa.CreateAuditable;

/**
 * 업로드파일 Entity
 */
@ToString
@Getter@Setter
@DynamicUpdate
@EqualsAndHashCode(of = { "fileNo" }, callSuper = false)
@Table(name = "UPLOAD_FILE")
@Entity
public class UploadFile extends CreateAuditable {


    /** 파일번호(순번) */
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "FILE_NO")
    private Long         fileNo;

    /** 파일아이디 */
    @Column(name = "FILE_ID")
    private String       fileId;

    /** 파일명(업로드 원본 파일명) */
    @Column(name = "FILE_NAME")
    private String       fileName;

    /** 파일구분(파일업로드를 이용하는 업무 구분, 파일업로드 기본 경로 아래 카테고리 경로 생성) */
    @Column(name = "CATEGORY")
    private FileCategory category;

    /** 파일업로드 기본 경로 및 카테고리 경로 이후 나머지 경로 */
    @Column(name = "PATH")
    private String       subDirectory;
}
