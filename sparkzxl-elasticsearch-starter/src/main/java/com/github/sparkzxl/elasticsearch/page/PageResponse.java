package com.github.sparkzxl.elasticsearch.page;

import java.util.List;
import lombok.Data;

/**
 * description: es分页实体类
 *
 * @author zhouxinlei
 */
@Data
public class PageResponse<T> {

    protected long total;
    protected List<T> list;
    private int pageNum;
    private int pageSize;
}
