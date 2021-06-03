package wmkang.common.file;


public enum UploadState {

    SIZE_ZERO_IGNORED,  //파일 사이즈가 0인 유효하지 않은 파일
    EXTENSION_DENIED,   //유효하지 않은 확장자
    SAVE_FAILED,        //저장 실패
    SUCCESS             //성공
}
