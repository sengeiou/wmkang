package wmkang.task.util;

/**
 * 개별 모듈에서 사용되는 상수 정의
 * 모듈별 의존 관계에 맞춰 동일 패키지, 동일 이름의 상속을 통한 확장 체계.
 * 특정 모듈에 속한 소스는 해당 모듈에서 정의한 C 만 참조하더라도, 상위 계층에서 정의한 모든 상수들을 참조할 수 있음.
 *
 *  @see wmkang.domain.util.C
 *  @see wmkang.common.util.C
 *  @see wmkang.task.util.C
 */
public interface C extends wmkang.common.util.C {


    /**
     * 패스워드 규칙
     * 1. 문자 길이 : 8~15
     * 2. 아래 4가지 문자 종류 중 3가지 이상 포함
     *  1) 영문 대문자
     *  2) 영문 소문자
     *  3) 숫자
     *  4) 특수 문자
     */
    String  PASSWD_RULE_REG_EXPR = "(?=^.{8,15}$)((?=.*\\d)(?=.*[A-Z])(?=.*[a-z])|(?=.*\\d)(?=.*[^A-Za-z0-9])(?=.*[a-z])|(?=.*[^A-Za-z0-9])(?=.*[A-Z])(?=.*[a-z])|(?=.*\\d)(?=.*[A-Z])(?=.*[^A-Za-z0-9]))^.*";

}
