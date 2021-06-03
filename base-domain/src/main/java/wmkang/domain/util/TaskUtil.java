package wmkang.domain.util;


import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;
import org.springframework.util.CollectionUtils;

import wmkang.domain.service.entity.Task;


public class TaskUtil {


    public static List<Integer> parsePriorTaskIds(String priorTasksStr, int selfNo) {
        if (StringUtils.isBlank(priorTasksStr))
            return new ArrayList<Integer>(0);
        return Pattern.compile(",").splitAsStream(priorTasksStr).filter(StringUtils::isNotBlank).filter(StringUtils::isNumericSpace).map(String::trim)
                .map(Integer::parseInt).filter(no -> (no != selfNo)).collect(Collectors.toList());
    }

    public static String getPriorTaskIdsStr(List<Task> priorTasks) {
        if (CollectionUtils.isEmpty(priorTasks))
            return null;
        return priorTasks.stream().map(p -> String.valueOf(p.getId())).collect(Collectors.joining(","));
    }

    public static boolean hasUncompletedTask(List<Task> taskLit) {
        if (CollectionUtils.isEmpty(taskLit))
            return false;
        return taskLit.stream().filter(t -> !t.getCompleted()).findFirst().isPresent();
    }

    public static boolean isCompletedAndHasUncompletedPriorTask(Task task) {
        return task.getCompleted() && hasUncompletedTask(task.getPriorTasks());
    }
}
