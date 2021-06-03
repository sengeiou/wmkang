package wmkang.domain.util;


public interface C {


    String  CR                   = "\r";
    String  LF                   = "\n";
    String  CRLF                 = CR + LF;

    String  LINE_CHAR            = System.lineSeparator();
    String  STARTUP_DIR          = System.getProperty("user.dir");

    boolean IS_MAC               = CR.equals(LINE_CHAR);
    boolean IS_UNIX              = LF.equals(LINE_CHAR);
    boolean IS_WINDOWS           = CRLF.equals(LINE_CHAR);

    String  PROJECT_BASE_PACKAGE = "wmkang";

    String  TX_MANAGER_MANAGE    = "manageTransactionManager";
    String  TX_MANAGER_SERVICE   = "serviceTransactionManager";
    String  TX_MANAGER_GLOBAL    = "globalTransactionManager";
    int     TX_TIMEOUT_SECONDS   = 7;

}
