package wmkang.task.util;


import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.security.core.context.SecurityContextHolder;

import wmkang.common.api.Status;
import wmkang.common.exception.ApplicationException;
import wmkang.common.security.UserDetails;
import wmkang.common.util.C;
import wmkang.domain.enums.Role;

/**
 * 개별 모듈에서 빈번히 사용되는 기능들을 모듈화 하기 위한 유틸리티 클래스
 * 모듈별 의존 관계에 맞춰 동일 패키지, 동일 이름의 상속을 통한 확장 체계.
 * 특정 모듈에 속한 소스는 해당 모듈에서 정의한 C 만 참조하더라도, 상위 계층에서 정의한 모든 상수들을 참조할 수 있음.
 *
 *  @see wmkang.domain.util.Util
 *  @see wmkang.common.util.Util
 *  @see wmkang.task.util.Util
 */
public class Util extends wmkang.common.util.Util {
}
