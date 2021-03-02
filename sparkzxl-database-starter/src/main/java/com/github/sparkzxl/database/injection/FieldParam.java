package com.github.sparkzxl.database.injection;

import com.github.sparkzxl.database.annonation.InjectionField;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

/**
 * description:
 *
 * @author: zhouxinlei
 * @date: 2021-03-02 13:38:04
*/
@Data
@AllArgsConstructor
public class FieldParam {
    private InjectionField injectionField;
    private Serializable queryKey;
    private Object curField;

}
