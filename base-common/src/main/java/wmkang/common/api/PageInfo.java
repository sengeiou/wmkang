package wmkang.common.api;


import org.springframework.data.domain.Page;

import lombok.Getter;


@Getter
public class PageInfo {


    private int  page;
    private int  size;
    private long total;

    public PageInfo(Page<?> pageInfo) {
        page  = pageInfo.getNumber() + 1;
        size  = pageInfo.getSize();
        total = pageInfo.getTotalElements();
    }
}
