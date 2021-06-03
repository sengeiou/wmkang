package wmkang.common.logback;


import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.spi.IThrowableProxy;
import ch.qos.logback.classic.spi.ThrowableProxy;
import ch.qos.logback.core.filter.Filter;
import ch.qos.logback.core.spi.FilterReply;


public class LogbackFilter extends Filter<ILoggingEvent> {


    private String excludeClassList;


    public void setExcludeClassList(String excludeClassList) {
        this.excludeClassList = excludeClassList;
    }

    @Override
    public FilterReply decide(final ILoggingEvent event) {
        IThrowableProxy throwableProxy = event.getThrowableProxy();
        if (throwableProxy == null) {
            return FilterReply.NEUTRAL;
        }
        Throwable throwable = ((ThrowableProxy) throwableProxy).getThrowable();
        if (excludeClassList.indexOf(throwable.getClass().getName()) > -1) {
            return FilterReply.DENY;
        }
        return FilterReply.NEUTRAL;
    }
}
