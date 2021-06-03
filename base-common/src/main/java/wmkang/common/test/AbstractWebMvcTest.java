package wmkang.common.test;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import wmkang.common.api.Response;
import wmkang.common.api.Status;
import wmkang.common.config.TestConfig;

/**
 * WebMvc 테스트 기본 환경 제공을 위한 공통 부모 클래스
 */
@ActiveProfiles("test")
@Transactional
@AutoConfigureMockMvc(printOnlyOnFailure = false)
@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public abstract class AbstractWebMvcTest {


    protected final static TypeReference<Response<Void>>    RESPONSE_TYPE_VOID    = new TypeReference<>(){};
    protected final static TypeReference<Response<?>>       RESPONSE_TYPE_WILD    = new TypeReference<>(){};
    protected final static TypeReference<Response<Integer>> RESPONSE_TYPE_INTEGER = new TypeReference<>(){};
    protected final static TypeReference<Response<Long>>    RESPONSE_TYPE_LONG    = new TypeReference<>(){};
    protected final static TypeReference<Response<String>>  RESPONSE_TYPE_STRING  = new TypeReference<>() {};

    @Autowired
    protected WebApplicationContext                         context;

    @Autowired
    protected MockMvc                                       mvc;

    @Autowired
    protected ObjectMapper                                  om;


    protected void assertSuccess(Response<?> Response) {
        assertEquals(Status.SUCCESS, Response.getHeader());
    }

    protected void assertFail(Response<?> Response) {
        assertNotEquals(Status.SUCCESS, Response.getHeader());
    }
}
