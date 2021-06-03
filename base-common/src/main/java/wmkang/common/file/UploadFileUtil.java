package wmkang.common.file;


import java.nio.file.Path;
import java.nio.file.Paths;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import wmkang.common.util.C;
import wmkang.domain.service.entity.UploadFile;


public class UploadFileUtil {


    static Path   uplodSavePath;
    static String uriContextPath;


    public static void init(String uploadBasePath, String contextPath) {
        uplodSavePath = Paths.get(uploadBasePath).toAbsolutePath().normalize();
        uriContextPath = contextPath;
    }

    // 유틸리티 메소드

    public static String getFilePath(UploadFile uploadFile) {
        return Paths.get(uploadFile.getCategory().getSymbol()).resolve(uploadFile.getSubDirectory()).resolve(uploadFile.getFileId()).toString();
    }

    public static String getFileAbsolutePath(UploadFile uploadFile) {
        return uplodSavePath.resolve(getFilePath(uploadFile)).toAbsolutePath().toString();
    }

    public static String getDownloadUri(UploadFile uploadFile) {
        return C.FILE_DOWNLOAD_URI.concat("?fileNo=").concat(String.valueOf(uploadFile.getFileNo())).concat("&fileId=").concat(uploadFile.getFileId());
    }

    public static String getDownloadUrl(UploadFile uploadFile) {
        HttpServletRequest request = ((ServletRequestAttributes)RequestContextHolder.getRequestAttributes()).getRequest();
        if(request.getHeader("x-forwarded-host") == null) {
            return ServletUriComponentsBuilder.fromCurrentContextPath().path(C.FILE_DOWNLOAD_URI).queryParam("fileNo", uploadFile.getFileNo()).queryParam("fileId", uploadFile.getFileId()).toUriString();
        }
        return request.getHeader("x-forwarded-proto").concat("://").concat(request.getHeader("x-forwarded-host")).concat(uriContextPath).concat(getDownloadUri(uploadFile));
    }
}
