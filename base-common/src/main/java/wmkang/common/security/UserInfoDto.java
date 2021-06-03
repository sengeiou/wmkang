package wmkang.common.security;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import wmkang.domain.enums.Role;
import wmkang.domain.enums.Shard;


@ToString
@Getter@Setter
public class UserInfoDto {


    @Schema(description = "사용자 이름")
    String name;

    @Schema(description = "이메일 주소")
    String email;

    @Schema(description = "사용자 권한")
    Role   role;

    @Schema(description = "사용자 샤드")
    Shard  shard;
}