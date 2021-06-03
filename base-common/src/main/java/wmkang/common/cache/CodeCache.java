package wmkang.common.cache;


import java.util.Map;

import org.springframework.lang.Nullable;

import wmkang.common.annotation.Cache;


@Cache("code")
public class CodeCache extends RedisCacheTemplate<Map<String, String>> {


    @Nullable
    public String get(String groupCode, String code) {
        Map<String, String> map = get(groupCode);
        return (map == null)? null : map.get(code);
    }
}
