package wmkang.common.param;


import javax.validation.constraints.NotNull;

import lombok.Getter;
import lombok.Setter;
import wmkang.domain.enums.Shard;


@Setter@Getter
public class ShardParam {


    @NotNull
    Shard shard;
}
