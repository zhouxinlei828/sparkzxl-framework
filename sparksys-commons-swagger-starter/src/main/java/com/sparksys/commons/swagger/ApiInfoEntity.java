package com.sparksys.commons.swagger;

import lombok.Data;

/**
 * description: ApiInfo
 *
 * @author: zhouxinlei
 * @date: 2020-07-10 18:13:19
 */
@Data
public class ApiInfoEntity {

    private String title;
    private String description;
    private String version;
    private String termsOfServiceUrl;
}
