package wmkang.domain.util;


import lombok.extern.slf4j.Slf4j;

/**
 * Thread Scope 구간 동안 사용될 Service DB 선택(service-1, service-2,,,)을 유지하기 위한 용도.
 * Controller 파라미터(Http Parameter, Request Body)로 enum Shard 타입이 변수가 사용되었을 때,
 * ShardConverter가 동작하게 되고, 툭정 Shard 값으로 변환되었을 때, set() 메소드를 통해 해당 샤드값이 설정됨.
 * 설정된 값은 ControllerAdvice에 의해, 응답 결과가 반환되기 직전 remove() 메소드를 통해 제거됨.
 */
@Slf4j
public class ShardHolder {


    private static final ThreadLocal<String> currentShard = new ThreadLocal<>();


    public static void set(String shardNo) {
        log.trace("> ShardHolder.set(" + shardNo + ")");
        currentShard.set(shardNo);
    }

    public static void set(Integer shardNo) {
        log.trace("> ShardHolder.set("+shardNo+")");
        currentShard.set(String.valueOf(shardNo));
    }

    public static void remove() {
        log.trace("> ShardHolder.remove()");
        currentShard.remove();
    }

    public static String get() {
        return currentShard.get();
    }
}
