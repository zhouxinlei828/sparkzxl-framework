package com.github.sparkzxl.mybatis.dto;

import java.util.List;
import lombok.Data;

/**
 * description: 删除入参
 *
 * @author zhouxinlei
 */
@Data
public class DeleteDTO<ID> {

    private List<ID> ids;

}
