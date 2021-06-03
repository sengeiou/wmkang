package wmkang.task.controller;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.Connection;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import org.h2.tools.RunScript;
import org.h2.tools.Script;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.zaxxer.hikari.HikariDataSource;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import wmkang.common.api.Response;
import wmkang.common.api.Status;
import wmkang.task.config.DatabaseProperties;


/**
 * 데이터베이스 백업 및 복원 기능 컨트롤러
 */
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/database")
@RestController
public class DatabaseController {


    private final DatabaseProperties backupProp;

    @Qualifier("serviceDataSource")
    private final DataSource       dataSource;


    private HikariDataSource getUserDataSource() {
        return (HikariDataSource)((AbstractRoutingDataSource)dataSource).getResolvedDefaultDataSource();
    }

    @GetMapping("/export")
    public ResponseEntity<Resource> exportDatabase(HttpServletResponse response) throws Exception {

        HikariDataSource dataSource = getUserDataSource();
        String url = dataSource.getJdbcUrl();
        String user = dataSource.getUsername();

        // 1. 백업 스크립트 실행

        String backupFilePath = backupProp.getExportFile() + getFilePostfix();
        try {
            Script.main("-url", url, "-user", user, "-script", backupFilePath, "-options", "compression", "zip");
            log.info("# DB_EXPORT - SCRIPT_EXECUTED");
        } catch (Exception e) {
            log.error("# DB_EXPORT - FAILED : Script execution failed", e);
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            return null;
        }

        // 2. 생성된 덤프 파일 다운로드

        File backupFile = new File(backupFilePath);
        if (backupFile.exists()) {
            Path filePath = Paths.get(backupFile.getAbsolutePath()).normalize();
            log.info("# DB_EXPORT - FILE_FOUND PATH/SIZE : {} ({} Bytes)", filePath, backupFile.length());
            log.info("# DB_EXPORT - SUCCESSFULLY_COMPLETED!");
            Resource resource = new UrlResource(filePath.toUri());
            return ResponseEntity.ok().contentType(MediaType.parseMediaType("application/octet-stream"))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + backupFile.getName() + "\"").body(resource);
        } else {
            log.error("# DB_EXPORT - FAILED : Exported file not found");
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            return null;
        }
    }

    @PostMapping("/import")
    public Response<?> importDatabase(@RequestParam(name="file", required = true) MultipartFile file) throws Exception {

        log.info("# DB_IMPORT - TRYING");

        // 1. 업로드 파일 유효성 검사

        if( (file.getSize() < 1 ) || !"application/x-zip-compressed".equals(file.getContentType()) ){
            log.error("# DB_IMPORT - FILED : Upload file is not valid");
            return Status.INVALID_IMPORT_FILE.getResponse();
        }

        // 2. 업로드 파일 저장 (동일 이름의 파일이 있을 경우, 업로드 파일로 교체)

        File uploadFile = new File(backupProp.getImportPath(), file.getOriginalFilename());
        Path uploadFilePath = Paths.get(uploadFile.getAbsolutePath()).normalize();
        try {
            Files.copy(file.getInputStream(), uploadFilePath, StandardCopyOption.REPLACE_EXISTING);
            log.info("# DB_IMPORT - UPLOAD_FILE_SAVED");
        } catch (Exception e) {
            log.error("# DB_IMPORT - FAILED : File uploading failed", e);
            return Status.DATABASE_IMPORT_FAIL.getResponse();
        }

        // 3. ZIP 파일(업로드 파일) 압축 해제

        File unzipScriptFile = new File(backupProp.getUnzipScriptFile());
        if (unzipScriptFile.exists())
            unzipScriptFile.delete();

        File unzipDir = new File(backupProp.getUnzipPath());
        byte[] buffer = new byte[1024 * 1024];
        try (ZipInputStream zis = new ZipInputStream(new FileInputStream(uploadFilePath.toString()))) {
            ZipEntry zipEntry = zis.getNextEntry();
            while (zipEntry != null) {
                File newFile = newFile(unzipDir, zipEntry);
                try (FileOutputStream fos = new FileOutputStream(newFile)) {
                    int len;
                    while ((len = zis.read(buffer)) > 0) {
                        fos.write(buffer, 0, len);
                    }
                }
                zipEntry = zis.getNextEntry();
            }
            zis.closeEntry();
        }

        // 4. 압축 해제 후 임포트 스크립트 파일 존재 여부 확인
        if (!unzipScriptFile.exists()) {
            return Status.INVALID_IMPORT_FILE.getResponse();
        }

        try (Connection conn = dataSource.getConnection()) {

            conn.setAutoCommit(false);

            // 5. DB에 생성되어 있던 기존 객체 삭제
            try (Statement stmt = conn.createStatement()) {
                stmt.execute(backupProp.getDropQuery());
                log.info("# DB_IMPORT - DB_OBJECTS_CLEARED");
            } catch (Exception e) {
                conn.rollback();
                log.error("# DB_IMPORT - FAILED : Fail to clearing database objects", e);
                return Status.DATABASE_IMPORT_FAIL.getResponse();
            }

            // 6. 압축 해제된 복원 스크립트 실행
            try (FileReader reader = new FileReader(backupProp.getUnzipScriptFile())) {
                RunScript.execute(conn, reader);
                log.info("# DB_IMPORT - SCRIPT_EXECUTED");
            } catch (Exception e) {
                conn.rollback();
                log.error("# DB_IMPORT - FAILED : Script execution failed", e);
                return Status.DATABASE_IMPORT_FAIL.getResponse();
            }

            conn.commit();
        }

        log.info("# DB_IMPORT - SUCCESSFULLY_COMPLETED!");
        return Response.ok();
    }


    private DateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");

    private String getFilePostfix() {
        return dateFormat.format(System.currentTimeMillis()) + ".zip";
    }

    private File newFile(File destinationDir, ZipEntry zipEntry) throws IOException {
        File destFile = new File(destinationDir, zipEntry.getName());
        String destDirPath = destinationDir.getCanonicalPath();
        String destFilePath = destFile.getCanonicalPath();
        if (!destFilePath.startsWith(destDirPath + File.separator)) {
            throw new IOException("Entry is outside of the target dir: " + zipEntry.getName());
        }
        return destFile;
    }
}
