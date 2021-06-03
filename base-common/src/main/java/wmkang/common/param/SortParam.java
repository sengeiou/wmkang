package wmkang.common.param;


import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang.ArrayUtils;
//import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.domain.Sort.Order;

import lombok.Getter;
import lombok.Setter;
import wmkang.common.api.Status;
import wmkang.common.exception.ApplicationException;


@Getter@Setter
public class SortParam {


    String[][] sort;


    public Sort getSortOrder() {
        if (ArrayUtils.isEmpty(sort)) {
            return Sort.unsorted();
        }
        try {
            if (sort[0].length == 1) {
                return Sort.by(new Order(Direction.valueOf(sort[1][0]), sort[0][0]));
            }
            List<Order> orders = Arrays.stream(sort).map(s -> {
                String[] arr = s;
                return new Order(Direction.valueOf(arr[1]), arr[0]);
            }).collect(Collectors.toList());
            return Sort.by(orders);
        } catch (Exception e) {
            throw new ApplicationException(Status.PARAMETER_INVALID, "sort: Invalid format(should be 'propertyName,ASC|DESC')");
        }
    }
}
