package wmkang.common.util;


import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import lombok.extern.slf4j.Slf4j;
import wmkang.common.security.UserDetails;
import wmkang.domain.util.ShardHolder;


@Slf4j
public class ShardDataSource extends AbstractRoutingDataSource {


    private String name = "NO_NAME";


    /**
     * DB 샤드 선택 기준
     * 1차 : Controller 파라미터(DTO 포함)에 Shard 타입 변수가 선언되고, ShardConverter에 의해 해당 변수 값이 채워질 때, ShardConverter가 선택된 샤드 값을 ShardHolder를 통해 저장.
     *  - ShardHolder는 ThreadLocal을 통해 하나의 스레드 구간 동안(Request 스코프)만 저장된 샤드를 유지함.
     *  - 스레드 플에 의해 스레드가 재사용 될 수 있으므로, 설정되었던 값이 재사용되는 것을 막기 위해, ControllerAdvice에서 응답 반환 전 ShardHolder 설정값을 제거.
     * 2차 : 사용자 테이블에 사용자별로 샤드가 각각 지정되어 있으므로, 세션에 로딩되어 있는 사용자 정보에서 샤드 값을 조회함.
     *  - Shard 타입 변수가 선언되어 있지 않을 경우, 사용되는 선택 기준
     *
     * 서비스 정책 맞춰 파라미터 또는 사용자 세션 2 가지 중 한 가지를 선택할 수 있으며, 본 예제처럼 혼용하여 사용하는 것도 가능함.
     */
    @Override
    protected Object determineCurrentLookupKey() {
        String shardNo = ShardHolder.get();
        if (shardNo == null) {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if ((authentication != null) && !(authentication instanceof AnonymousAuthenticationToken))
                shardNo = ((UserDetails) authentication.getPrincipal()).getShard().getNo();
        }
        log.trace("# SHARD_CHOICE - {}", shardNo);
        return shardNo;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "ShardDataSource." + name;
    }
}
