
package wmkang.task.config;


import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;

import javax.annotation.PostConstruct;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;


@Slf4j
@Getter@Setter
@RefreshScope
@Component
@ConfigurationProperties(prefix = "app.task.database")
public class DatabaseProperties {


    private String importPath;

    private String exportPath;
    private String exportFile;

    private String dropQuery;

    private String unzipPath;
    private String unzipScriptFile;


    @PostConstruct
    public void init() {

        // Initialize export path
        try {
            File file = new File(exportPath);
            if (!file.exists())
                Files.createDirectories(Paths.get(exportPath).toAbsolutePath().normalize());
        } catch (Exception e) {
            log.error("Fail to create dir", e);
        }

        // Initialize import path
        try {
            File file = new File(importPath);
            if (!file.exists())
                Files.createDirectories(Paths.get(importPath).toAbsolutePath().normalize());
        } catch (Exception e) {
            log.error("Fail to create dir", e);
        }

        // Initialize unzip path & file
        unzipPath = importPath + File.separator + "unzip";
        try {
            File file = new File(unzipPath);
            if (!file.exists())
                Files.createDirectories(Paths.get(unzipPath).toAbsolutePath().normalize());
        } catch (Exception e) {
            log.error("Fail to create dir", e);
        }
        unzipScriptFile = unzipPath + File.separator + "script.sql";
    }
}