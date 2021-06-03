package wmkang.common.file;


import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import wmkang.common.api.Status;
import wmkang.common.exception.ApplicationException;

/**
 * 파일업로드 요청을 받는 컨트롤러에게 업로드 처리 결과를 반환하기 위한 용도
 * (사용자에게 처리 결과를 전달하기 위한 용도 아님)
 */
@ToString
@Getter@Setter
public class UploadResult {


    static final String NO_EXTENSTION = "NONE";

    private Long        fileNo;
    private String      fileId;
    private String      fileType;
    private long        fileSize;
    private String      fileName;
    private UploadState state         = UploadState.SUCCESS;
    // 이하 속성 추후 삭제
    private String      filePath;
    private String      fileAbsolutePath;
    private String      downloadUri;
    private String      downloadUrl;


    public UploadResult(MultipartFile file) {
        this.fileName = file.getOriginalFilename();
        this.fileType = file.getContentType();
        this.fileSize = file.getSize();

        if(fileName == null)
            throw new ApplicationException(Status.PARAMETER_INVALID, "File name is null");

        // IE, Edge : 파일 전체 경로 올라옴, 경로 삭제 필요
        // Chrome, Opera : 파일명만 올라옴
        int sepIdx;
        if (((sepIdx = fileName.lastIndexOf('/')) > 0) || ((sepIdx = fileName.lastIndexOf('\\')) > 0)){
            fileName = fileName.substring(sepIdx + 1);
        }
    }

    @JsonIgnore
    public String getExtension() {
        int dotIdx = fileName.lastIndexOf(".");
        if (dotIdx > 0) {
            return fileName.substring(dotIdx + 1).toLowerCase();
        } else {
            return NO_EXTENSTION;
        }
    }
}
