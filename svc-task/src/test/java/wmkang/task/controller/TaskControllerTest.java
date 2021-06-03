package wmkang.task.controller;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.IntStream;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import com.fasterxml.jackson.core.type.TypeReference;

import io.micrometer.core.instrument.util.StringUtils;
import wmkang.common.api.Response;
import wmkang.common.api.Status;
import wmkang.common.test.AbstractWebMvcTest;
import wmkang.common.util.Util;
import wmkang.domain.service.dto.TaskDto;
import wmkang.domain.util.TaskUtil;
import wmkang.task.dto.TaskControllerDto.UpdateTask;


/**
 * TaskController 단위 테스트 클래스
 */
@WithUserDetails("user1@wmkang.com")
public class TaskControllerTest extends AbstractWebMvcTest {


    final TypeReference<Response<TaskDto>>       RESPONSE_TYPE_TASK      = new TypeReference<>(){};
    final TypeReference<Response<List<TaskDto>>> RESPONSE_TYPE_LIST_TASK = new TypeReference<>(){};


    @DisplayName("CRUD 시나리오 테스트")
    @Test
    public void crudSenarioTest() throws Exception {

        // CASE_1 : Task 생성 => OK

        Set<TaskDto> taskSet = createNewTasks(3);

        // CASE_2 : Task 조회 => OK

        taskSet.forEach(t -> {
            try {
                TaskDto found = findTaskById(t.getId());
                assertTask(found, false, 0);
                assertEquals(t, found);
            } catch (Exception e) {
                e.printStackTrace();
                fail();
            }
        });

        // CASE_3 : Task 업데이트 - 직업명 & 상위작업 => OK

        //  작업명 변경
        String titlePostfix = "_MODIFIED";
        taskSet.stream().forEach(t -> t.setTitle(t.getTitle() + titlePostfix));

        //  상위작업 생성
        TaskDto priorTask = createNewTasks(1).iterator().next();

        //  작업명 및 상위작업 변경 요청
        taskSet.forEach(t -> {
            try {
                t.setPriors(String.valueOf(priorTask.getId()));
                MvcResult mvcResult = mvc.perform(taskPutRequest(t.getId())
                                            .content(om.writeValueAsString(Util.copy(t, UpdateTask.class))))
                                            .andExpect(status().isOk())
                                            .andReturn();
                Response<Void> Response = om.readValue(mvcResult.getResponse().getContentAsString(), RESPONSE_TYPE_VOID);
                assertSuccess(Response);
            } catch (Exception e) {
                e.printStackTrace();
                fail();
            }
        });

        //  작업 직접 조회 시에도 변경 사항 반영되었는지 확인
        taskSet.forEach(t -> {
            try {
                TaskDto found = findTaskById(t.getId());
                assertEquals(String.valueOf(priorTask.getId()), found.getPriors());
            } catch (Exception e) {
                e.printStackTrace();
                fail();
            }
        });

        // CASE_4 : Task 업데이트 - 상위작업 미완료 상태에서, 하위작업 완료 상태로 변경  => 실패 응답(PRIOR_TASK_NOT_COMPLETED) 수신

        //  작업 완료 상태 변경 요청이나, 상위작업이 완료되지 않아 실패 케이스
        taskSet.forEach(t -> {
            try {
                mvc.perform(taskPutStateRequest(t.getId(), true))
                       .andExpect(status().is(Status.PRIOR_TASK_NOT_COMPLETED.getCode()));
            } catch (Exception e) {
                e.printStackTrace();
                fail();
            }
        });

        // CASE_5 : Task 업데이트 - 상위작업 상태 완료 변경 & 하위작업 상태 완료 변경  => 성공 응답 수신

        //  상위작업 상태 완료로 변경
        MvcResult result = mvc.perform(taskPutStateRequest(priorTask.getId(), true) )
                                    .andExpect(status().isOk())
                                    .andReturn();
        Response<Void> response = om.readValue(result.getResponse().getContentAsString(), RESPONSE_TYPE_VOID);
        assertSuccess(response);

        //  하위작업 상태 완료 변경 요청
        taskSet.forEach(t -> {
            try {
                MvcResult mr = mvc.perform(taskPutStateRequest(t.getId(), true))
                                            .andExpect(status().isOk())
                                            .andReturn();
                Response<Void> r = om.readValue(mr.getResponse().getContentAsString(), RESPONSE_TYPE_VOID);
                assertSuccess(r);
            } catch (Exception e) {
                e.printStackTrace();
                fail();
            }
        });

        // CASE_6 : Task 삭제  => 성공 응답 수신

        taskSet.forEach(t -> {
            try {
                MvcResult mr = mvc.perform(taskDeleteRequest(t.getId()))
                                            .andExpect(status().isOk())
                                            .andReturn();
                Response<Void> resp = om.readValue(mr.getResponse().getContentAsString(), RESPONSE_TYPE_VOID);
                assertSuccess(resp);
            } catch (Exception e) {
                e.printStackTrace();
                fail();
            }
        });
    }

