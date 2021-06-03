package wmkang.common.api;


import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import org.springframework.data.domain.Page;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.Getter;
import lombok.Setter;


@JsonInclude(Include.NON_NULL)
@Getter@Setter
public class Response<T> {


    static Map<Integer, Status> statusMap = new HashMap<>();

    private Status      header;
    private PageInfo    page;
    private T           data;


    Response() {
    }

    Response(Status staus) {
        this.header = staus;
    }

    Response(Status staus, T data) {
        this.header = staus;
        this.data = data;
    }

    Response(Status staus, T data, Page<?> page) {
        this.header = staus;
        this.data = data;
        this.page = new PageInfo(page);
    }


    static {
        Stream.of(Status.values()).forEach(s -> statusMap.put(s.getCode(), s));
    }

    // 성공 응답 -------------------------------------------

    public static Response<Void> ok() {
        return Status.SUCCESS.getResponse();
    }

    public static <T> Response<T> ok(T data) {
        return new Response<>(Status.SUCCESS, data);
    }

    public static <T> Response<List<T>> ok(Page<T> page) {
        return new Response<>(Status.SUCCESS, page.getContent(), page);
    }

    // 성공, 싶패 범용(Status 기반) ------------------------------------

    public static Response<Void> getResponse(Status status) {
        return status.getResponse();
    }

    public static <T> Response<T> getResponse(Status status, T data) {
        return new Response<>(status, data);
    }

    public static <T> Response<List<T>> getPageResponse(Status status, Page<T> page) {
        return new Response<>(status, page.getContent(), page);
    }

    // 성공, 싶패 범용(int 상태코드 기반) ------------------------------------

    public static Response<Void> getResponse(int code) {
        return statusMap.get(code).getResponse();
    }

    public static <T> Response<T> getResponse(int code, T data) {
        return new Response<>(statusMap.get(code), data);
    }

    public static <T> Response<List<T>> getPageResponse(int code, Page<T> page) {
        return new Response<>(statusMap.get(code), page.getContent(), page);
    }
}
