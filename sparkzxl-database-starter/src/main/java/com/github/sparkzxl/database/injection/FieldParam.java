package com.github.sparkzxl.database.injection;

import com.github.sparkzxl.database.annonation.InjectionField;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

/**
 * description:
 *
 * @author zhouxinlei
*/
@Data
@AllArgsConstructor
public class FieldParam {
    private InjectionField injectionField;
    private Serializable queryKey;
    private Object curField;

}
