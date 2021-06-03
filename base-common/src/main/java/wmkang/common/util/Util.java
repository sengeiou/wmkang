package wmkang.common.util;


import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.security.core.context.SecurityContextHolder;

import wmkang.common.api.Status;
import wmkang.common.exception.ApplicationException;
import wmkang.common.security.UserDetails;
import wmkang.domain.enums.Role;

/**
 * 빈번히 사용되는 기능들을 간편하게 사용하기 위힌 유틸리티 클래스
 */
public class Util extends wmkang.domain.util.Util {


    private static MessageSourceAccessor messageAccessor;


    public static void init(MessageSourceAccessor messageSourceAccessor) {
        messageAccessor = messageSourceAccessor;
    }

    /**
     * 안전한 String 처리
     */
    public static String getNonNullTrimStr(String str) {
        return (str == null) ? "" : str.trim();
    }

    /**
     * 파라미터 누락 점검 용도
     */
    public static void assertNonNull(Object obj, String fieldName) {
        boolean isBlank = (obj == null);
        if ((!isBlank) && (obj instanceof String)) {
            String str = (String) obj;
            isBlank = (str.length() == 0) || "".equals(str.trim());
        }
        if (isBlank) {
            throw new ApplicationException(Status.PARAMETER_INVALID, getMessage(C.MESSAGE_KEY_MISSING, fieldName));
        }
    }

    /**
     * 메시지 조회
     * messageAccessor.getMessage("msgCode", new String[]{"args1", "args2", "args3"})
     *         -> Util.getMessage("msgCode", "args1", "args2", "args3")
     */
    public static String getMessage(String code, Object... args) {
        return messageAccessor.getMessage(code, args);
    }

    /**
     * 로그인 사용자 조회ㅣ
     */
    public static UserDetails getLoginUser() {
        return (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    /**
     * 로그인 사용자 Role 조회
     */
    public static Role getLoginUserRole() {
        return ((UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getRole();
    }
}