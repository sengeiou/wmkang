package wmkang.common.controller;


import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;

import javax.servlet.http.HttpServletRequest;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import wmkang.common.api.Status;
import wmkang.common.exception.ApplicationException;
import wmkang.common.file.FileUploadConfig;
import wmkang.common.file.UploadFileUtil;
import wmkang.common.util.C;
import wmkang.common.util.Util;
import wmkang.domain.service.entity.UploadFile;
import wmkang.domain.service.repository.UploadFileRepository;

@RequiredArgsConstructor
@RestController
public class FileDownloadController {


    private final FileUploadConfig          fileConfig;
    private final UploadFileRepository      fileRepo;


    /**
     * 파일 다운로드를 위한 파라미터 2가지를 선언하고 있습니다.
     * fileNo 파라미터 만으로도 충분히 다운로드 기능 구현이 가능하나, 보안적 측면을 강화하기 위해 fileId 까지 확인하도록 구현하였습니다.
     */
    @GetMapping(C.FILE_DOWNLOAD_URI)
    public ResponseEntity<Resource> download( @RequestParam(required = true) Long   fileNo,
                                              @RequestParam(required = true) String fileId,
                                              @RequestHeader(value = "user-agent") String userAgent,
                                              HttpServletRequest request) throws IOException {

        Util.assertNonNull(fileNo, "fileNo");
        Util.assertNonNull(fileId, "fileId");

        UploadFile found = fileRepo.findById(fileNo).map(f -> {
            if (!f.getFileId().equals(fileId)) {
                throw new ApplicationException(Status.RESOURCE_NOT_EXIST);
            }
            return f;
        }).orElseThrow(() -> new ApplicationException(Status.RESOURCE_NOT_EXIST));

        Resource resource = null;
        try {
            Path filePath = fileConfig.getBaseAbsPath().resolve(UploadFileUtil.getFilePath(found));
            resource = new UrlResource(filePath.toUri());
            if (!resource.exists()) {
                throw new ApplicationException(Status.RESOURCE_NOT_EXIST);
            }
        } catch (MalformedURLException ex) {
            throw new ApplicationException(Status.RESOURCE_NOT_EXIST);
        }

        String fileName = found.getFileName();
        String contentType = request.getServletContext().getMimeType(fileName);
        if (contentType == null) {
            contentType = C.FILE_DOWNLOAD_TYPE;
        }

        // userAgent
        // Edge   : Mozilla/5.0 (Windows NT 10.0; Win64; x64)       AppleWebKit/537.36 (KHTML, like Gecko) Chrome/64.0.3282.140 Safari/537.36 Edge/17.17134
        // IE 11  : Mozilla/5.0 (Windows NT 10.0; WOW64; Trident/7.0; Touch; rv:11.0) like Gecko
        // Chrome : Mozilla/5.0 (Windows NT 10.0; Win64; x64)       AppleWebKit/537.36 (KHTML, like Gecko) Chrome/68.0.3440.106 Safari/537.36
        // Opera  : Mozilla/5.0 (Windows NT 10.0; Win64; x64)       AppleWebKit/537.36 (KHTML, like Gecko) Chrome/67.0.3396.87 Safari/537.36 OPR/54.0.2952.71
        // Safari : Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_5) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/13.1.1 Safari/605.1.15

        // Browser 종류에 따른 한글 파일명 처리
        fileName = new String(fileName.getBytes(StandardCharsets.UTF_8), StandardCharsets.ISO_8859_1);
        if ((userAgent.indexOf("Edge") > -1)                                                // Edge
                || ((userAgent.indexOf("Chrome") < 0)&&(userAgent.indexOf("Mac") < 0))) {   // IE 11

            fileName = URLEncoder.encode(fileName, StandardCharsets.ISO_8859_1);
        }

        return ResponseEntity.ok().contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"").body(resource);
    }
}
