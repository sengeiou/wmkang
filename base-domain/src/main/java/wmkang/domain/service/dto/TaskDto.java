package wmkang.domain.service.dto;


import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import wmkang.domain.service.entity.Task;
import wmkang.domain.util.TaskUtil;
import wmkang.domain.util.Util;

/**
 * 타스크 DTO
 */
@Builder
@ToString
@EqualsAndHashCode
@NoArgsConstructor@AllArgsConstructor
@Setter@Getter
public class TaskDto {


    @Schema(description = "타스크 아이디")
    Integer id;

    @Schema(description = "타스크 이름")
    String  title;

    @Builder.Default
    @Schema(description = "완료 여부", defaultValue = "false")
    Boolean completed = false;

    @Schema(description = "상위 타스크 아이디 리스트(구분자 ',')")
    String  priors;

    @EqualsAndHashCode.Exclude
    @Schema(description = "생성일자")
    LocalDateTime createdDate;

    @EqualsAndHashCode.Exclude
    @Schema(description = "수정일자")
    LocalDateTime modifiedDate;


    public static TaskDto fromEntity(Task task) {
        TaskDto dto = Util.copy(task, TaskDto.class);
        dto.setPriors(TaskUtil.getPriorTaskIdsStr(task.getPriorTasks()));
        return dto;
    }
}
