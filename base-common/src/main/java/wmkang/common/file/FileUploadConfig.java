package wmkang.common.file;


import java.net.InetAddress;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;

import javax.annotation.PostConstruct;

import org.apache.commons.lang.StringUtils;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter@Setter
@ConfigurationProperties(prefix = "app.file.upload")
@Component
public class FileUploadConfig {


    private String           basePath;
    private String           subdirFormat;
    private String           fileNameFormat;
    private boolean          appendIpAddr;
    private String           acceptExtensions;
    private String           denyExtensions;
    private Path             baseAbsPath;
    private SimpleDateFormat suddirFormatter;
    private SimpleDateFormat fileNameFormatter;
    private String           ipStr;

    @PostConstruct
    public void init() throws Exception {

        baseAbsPath = Paths.get(basePath).toAbsolutePath().normalize();
        Files.createDirectories(baseAbsPath);

        if (appendIpAddr) {
            try {
                ipStr = InetAddress.getLocalHost().getHostAddress();
                ipStr = "-" + String.valueOf(Integer.parseInt(ipStr.substring(ipStr.lastIndexOf('.') + 1)) + 1000).substring(1);
            } catch (Exception e) {
                ipStr = ".999";
            }
        }

        if (StringUtils.isBlank(acceptExtensions))
            acceptExtensions = null;

        if (StringUtils.isBlank(denyExtensions))
            denyExtensions = null;

        suddirFormatter = new SimpleDateFormat(subdirFormat);
        fileNameFormatter = new SimpleDateFormat(fileNameFormat);

        log.info("# FILE-UPLOAD-CONFIG");
        log.info("> FILE_UPLOAD_BASE_PATH : {}", baseAbsPath);
        log.info("> SUB_DIR_PATTERN       : {}", subdirFormat);
        log.info("> FILE_NAME_PATTERN     : {}", (fileNameFormat + (appendIpAddr ? ipStr : "")));
        log.info("> ACCEPT-EXTENSIONS     : {}", acceptExtensions);
        log.info("> DENY-EXTENSIONS       : {}", denyExtensions);
    }

    /**
     * 확장자 블랙 리스트 검사
     */
    private boolean isBlackExtension(String extension) {
        return ((denyExtensions != null) && (denyExtensions.indexOf(',' + extension + ',') > -1));
    }

    /**
     * 확장자 화이트 리스트 검사
     */
    private boolean isWhiteExtension(String extension) {
        return ((acceptExtensions != null) && (acceptExtensions.indexOf(',' + extension + ',') > -1));
    }

    /**
     * 유효한 확장자 검사
     */
    public boolean isValidExtension(String extension) {
        if (isBlackExtension(extension)) return false;
        if (isWhiteExtension(extension)) return true;
        return false;
    }

    public String getSubDir() {
        return suddirFormatter.format(System.currentTimeMillis());
    }

    public synchronized String getNewFileId() {
        return fileNameFormatter.format(System.currentTimeMillis()) + (appendIpAddr ? ipStr : "");
    }
}
