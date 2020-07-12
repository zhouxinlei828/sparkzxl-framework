
package com.sparksys.commons.cache.properties;

import lombok.Data;
/**
 * description:
 *
 * @author: zhouxinlei
 * @date: 2020-07-09 11:31:52
*/
@Data
public class Redisson {

    private int timeout;

    private String address;

    private String password;

    private int connectionPoolSize;

    private int connectionMinimumIdleSize;

    private int slaveConnectionPoolSize;

    private int masterConnectionPoolSize;

    private String[] sentinelAddresses;

    private String masterName;

}