    //------------------------------------------------------------------------------------
    // 단위 테스트 구현시 사용 빈도가 높은 API 대상으로, 일반적인 검증 조건(파라미터 및 응답 검증)으로 호출 기능을 모듈화한 메소드들

    private TaskDto findTaskById(int id) throws Exception {
        MvcResult mvcResult = mvc.perform(taskGetIdRequest(id))
                                 .andExpect(status().isOk())
                                 .andReturn();
        Response<TaskDto> response = om.readValue(mvcResult.getResponse().getContentAsString(), RESPONSE_TYPE_TASK);
        assertSuccess(response);
        assertNotNull(response.getData());
        return response.getData();
    }

    private Set<TaskDto> createNewTasks(int count) {
        Set<TaskDto> taskSet = new HashSet<>(count);
        IntStream.range(1, count+1).forEach(n -> {
            try {
                TaskDto t = TaskDto.builder().title("TEST_TASK_" + n).build();
                MvcResult mvcResult = mvc.perform(taskPostRequest()
                                         .content(om.writeValueAsString(t)))
                                         .andExpect(status().isOk())
                                         .andReturn();
                Response<Integer> r = om.readValue(mvcResult.getResponse().getContentAsString(), RESPONSE_TYPE_INTEGER);
                assertSuccess(r);
                t.setId(r.getData());
                taskSet.add(t);
            } catch (Exception e) {
                e.printStackTrace();
                fail();
            }
        });
        return taskSet;
    }

    //------------------------------------------------------------------------------------
    // 테스트 컨트롤러 각 메서드별 HTTP 요청 생성 메소드

    private MockHttpServletRequestBuilder taskPostRequest() {
        return post("/task").accept(MediaType.APPLICATION_JSON_VALUE).contentType(MediaType.APPLICATION_JSON);
    }

    private MockHttpServletRequestBuilder taskGetIdRequest(int id) {
        return get("/task/" + id).accept(MediaType.APPLICATION_JSON_VALUE);
    }

    private MockHttpServletRequestBuilder taskGetPageRequest() {
        return get("/task/page").accept(MediaType.APPLICATION_JSON_VALUE);
    }

    private MockHttpServletRequestBuilder taskPutRequest(int id) {
        return put("/task/" + id).accept(MediaType.APPLICATION_JSON_VALUE).contentType(MediaType.APPLICATION_JSON);
    }

    private MockHttpServletRequestBuilder taskPutStateRequest(int id, boolean completed) {
        return put("/task/state/" + id).accept(MediaType.APPLICATION_JSON_VALUE).param("completed", String.valueOf(completed)).contentType(MediaType.APPLICATION_JSON);
    }

    private MockHttpServletRequestBuilder taskDeleteRequest(int id) {
        return delete("/task/" + id).accept(MediaType.APPLICATION_JSON_VALUE);
    }

    //------------------------------------------------------------------------------------
    // API 호출 결과 검증 유틸리티 메소드

    private void assertTask(TaskDto task, boolean completed, int priorsCount) {
        assertTrue(task.getId() > 0);
        assertFalse(StringUtils.isBlank(task.getTitle()));
        assertEquals(completed, task.getCompleted());
        assertEquals(priorsCount, TaskUtil.parsePriorTaskIds(task.getPriors(), task.getId()).size());
    }
}

