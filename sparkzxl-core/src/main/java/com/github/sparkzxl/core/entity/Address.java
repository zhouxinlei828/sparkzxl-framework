package com.github.sparkzxl.core.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * description: 地址信息
 *
 * @author: zhouxinlei
 * @date: 2021-01-30 11:22:10
 */
@NoArgsConstructor
@Data
public class Address {
    private String formatted;
    private String streetAddress;
    private String locality;
    private String region;
    private String postalCode;
    private String country;
}
