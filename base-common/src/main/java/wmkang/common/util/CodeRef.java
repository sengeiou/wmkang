package wmkang.common.util;

/**
 * 공통 코드 참조를 위한 코드그룹 및 코드 정의
 *
 * [ 확인 및 주의 사항 ]
 * - 코드 내 리터럴 사용 금지 용도
 * - 실제 코드에서 참조하는 코드 값만 주석 해제하여 사용 (과도한 메모리 사용 예방 차원)
 */
public interface CodeRef {


    /**
     * 코드그룹명 : 상품카테고리
     */
    interface Category {
        String Electric     = "EL";		// 가전
//        String Furniture 	= "FN";    	// 가구
//        String Fashion    = "FS";    	// 패션
//        String Food  		= "FD";    	// 식품
//        String Sports 	= "SP";    	// 스포츠
//        String Cosmetics  = "CM";    	// 화장품
    }
}
