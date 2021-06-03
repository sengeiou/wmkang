package wmkang.task.controller;


import java.util.List;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Pageable;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import wmkang.common.annotation.AuditLog;
import wmkang.common.api.Response;
import wmkang.common.api.Status;
import wmkang.domain.annotation.ServiceReadTransactional;
import wmkang.domain.annotation.ServiceWriteTransactional;
import wmkang.domain.enums.ActionType;
import wmkang.domain.service.dto.TaskDto;
import wmkang.domain.service.entity.Task;
import wmkang.domain.service.repository.PriorTaskRepository;
import wmkang.domain.service.repository.TaskRepository;
import wmkang.domain.util.TaskUtil;
import wmkang.domain.util.Util;
import wmkang.task.dto.TaskControllerDto.CreateTask;
import wmkang.task.dto.TaskControllerDto.SearchCondition;
import wmkang.task.dto.TaskControllerDto.UpdateTask;

/**
 * 타스크 관리 컨트롤러
 */
@RequiredArgsConstructor
@RequestMapping("/task")
@RestController
public class TaskController {


    private final TaskRepository      taskRepo;
    private final PriorTaskRepository priorTaskRepo;


    /**
     * 전체 타스크 조회
     */
    @GetMapping
    @ServiceReadTransactional
    public Response<List<TaskDto>> getAllTasks() {
        return Response.ok(taskRepo.findAll().stream().map(TaskDto::fromEntity).collect(Collectors.toList()));
    }

    /**
     * ID 조회
     */
    @GetMapping("/{id}")
    @ServiceReadTransactional
    public Response<TaskDto> getTask(@PathVariable Integer id) {
        return taskRepo.findById(id).map(TaskDto::fromEntity).map(Response::ok)
                                    .orElse(Status.ENTITY_NOT_EXIST.getResponse(null));
    }

    /**
     * 페이지 단위 조회
     */
    @GetMapping("/page")
    @ServiceReadTransactional
    public Response<List<TaskDto>> getTaskByPage(Pageable pageable) {
        return Response.ok(taskRepo.findAll(pageable).map(TaskDto::fromEntity));
    }

    /**
     * 검색
     */
    @GetMapping("/search")
    @ServiceReadTransactional
    public Response<List<TaskDto>> search(@Valid SearchCondition search, Pageable pageable){
        return Response.ok(taskRepo.findByTitleIgnoreCaseContainsAndCompletedAndCreatedDateAfter(search.getTitle(), search.getCompleted(), search.getFromDate().atStartOfDay(), pageable).map(TaskDto::fromEntity) );
    }

    /**
     * 타스크 등록
     */
    @AuditLog(id = "T01", action = ActionType.CREATE)
    @PostMapping
    @ServiceWriteTransactional
    public Response<Integer> create(@Valid @RequestBody CreateTask taskDto) {
        List<Task> priorTaskList = null;
        if(!StringUtils.isBlank(taskDto.getPriors())) {
            priorTaskList = taskRepo.findByIdIn( TaskUtil.parsePriorTaskIds(taskDto.getPriors(), -1) );
        }
        Task task = Util.copy(taskDto, Task.class);
        task.setPriorTasks(priorTaskList);
        if(TaskUtil.isCompletedAndHasUncompletedPriorTask(task)) {
            return Status.PRIOR_TASK_NOT_COMPLETED.getResponse(null);
        }
        return Response.ok(taskRepo.save(task).getId());
    }

    /**
     * 타스크 수정
     */
    @AuditLog(id = "T02", action = ActionType.UPDATE)
    @PutMapping("/{id}")
    @ServiceWriteTransactional
    public Response<Void> update(@PathVariable Integer id, @RequestBody @Valid UpdateTask taskDto) {
        return taskRepo.findById(id).map(found -> {
            if (found.getCompleted() && !taskDto.getCompleted()) {
                return Status.UNCHANGABLE_COMPLETED_STATE.getResponse();
            }
            List<Task> newPriorTaskList = null;
            if (!StringUtils.isBlank(taskDto.getPriors())) {
                newPriorTaskList = taskRepo.findByIdIn(TaskUtil.parsePriorTaskIds(taskDto.getPriors(), found.getId()));
            }
            if (taskDto.getCompleted() && TaskUtil.hasUncompletedTask(newPriorTaskList)) {
                return Status.PRIOR_TASK_NOT_COMPLETED.getResponse();
            }
            Util.copy(taskDto, found);
            if (!CollectionUtils.isEmpty(newPriorTaskList)) {
                found.setPriorTasks(newPriorTaskList);
            }
            taskRepo.save(found);
            return Response.ok();
        }).orElse(Status.ENTITY_NOT_EXIST.getResponse());
    }

    /**
     * 타스크 상태 변경
     */
    @PutMapping("/state/{id}")
    @ServiceWriteTransactional
    public Response<Void> updateState(@PathVariable int id, @RequestParam(required = true) Boolean completed) {
        return taskRepo.findById(id).map(found -> {
            if (found.getCompleted() && !completed) {
                return Status.UNCHANGABLE_COMPLETED_STATE.getResponse();
            }
            if (!found.getCompleted() && completed) {
                if (TaskUtil.hasUncompletedTask(found.getPriorTasks())) {
                    return Status.PRIOR_TASK_NOT_COMPLETED.getResponse();
                }
                found.setCompleted(true);
                taskRepo.save(found);
            }
            return Response.ok();
        }).orElse(Status.ENTITY_NOT_EXIST.getResponse());
    }

    /**
     * 타스크 삭제
     */
    @DeleteMapping("/{id}")
    @ServiceWriteTransactional
    public Response<Void> delete(@PathVariable Integer id) {
        return taskRepo.findById(id).map(found -> {
            priorTaskRepo.deleteSrcOrRef(id);
            taskRepo.delete(found);
            return Response.ok();
        }).orElse(Status.ENTITY_NOT_EXIST.getResponse());
    }
}
