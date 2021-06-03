package wmkang.domain.enums.converter;


import javax.persistence.Converter;

import wmkang.domain.enums.Shard;
import wmkang.domain.util.ShardHolder;


@Converter(autoApply = true)
public class ShardConverter extends AbstractSymbolicConverter<Shard> {


    @Override
    public Shard convert(String shardNo) {
        Shard shard = super.convert(shardNo);
        if(shard != null) {
            ShardHolder.set(shardNo);
        }
        return shard;
    }
}