package wmkang.domain.enums;


import com.fasterxml.jackson.annotation.JsonCreator;

import lombok.AllArgsConstructor;
import wmkang.domain.util.ShardHolder;


@AllArgsConstructor
public enum Shard implements Symbolic {


    FIRST   ("1"),
    SECOND  ("2");


    private final String shardNo;


    public String getNo() {
        return shardNo;
    }

    @Override
    public String getSymbol() {
        return shardNo;
    }

    @JsonCreator
    public static Shard fromJson(String jsonValue) {
        for (Shard shard : values()) {
            if (shard.getNo().equals(jsonValue)) {
                ShardHolder.set(shard.getNo());
                return shard;
            }
        }
        throw new IllegalArgumentException(jsonValue);
    }
}
