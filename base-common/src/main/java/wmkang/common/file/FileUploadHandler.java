package wmkang.common.file;


import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import wmkang.domain.enums.FileCategory;
import wmkang.domain.service.entity.UploadFile;
import wmkang.domain.service.repository.UploadFileRepository;

/**
 * 범용 업로드 파일 처리(디스크 및 DB 저장)을 위한 유틸리티 클래스
 */
@Slf4j
@RequiredArgsConstructor
@Component
public class FileUploadHandler {


    private final FileUploadConfig     fileConfig;
    private final UploadFileRepository uploadFileRepo;

    @Value("${server.servlet.context-path}")
    String   contextPath;


    public UploadResult upload(MultipartFile file, FileCategory category) {

        UploadResult fileInfo = new UploadResult(file);

        // 파일 용량 검사
        if (file.getSize() == 0) {
            fileInfo.setState(UploadState.SIZE_ZERO_IGNORED);
            return fileInfo;
        }

        // 파일 확장자 검사
        String extension = fileInfo.getExtension();

        if (! fileConfig.isValidExtension(extension)) {
            fileInfo.setState(UploadState.EXTENSION_DENIED);
            return fileInfo;
        }

        // 파일시스템 저장
        String subDir = fileConfig.getSubDir();
        String fileId = fileConfig.getNewFileId();
        Path savePath = null;
        try {
            savePath = fileConfig.getBaseAbsPath().resolve(category.getSymbol()).resolve(subDir);
            Files.createDirectories(savePath);
            Files.copy(file.getInputStream(), savePath.resolve(fileId), StandardCopyOption.REPLACE_EXISTING);
        } catch (Exception e) {
            log.error("# FAILED_SAVING_FILE : " + savePath + File.separator + fileId, e);
            fileInfo.setState(UploadState.SAVE_FAILED);
            return fileInfo;
        }

        // 데이터베이스 저장
        UploadFile uploadFile = new UploadFile();
        uploadFile.setFileId(fileId);
        uploadFile.setFileName(fileInfo.getFileName());
        uploadFile.setCategory(category);
        uploadFile.setSubDirectory(subDir);
        uploadFile = uploadFileRepo.save(uploadFile);

        // 성공 처리 결과 반환
        fileInfo.setFileNo(uploadFile.getFileNo());
        fileInfo.setFileId(uploadFile.getFileId());

        // TODO 추후 삭제(테스트 용)
        fileInfo.setFilePath(UploadFileUtil.getFilePath(uploadFile));
        fileInfo.setFileAbsolutePath(UploadFileUtil.getFileAbsolutePath(uploadFile));
        fileInfo.setDownloadUri(UploadFileUtil.getDownloadUri(uploadFile));
        fileInfo.setDownloadUrl(UploadFileUtil.getDownloadUrl(uploadFile));

        return fileInfo;
    }

    public UploadFile getFileInfo(Long fileNo) {
        return uploadFileRepo.findById(fileNo).orElse(null);
    }

    public void delete(Long fileNo) {
        uploadFileRepo.findById(fileNo).ifPresent(f -> {
            try {
                Files.deleteIfExists(fileConfig.getBaseAbsPath().resolve(UploadFileUtil.getFilePath(f)));
            }catch(IOException e) {
                log.error("Failed to delete file - {}", UploadFileUtil.getFilePath(f));
            }
        });
        uploadFileRepo.deleteById(fileNo);
    }

    public Map<Long, String> getFileNoIdMap(List<Long> fileNoList) {
        return uploadFileRepo.findAllById(fileNoList).stream().collect(Collectors.toMap(UploadFile::getFileNo, UploadFile::getFileId));
    }

    public Map<Long, String> getFileNoPathMap(List<Long> fileNoList) {
        return uploadFileRepo.findAllById(fileNoList).stream().collect(Collectors.toMap(UploadFile::getFileNo, UploadFileUtil::getFilePath));
    }

    public Map<Long, String> getFileNoAbsolutePathMap(List<Long> fileNoList) {
        return uploadFileRepo.findAllById(fileNoList).stream().collect(Collectors.toMap( UploadFile::getFileNo, UploadFileUtil::getFileAbsolutePath ) );
    }

    public Map<Long, String> getFileNoUriMap(List<Long> fileNoList) {
        return uploadFileRepo.findAllById(fileNoList).stream().collect(Collectors.toMap(UploadFile::getFileNo, UploadFileUtil::getDownloadUri));
    }

    public Map<Long, String> getFileNoUrlMap(List<Long> fileNoList) {
        return uploadFileRepo.findAllById(fileNoList).stream().collect(Collectors.toMap(UploadFile::getFileNo, UploadFileUtil::getDownloadUrl));
    }
}
