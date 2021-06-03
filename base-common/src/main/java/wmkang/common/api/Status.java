package wmkang.common.api;


import java.util.List;

import org.springframework.data.domain.Page;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import lombok.Getter;
import wmkang.common.jackson.StatusDeserializer;


@Getter
@JsonDeserialize(using = StatusDeserializer.class)
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum Status {


    // 표준 상태 코드

    SUCCESS                     (200, "OK"),
    UNAUTHORIZED                (401, "인증되지 않은 요청"),
    FORBIDDEN                   (403, "접근 권한 없음"),
    NOT_FOUND                   (404, "존재하지 않는 API"),
    METHOD_NOT_ALLOWED          (405, "지원하지 않는 HTTP 메소드"),
    INTERNAL_SERVER_ERROR       (500, "서버 내부 오류"),

    // 일반 오류

    PARAMETER_INVALID           (600, "파라미터 오류"),
    RESOURCE_NOT_EXIST          (601, "요청한 자원이 존재하지 않음"),
//    SHARD_MISSING               (602, "샤드 정보 누락"),
    FILE_UPLOAD_FAILED          (603, "파일 업로드 실패"),

    PASSWD_INCLUDES_ID_CHARS    (610, "패스워드에 ID 3글자 이상 포함"),

    // 엔티티 관련 오류

    ENTITY_NOT_EXIST            (700, "존재하지 않는 개체에 대한 요청"),
    ENTITY_ALREADY_EXIST        (701, "이미 존재하는 개체에 대한 요청"),
    ENTITY_PARENT_NOT_EXIST     (702, "부모 개체가 존재하지 않음"),

    // TASK 모듈 오류 정의 - 900번대

    PRIOR_TASK_NOT_COMPLETED    (900, "완료되지 않은 상위 타스크가 있습니다"),
    UNCHANGABLE_COMPLETED_STATE (901, "완료된 타스크의 상태는 변경 불가"),

    DATABASE_IMPORT_FAIL        (910, "데이터 가져오기(Import) 실패"),
    DATABASE_EXPORT_FAIL        (911, "데이터 내보내기(Export) 실패"),
    INVALID_IMPORT_FILE         (912, "유효하지 데이터 파일")
    ;

    private int            code;
    private String         message;


    @JsonIgnore
    private Response<Void> response;


    Status(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public Response<Void> getResponse() {
        if(response == null)
            response = new Response<>(this);
        return response;
    }

    public <T> Response<T> getResponse(T t) {
        return new Response<>(this, t);
    }

    public <T> Response<List<T>> getPageResponse(Page<T> page) {
        return new Response<>(this, page.getContent(), page);
    }
}
