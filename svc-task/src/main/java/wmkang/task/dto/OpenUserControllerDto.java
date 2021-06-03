package wmkang.task.dto;


import java.util.stream.IntStream;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.ScriptAssert;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import wmkang.common.api.Status;
import wmkang.common.exception.ApplicationException;
import wmkang.common.util.Util;
import wmkang.domain.enums.Role;
import wmkang.domain.enums.Shard;
import wmkang.task.util.C;


public class OpenUserControllerDto {


    /**
     * 회원 가입
     */
    @ScriptAssert( lang     = "javascript", alias = "v",
                   script   = "(String(v.passwd)) == (String(v.confirmPasswd))",
                   reportOn = "confirmPasswd",
                   message  = "confirmPasswdDismatch" )
    @Getter@Setter
    @NoArgsConstructor@AllArgsConstructor
    public static class RegistUser {

        @Email
        @Size(min = 8, max = 40)
        @NotBlank
        @Schema(description = "이메일 주소")
        String email;

        @Pattern(regexp = C.PASSWD_RULE_REG_EXPR)
        @NotBlank
        @Schema(description = "패스워드")
        String passwd;

        @NotBlank
        @Schema(description = "확인 패스워드")
        String confirmPasswd;

        @Size(min = 2, max = 15)
        @NotBlank
        @Schema(description = "사용자 이름")
        String name;

        Role role;

        Shard shard;

        public void validate() {
            String id = email.substring(0, email.indexOf('@'));
            IntStream.range(0, id.length() - 2).forEach(i -> {
                if (passwd.indexOf(email.substring(i, i + 3)) > -1)
                    throw new ApplicationException(Status.PARAMETER_INVALID, Util.getMessage("passwdInludeId"));
            });
        }
    }
}
