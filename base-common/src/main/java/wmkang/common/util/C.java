package wmkang.common.util;


public interface C extends wmkang.domain.util.C {


    long    TIME_ONE_SECOND_MILLIS       = 1000;
    long    TIME_ONE_MINUTE_MILLIS       = TIME_ONE_SECOND_MILLIS * 60;
    long    TIME_ONE_HOUR_MILLIS         = TIME_ONE_MINUTE_MILLIS * 60;
    long    TIME_ONE_DAY_MILLIS          = TIME_ONE_HOUR_MILLIS * 24;

    String  FILE_DOWNLOAD_URI            = "/file/download";
    String  FILE_DOWNLOAD_TYPE           = "application/x-download";

    String  AOP_POINTCUT_EXPR_CONTROLLER = "execution(wmkang.common.api.Response wmkang..*Controller.*(..))";
    String  AOP_POINTCUT_EXPR_JDBC       = "execution(java.sql.Connection javax.sql.DataSource.getConnection(..))";

    String  ATTR_KEY_SESSION_USER        = "USER";

    String  MESSAGE_KEY_MISSING          = "Missing";


}
