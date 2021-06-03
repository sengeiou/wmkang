package wmkang.task;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import wmkang.common.util.ApplicationBanner;
import wmkang.task.util.C;


@SpringBootApplication(scanBasePackages = C.PROJECT_BASE_PACKAGE)
public class TaskApplication {


    public static void main(String[] args) {
        SpringApplication application = new SpringApplication(TaskApplication.class);
        application.setBanner(new ApplicationBanner());
        application.run(args);
    }
}
