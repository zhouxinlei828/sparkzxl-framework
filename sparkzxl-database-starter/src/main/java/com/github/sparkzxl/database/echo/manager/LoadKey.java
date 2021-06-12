package com.github.sparkzxl.database.echo.manager;

import com.github.sparkzxl.database.echo.annonation.EchoField;
import com.google.common.base.Objects;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * description: 封装 Echo 注解中解析出来的参数
 *
 * @author zhouxinlei
 * @date 2021-06-09 10:25:43
 */
@Data
@NoArgsConstructor
@ToString
public class LoadKey {

    /**
     * 执行查询任务的类
     * <p/>
     * api()  和 feign() 任选其一,  使用 api时，请填写实现类， 使用feign时，填写接口即可
     * 如： @Echo(api="userServiceImpl") 等价于 @Echo(feign=UserService.class)
     * 如： @Echo(api="userController") 等价于 @Echo(feign=UserApi.class)
     */
    private String api;

    /**
     * 调用方法
     */
    private String method;

    public LoadKey(EchoField rf) {
        this.api = rf.api();
        this.method = rf.method();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        LoadKey that = (LoadKey) o;
        return Objects.equal(api, that.api) && Objects.equal(method, that.method);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(api, method);
    }
}
