package wmkang.common.cache;


import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.springframework.data.redis.core.HashOperations;

import wmkang.common.annotation.Cache;


public class RedisCacheTemplate<T> {


    @Resource(name = "redisTemplate")
    private HashOperations<String, String, T> hashOperations;

    private String                            namespace;


    @PostConstruct
    protected void init() {
        namespace = this.getClass().getAnnotation(Cache.class).value();
        if(namespace == null)
            throw new RuntimeException("Cache namespace is not specified - " + this.getClass().getName());
    }


    // HashOperations Wrapping Methods

    public Set<String> keys(){
        return hashOperations.keys(namespace);
    }

    public void putAll(Map<String, T> map) {
        hashOperations.putAll(namespace, map);
    }

    public void put(String key, T value) {
        hashOperations.put(namespace, key, value);
    }

    public T get(String key) {
        return hashOperations.get(namespace, key);
    }

    public List<T> multiGet(Collection<String> keys) {
        return hashOperations.multiGet(namespace, keys);
    }

    public void delete(Object... keys) {
        hashOperations.delete(namespace, keys);
    }

    public void drop() {
        hashOperations.delete(namespace, keys().toArray());
    }
}
