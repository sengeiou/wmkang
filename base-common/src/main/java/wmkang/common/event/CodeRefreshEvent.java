package wmkang.common.event;


import org.springframework.context.ApplicationEvent;

@SuppressWarnings("serial")
public class CodeRefreshEvent extends ApplicationEvent {


    public CodeRefreshEvent(Object source) {
        super(source);
    }
}
