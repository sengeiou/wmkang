package wmkang.common.cache;


import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import wmkang.domain.manage.entity.Code;
import wmkang.domain.manage.repository.CodeRepository;


@Slf4j
@RequiredArgsConstructor
@Component
public class CacheHandler {


    final CodeRepository codeRepo;

    final CodeCache      codeCache;


    public synchronized void reloadCodeCache() {

        log.info("# COMMON CODE (RE)LOADING");

        Set<String> keySet = codeCache.keys();
        List<Code> codeList = codeRepo.findAllByOrderByGroupCodeAscCodeAsc();

        Map<String, Map<String, String>> resultMap = codeList.stream().collect(
                Collectors.groupingBy(  Code::getGroupCode,
                                        Collectors.toMap(Code::getCode,Code::getName)
        ));
        codeCache.putAll(resultMap);

        // 이전 캐쉬 로딩 시점에 있었던 코드가, 신규 로딩시 삭제되었을 수 있으므로, 해당 코드만 캐쉬에서 제거함.
        if(keySet != null) {
            keySet.removeAll(resultMap.keySet());
            keySet.stream().forEach(codeCache::delete);
        }
    }
}
