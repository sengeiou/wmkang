package wmkang.task.dto;


import java.time.LocalDate;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import org.springframework.format.annotation.DateTimeFormat;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;


public class TaskControllerDto {


    @ToString
    @Setter@Getter
    public static class CreateTask {

        @NotBlank
        @Schema(description = "타스크 이름")
        String  title;

        @Schema(description = "완료 여부", defaultValue = "false")
        Boolean completed = false;

        @Schema(description = "상위 타스크 아이디 리스트(구분자 ',')")
        String  priors;
    }

    @ToString
    @Setter@Getter
    public static class UpdateTask {

        @NotBlank
        @Schema(description = "타스크 이름")
        String  title;

        @NotNull
        @Schema(description = "완료 여부")
        Boolean completed;

        @Schema(description = "상위 타스크 아이디 리스트(구분자 ',')")
        String  priors;
    }

    @ToString
    @Getter@Setter
    public static class SearchCondition {

        @NotBlank
        @Schema(description = "타스크 이름 키워드")
        String    title;

        @NotNull
        @Schema(description = "완료 여부")
        Boolean   completed;

        @NotNull
        @DateTimeFormat(pattern = "yyyyMMdd")
        @Schema(description = "등록일 기준 검색 시작 일자")
        LocalDate fromDate;
    }
}
