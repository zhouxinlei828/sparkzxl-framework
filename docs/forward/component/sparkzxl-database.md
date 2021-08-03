# sparkzxl-data-starter

> 职能：
> 使用mybatis-plus作为curd增强框架，同时对跨库、库表、库服务 关联数据自动注入的支持，解决解决分页数据的属性或单个对象的属性 回显关联数据之痛, 支持对静态数据属性(数据字典)、动态主键数据进行自动注入，对db之上一层做了缓存，减轻对db
> 的频繁访问，对curd的接口自动生成

## POM

```xml

<dependencies>
    <dependency>
        <groupId>com.github.sparkzxl</groupId>
        <artifactId>sparkzxl-core</artifactId>
        <scope>provided</scope>
    </dependency>
    <!-- mybatis-plus -->
    <dependency>
        <groupId>com.baomidou</groupId>
        <artifactId>mybatis-plus-boot-starter</artifactId>
        <exclusions>
            <exclusion>
                <artifactId>mybatis</artifactId>
                <groupId>org.mybatis</groupId>
            </exclusion>
        </exclusions>
    </dependency>
    <dependency>
        <groupId>com.github.sparkzxl</groupId>
        <artifactId>sparkzxl-cache-starter</artifactId>
    </dependency>
    <dependency>
        <groupId>com.fasterxml.jackson.core</groupId>
        <artifactId>jackson-annotations</artifactId>
    </dependency>
    <dependency>
        <groupId>mysql</groupId>
        <artifactId>mysql-connector-java</artifactId>
        <scope>runtime</scope>
    </dependency>
    <dependency>
        <groupId>p6spy</groupId>
        <artifactId>p6spy</artifactId>
    </dependency>
    <dependency>
        <groupId>com.github.pagehelper</groupId>
        <artifactId>pagehelper-spring-boot-starter</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-configuration-processor</artifactId>
        <optional>true</optional>
    </dependency>
    <dependency>
        <groupId>com.github.sparkzxl</groupId>
        <artifactId>sparkzxl-swagger-starter</artifactId>
        <scope>provided</scope>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-validation</artifactId>
    </dependency>
</dependencies>
```

## 功能

- sql日志自动填充功能-执行 SQL 分析打印

> 可在控制台打印出完整的sql日志，自动填充参数

- 自动增删改查接口

1. mapper接口继承SuperMapper类
2. service接口类继承SuperService或者SuperCacheService类，区别在于一个实现了缓存，一个没有
3. serviceImpl实现类继承SuperServiceImpl或者AbstractSuperCacheServiceImpl
4. CurdController 实现了curd的接口自动生成，使用方式可继承SuperSimpleController类来实现自动生成curd接口

- 关联数据自动注入器

> 用于 自动解决分页数据的属性或单个对象的属性 回显关联数据之痛, 支持对静态数据属性(数据字典)、动态主键数据进行自动注入
**解决问题：** 跨库、库表、库服务 关联数据自动注入。

**使用步骤：**

1. 需要注入数据的字段上面添加注解： @InjectionField(api = DICTIONARY_ITEM_CLASS, method = DICTIONARY_ITEM_METHOD, type="EDUCATION")
2. 需要注入数据的字段类型改成：RemoteData<Long, String> 或 RemoteData<String, String> 或 RemoteData<Long, User>
3. 需要注入数据的方法标记注解：@InjectionResult 或者手动调用方法：InjectionCore.injection(Object obj)。
4. 实现具体的查询方法。

工具类中的RemoteData类的设计，灵感源于Hibernate,比如用户实体的字段改成:

```java
    @TableField("org_id")
@InjectionField(api = ORG_ID_CLASS, method = ORG_ID_METHOD, beanClass = CoreOrg.class)
private RemoteData<Long, CoreOrg> org;
```

RemoteData 对象主要有2个字段：key、data

```java
package com.github.sparkzxl.database.entity;

import cn.hutool.core.util.ObjectUtil;
import IValidatable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * description: 远程数据对象
 * <K> ID或者code 等唯一键
 * <D> 根据key 远程查询出的数据
 *
 * @author zhouxinlei
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RemoteData<K, D> implements Serializable, IValidatable {

    private K key;
    private D data;


    public RemoteData(K key) {
        this.key = key;
    }

    /**
     * 获取对象的 主键key
     *
     * @param remoteData
     * @param <K>
     * @param <D>
     * @return
     */
    public static <K, D> K getKey(RemoteData<K, D> remoteData) {
        return remoteData != null ? remoteData.getKey() : null;
    }

    public static <K, D> K getKey(RemoteData<K, D> remoteData, K def) {
        return remoteData != null && ObjectUtil.isNotEmpty(remoteData.getKey()) ? remoteData.getKey() : def;
    }

    /**
     * 获取对象的 data
     *
     * @param remoteData
     * @param <K>
     * @param <D>
     * @return
     */
    public static <K, D> D getData(RemoteData<K, D> remoteData) {
        return remoteData != null ? remoteData.getData() : null;
    }

    @Override
    public String toString() {
        String toString = key == null ? "" : String.valueOf(key);
        if (ObjectUtil.isNotEmpty(this.data) && this.data instanceof String) {
            toString = String.valueOf(data);
        }
        return toString;
    }


    /**
     * 用于Hibernate-Validator 自定义校验规则
     *
     * @return
     */
    @Override
    public Object value() {
        return this.key;
    }
}

```

