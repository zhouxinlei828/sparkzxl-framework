package com.github.sparkzxl.mongodb.dynamic;

import lombok.Data;

/**
 * description:  MongoDB数据源属性类
 *
 * @author zhouxinlei
 * @date 2021-09-03 13:29:25
 */
@Data
public class MongoDatabaseProperty {

    private String host;

    private Integer port = null;

    private String uri;

    private String database;

    private String gridFsDatabase;

    private String username;

    private String password;

}
