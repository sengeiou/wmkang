package wmkang.task.test;


import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.ArrayUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import wmkang.common.api.Response;
import wmkang.common.file.FileUploadHandler;
import wmkang.common.file.UploadResult;
import wmkang.domain.enums.FileCategory;


@RequiredArgsConstructor
@RequestMapping("/test/file")
@RestController
public class FileUploadTestController {


    private final FileUploadHandler fileHandler;

    private final FileCategory thisControllerFileCategory = FileCategory.COMMUNITY;


    @Getter@Setter
    public class TestUploadRequest {
        String          bizParam1;
        String          bizParam2;
        String          bizParam3;
        MultipartFile[] attachFiles;
    }

    @PostMapping("/upload")
    public Response<List<UploadResult>> uploadMultipleFiles(TestUploadRequest uploadRequest) {

        System.out.println("------------------------------------------");
        System.out.println("> 1. FILE-UPLOAD : PARAMETERS");

        System.out.println("param1 : " + uploadRequest.getBizParam1());
        System.out.println("param2 : " + uploadRequest.getBizParam2());
        System.out.println("param3 : " + uploadRequest.getBizParam3());
        System.out.println("files : " + uploadRequest.getAttachFiles());

        if(!ArrayUtils.isEmpty(uploadRequest.getAttachFiles())) {

            // File 업로드 처리
            List<UploadResult> resultList = Arrays.asList(uploadRequest.getAttachFiles()).stream().map(file -> fileHandler.upload(file, thisControllerFileCategory)).collect(Collectors.toList());

            System.out.println("------------------------------------------");
            System.out.println("> 2. FILE-UPLOAD : RESULT");
            resultList.stream().forEach(System.out::println);

            //fileNo 리스트 추출
            List<Long> fileNoList = resultList.stream().map(UploadResult::getFileNo).collect(Collectors.toList());

            // 각종 조회 메소드 테스트
            System.out.println("------------------------------------------");
            System.out.println("3. FILE-UPLOAD : FileUploadHandler 조회 메소드 테스트");
            System.out.println("------------------------------------------");
            System.out.println("FILE NO:ID");
            fileHandler.getFileNoIdMap(fileNoList).forEach((k, v) -> System.out.println(k + "=" + v));
            System.out.println("------------------------------------------");
            System.out.println("FILE NO:URI");
            fileHandler.getFileNoUriMap(fileNoList).forEach((k, v) -> System.out.println(k + "=" + v));
            System.out.println("------------------------------------------");
            System.out.println("FILE NO:URL");
            fileHandler.getFileNoUrlMap(fileNoList).forEach((k, v) -> System.out.println(k + "=" + v));
            System.out.println("------------------------------------------");
            System.out.println("FILE NO:PATH");
            fileHandler.getFileNoPathMap(fileNoList).forEach((k, v) -> System.out.println(k + "=" + v));
            System.out.println("------------------------------------------");
            System.out.println("FILE NO:ABS-PATH");
            fileHandler.getFileNoAbsolutePathMap(fileNoList).forEach((k, v) -> System.out.println(k + "=" + v));
            System.out.println("------------------------------------------");

//            fileNoList.stream().forEach(n -> fileHandler.delete(n));

            return Response.ok(resultList);
        }
        return Response.ok(null);
    }
}
