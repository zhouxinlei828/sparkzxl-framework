package com.github.sparkzxl.database.echo.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 在某个对象的字段上标记该注解，该字段的值将被主动注入
 * <p>
 * 如： \@Echo(api = "dictionaryServiceImpl") private String nation; \@Echo(api = "dictionaryApi") private String  nation; \@Echo(api =
 * "xxx.xxx.xxx.UserApi", b private Long userId;
 * <p>
 * 强烈建议：不要对象之间互相依赖 如： User 想要注入 File， File也想注入User
 *
 * @author zhouxinlei
 * @since 2022-02-20 19:51:39
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(value = {ElementType.METHOD, ElementType.TYPE, ElementType.FIELD})
public @interface EchoField {

    /**
     * 回显到那个字段
     *
     * @return 回显到那个字段
     */
    String ref() default "";

    /**
     * 绑定查询的key(根据对象中的某个属性查询的字段key)
     * 回显自身属性
     *
     * @return 查询
     */
    String bindKey() default "";

    /**
     * 分类过滤的key(根据某个分类查询数据，适用于字典，配置，业务分类表)
     *
     * @return 分类过滤的key
     */
    String filter() default "";

    /**
     * 提供自动注入值的 查询类
     * <p/>
     * 注意： 用 @Echo(api = "xxxServiceImpl")时，要保证当前服务有 xxxServiceImpl 类. 没这个类就要用 xxxApi  (FeignClient)
     *
     * @return 查询类的Spring Name
     */
    String api();

    /**
     * 自动注入值的类型， 用于强制转换
     * <p>
     * api() 配置了FeignClient时，通过 api 调用的结果会因为序列化的关系丢失类型 如：实际返回值中 Map<Serializable, Object> 的value值为 User
     * 对象，但由于通过FeignClient调用时，会自动进行序列化和房序列化， 导致返回值Map中Object类型的value值丢失类型，故通过该参数进行类型强制转换。
     *
     * @return 待强壮的类
     */
    Class<?> beanClass() default Object.class;

    /**
     * 自动注入值是字典时，需要指定该字典的key（def_dict 表的 parent_key 字段）
     *
     * @return 字典类型
     */
    String dictType() default "";


}
