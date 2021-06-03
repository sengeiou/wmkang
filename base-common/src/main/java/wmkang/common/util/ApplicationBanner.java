package wmkang.common.util;


import java.io.PrintStream;
import java.net.InetAddress;
import java.net.UnknownHostException;

import org.springframework.boot.Banner;
import org.springframework.boot.SpringBootVersion;
import org.springframework.boot.ansi.AnsiColor;
import org.springframework.boot.ansi.AnsiOutput;
import org.springframework.core.SpringVersion;
import org.springframework.core.env.Environment;

/**
 * 애플리케이션 실행시, 각종 환경 정보 출력을 위한 배너
 */
public class ApplicationBanner implements Banner {


    private static final String BANNER_LINE   = "*********************************************************************";


    @Override
    public void printBanner(Environment environment, Class<?> sourceClass, PrintStream out) {

        String hostName;
        String hostAddress;
        try {
            hostName = InetAddress.getLocalHost().getHostName();
            hostAddress = InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException uhe) {
            hostName = "localhost";
            hostAddress = "127.0.0.1";
        }

        String port = null;
        try{
            port = environment.getProperty("server.port");
        }catch(Exception e){
        }

        out.println();
        out.println(BANNER_LINE);

        // COLORFUL
        out.println(AnsiOutput.toString(AnsiColor.GREEN, "    << APPLICATION INFORMATION >>"));
        out.println("* NAME            : " + AnsiOutput.toString(AnsiColor.BLUE, environment.getProperty("info.app.name").toUpperCase()));

        // BLACK&WHITE
//        out.println("    << APPLICATION INFORMATIONS >>");
//        out.println("* NAME            : " + environment.getProperty("info.app.name").toUpperCase());

        out.println("* VERSION         : " + environment.getProperty("info.app.version"));
        out.println("* DESCRIPTION     : " + environment.getProperty("info.app.description"));
        out.println("* MAIN-CLASS      : " + sourceClass.getName());
        out.println("* SPRING-PROFILES : " + environment.getProperty("spring.profiles.active"));
        out.println("* SPRING-VERSION  : " + SpringVersion.getVersion());
        out.println("* BOOT-VERSION    : " + SpringBootVersion.getVersion());
        out.println("* JAVA-VERSION    : " + System.getProperty("java.version") + " (JAVA_HOME='" + System.getProperty("java.home") + "')");  // env.getProperty("JAVA_HOME");
        out.println("* HOST-NETWORK    : " + hostName + " (" + hostAddress +  ((port==null)? "" : ":" + port) + ")");
        out.println("* STARTUP-PATH    : " + C.STARTUP_DIR);
        out.println(BANNER_LINE);
        out.println();
    }
}