其中， key 用于存储 org_id的具体值， data用于注入需要回显的数据。 比如：本例需要回显org的name 字段， org类型就设置成RemoteData<Long, String>, 并将@InjectionField
注解上标注的 orgApi.findOrgByIds 方法返回 Map<id， name> 即可。 若想要回显org的多个字段， org类型可以设置成RemoteData<Long, Org>，, 并将@InjectionField
注解上标注的 orgApi.findOrgByIds 方法返回 Map<id， Org> 即可。

1. 引入依赖

```xml

<dependency>
    <groupId>com.github.sparkzxl</groupId>
    <artifactId>sparkzxl-database-starter</artifactId>
</dependency>
```

2. 使用方式

```yaml
sparkzxl:
  injection:
    # 是否启用 远程数据 手动注入
    enabled: true
    # 是否启用 远程数据 注解注入 
    aop-enabled: true
```

3.在需要注入的对象上添加注解：@InjectionField

```java
    @TableField("org_id")
@InjectionField(api = ORG_ID_CLASS, method = ORG_ID_METHOD, beanClass = CoreOrg.class)
private RemoteData<Long, CoreOrg> org;
```

4.实现具体的查询方法 // 标记了该注解的字段 @InjectionField(api = "dictionaryItemServiceImpl", method = "findDictionaryItem", dictType =
DictionaryType.AREA_LEVEL) 需要实现的 示例

 ```java

@Repository
public class CoreOrgRepository implements ICoreOrgRepository {

    private final CoreOrgMapper coreOrgMapper;

    public CoreOrgRepository(CoreOrgMapper coreOrgMapper) {
        this.coreOrgMapper = coreOrgMapper;
    }

    @Override
    public Map<Serializable, Object> findOrgByIds(Set<Serializable> ids) {
        List<CoreOrg> orgList = getOrgs(ids);
        return MapHelper.uniqueIndex(orgList, CoreOrg::getId, (org) -> org);
    }

    private List<CoreOrg> getOrgs(Set<Serializable> ids) {
        if (ids.isEmpty()) {
            return Collections.emptyList();
        }
        List<Long> idList = ids.stream().mapToLong(Convert::toLong).boxed().collect(Collectors.toList());
        List<CoreOrg> list;
        int size = 1000;
        if (idList.size() <= size) {
            list = idList.stream().map(this.coreOrgMapper::selectById).filter(Objects::nonNull).collect(Collectors.toList());
        } else {
            list = this.coreOrgMapper.selectList(new QueryWrapper<CoreOrg>().lambda().in(CoreOrg::getId, idList).eq(CoreOrg::getStatus, true));
        }
        return list;
    }
}
```

使用demo可参考sparkzxl-cloud中sparkzxl-authority-server 用户的查询

- 分页查询：

> 使用pagehelper组件具体可参考[pagehelper官网](https://pagehelper.github.io/)

本starter封装了分页查询的入参DTO，实际业务当中可继承PageDTO，同时分页结果的组装使用静态工厂方法隐藏对象的创建，可使用PageInfoUtils.pageInfo(data)

- 自动填充功能

> 使用mybatis-plus的增强功能，继承MetaObjectHandler，实现元对象处理器接口

目的主要对自动填充的字段，创建时间，更新时间，创建人，更新人字段的自动填充功能，创建人和更新人的这都能过填充是通过request请求域内的用户进行传递，自动填充，可通过BaseContextHandler.getUserId()
获取，具体可参考BaseContextHandler类当中的方法进行选择

- 自动配置属性

```yaml
sparkzxl:
  data:
    worker-id: 0
    data-center-id: 10
```

雪花算法的数据id生成号段，不填默认

## 使用方法

1. 引入依赖

```xml

<dependency>
    <groupId>com.github.sparkzxl</groupId>
    <artifactId>sparkzxl-database-starter</artifactId>
    <version>${sparkzxl.version}</version>
</dependency>
```

# 公众号

学习不走弯路，关注公众号「凛冬王昭君」

![wechat-sparkzxl.jpg](../../images/wechat-sparkzxl.jpg)
