package com.github.sparkzxl.mybatis.mybatis.method;

import com.baomidou.mybatisplus.core.metadata.TableFieldInfo;
import com.baomidou.mybatisplus.extension.injector.methods.AlwaysUpdateSomeColumnById;
import lombok.NoArgsConstructor;

import java.util.function.Predicate;

/**
 * description: 修改所有的字段
 *
 * @author zhouxinlei
 * @since 2022-03-20 11:26:49
 */
@NoArgsConstructor
public class UpdateAllById extends AlwaysUpdateSomeColumnById {

    public UpdateAllById(Predicate<TableFieldInfo> predicate) {
        super("updateAllById", predicate);
    }
}
