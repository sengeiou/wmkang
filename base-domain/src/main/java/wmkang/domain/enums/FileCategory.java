package wmkang.domain.enums;


import lombok.AllArgsConstructor;

/**
 * 업무 용도에 맞춰 업로드되는 파일의 종류를 구분하기 위한 용도.
 * 파일 저장시, 기본 파일 업로드 경로 아래 symbol 문자에 해당하는 경로가 생성되고, 그 아래 업로드 설정에 맞춰 하위 디렉토리 생성되고, 그 경로에 설정 정보에 맞춰 변경된 파일 이름으로 저장.
 * 카테고리 선택은, 파일 업로그 기능을 사용하는 개별 비즈니스 컨트롤러에서 선택.
 */
@AllArgsConstructor
public enum FileCategory implements Symbolic {


    COMMUNITY("COM"),       // 사용자 게시판 파일
    CERTIFICATE("CER"),     // 증명서류
    PRIVATE("PRI")          // 개인저장소
    ;

    private String symbol;


    @Override
    public String getSymbol() {
        return symbol;
    }

}
