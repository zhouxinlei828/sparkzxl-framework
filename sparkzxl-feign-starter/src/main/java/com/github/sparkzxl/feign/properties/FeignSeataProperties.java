package com.github.sparkzxl.feign.properties;

import lombok.Data;
import java.util.List;

/**
 * description: CustomMybatisProperties配置属性类
 *
 * @author zhouxinlei
 */
@Data
public class FeignSeataProperties {

    private boolean enabled;

    private List<String> headerList;
}
