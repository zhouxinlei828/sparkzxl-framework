# sparkzxl-database-starter

> 简介：
> 使用mybatis-plus作为curd增强框架，MyBatis-Plus (opens new window)（简称 MP）是一个 MyBatis (opens new window)的增强工具，在 MyBatis 的基础上只做增强不做改变，为简化开发、提高效率而生。

- **跨库、库表、库服务 关联数据自动注入的支持**：解决分页数据的属性或单个对象的属性 回显关联数据之痛, 支持对静态数据属性(数据字典)、动态主键数据进行自动注入
- **缓存支撑**：db之上一层做了缓存，减轻对db的频繁冲击
- **CRUD通用操作**：对单表数据逻辑的curd极简封装，同时对excel的导入导出适配

> **愿景**<br/>
> 我们的愿景是成为 MyBatis 最好的搭档，就像 魂斗罗 中的 1P、2P，基友搭配，效率翻倍。

## mybatis-plus 特性

- **无侵入**：只做增强不做改变，引入它不会对现有工程产生影响，如丝般顺滑
- **损耗小**：启动即会自动注入基本 CURD，性能基本无损耗，直接面向对象操作
- **强大的 CRUD 操作**：内置通用 Mapper、通用 Service，仅仅通过少量配置即可实现单表大部分 CRUD 操作，更有强大的条件构造器，满足各类使用需求
- **支持 Lambda 形式调用**：通过 Lambda 表达式，方便的编写各类查询条件，无需再担心字段写错
- **支持主键自动生成**：支持多达 4 种主键策略（内含分布式唯一 ID 生成器 - Sequence），可自由配置，完美解决主键问题
- **支持 ActiveRecord 模式**：支持 ActiveRecord 形式调用，实体类只需继承 Model 类即可进行强大的 CRUD 操作
- **支持自定义全局通用操作**：支持全局通用方法注入（ Write once, use anywhere ）
- **内置代码生成器**：采用代码或者 Maven 插件可快速生成 Mapper 、 Model 、 Service 、 Controller 层代码，支持模板引擎，更有超多自定义配置等您来使用
- **内置分页插件**：基于 MyBatis 物理分页，开发者无需关心具体操作，配置好插件之后，写分页等同于普通 List 查询
- **分页插件支持多种数据库**：支持 MySQL、MariaDB、Oracle、DB2、H2、HSQL、SQLite、Postgre、SQLServer 等多种数据库
- **内置性能分析插件**：可输出 Sql 语句以及其执行时间，建议开发测试时启用该功能，能快速揪出慢查询
- **内置全局拦截插件**：提供全表 delete 、 update 操作智能分析阻断，也可自定义拦截规则，预防误操作

## POM依赖

```xml

<dependencies>
    <dependency>
        <groupId>com.github.sparkzxl</groupId>
        <artifactId>sparkzxl-core</artifactId>
    </dependency>
    <!-- mybatis-plus -->
    <dependency>
        <groupId>com.baomidou</groupId>
        <artifactId>mybatis-plus-boot-starter</artifactId>
    </dependency>
    <dependency>
        <groupId>com.github.jsqlparser</groupId>
        <artifactId>jsqlparser</artifactId>
    </dependency>
    <dependency>
        <groupId>com.github.sparkzxl</groupId>
        <artifactId>sparkzxl-cache-starter</artifactId>
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
        <exclusions>
            <exclusion>
                <groupId>org.mybatis</groupId>
                <artifactId>mybatis-spring</artifactId>
            </exclusion>
            <exclusion>
                <groupId>org.mybatis</groupId>
                <artifactId>mybatis</artifactId>
            </exclusion>
        </exclusions>
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
3. serviceImpl实现类继承SuperServiceImpl或者SuperCacheServiceImpl
4. CurdController 实现了curd的接口自动生成，使用方式可继承SuperSimpleController类来实现自动生成curd接口

- 关联数据自动注入器

> 用于 自动解决分页数据的属性或单个对象的属性 回显关联数据之痛, 支持对静态数据属性(数据字典)、动态主键数据进行自动注入
**解决问题：** 跨库、库表、库服务 关联数据自动注入。

**使用步骤：**

1. 需要注入数据的字段上面添加注解：`@InjectionField(api = DICTIONARY_ITEM_CLASS, method = DICTIONARY_ITEM_METHOD, type="EDUCATION")`
2. 需要注入数据的字段类型改成：`RemoteData<Long, String>` 或 `RemoteData<String, String>` 或 `RemoteData<Long, User>`
3. 需要注入数据的方法标记注解：`@InjectionResult` 或者手动调用方法：`InjectionCore.injection(Object obj)`
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

其中， **key** 用于存储 org_id的具体值， data用于注入需要回显的数据。 比如：本例需要回显org的name 字段， org类型就设置成RemoteData<Long, String>, 并将`@InjectionField`  注解上标注的
orgApi.findOrgByIds 方法返回 Map<id， name> 即可。 若想要回显org的多个字段， org类型可以设置成`RemoteData<Long, Org>`，, 并将`@InjectionField` 注解上标注的
orgApi.findOrgByIds 方法返回 Map<id， Org> 即可。

1. 引入依赖

```xml

<dependency>
    <groupId>com.github.sparkzxl</groupId>
    <artifactId>sparkzxl-database-starter</artifactId>
</dependency>
```

2. 使用方式

```yaml
mybatis-plus:
  custom:
    injection:
      # 是否启用 远程数据 手动注入
      enabled: true
      # 是否启用 远程数据 注解注入
      aop-enabled: true
```

3.在需要注入的对象上添加注解：`@InjectionField`

```java
@TableField("org_id")
@InjectionField(api = ORG_ID_CLASS, method = ORG_ID_METHOD, beanClass = CoreOrg.class)
private RemoteData<Long, CoreOrg> org;
```

4.实现具体的查询方法 

> 标记了该注解的字段 @InjectionField(api = "dictionaryItemServiceImpl", method = "findDictionaryItem", dictType = DictionaryType.AREA_LEVEL) 
需要实现的 

示例

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

使用demo可参考`sparkzxl-cloud`中`sparkzxl-auth-server` 用户的查询

- 分页查询：

> 使用pagehelper组件具体可参考:[pagehelper官网](https://pagehelper.github.io/)

本starter封装了分页查询的入参DTO，实际业务当中可继承PageDTO，同时分页结果的组装使用静态工厂方法隐藏对象的创建，可使用PageInfoUtils.pageInfo(data)

- 自动填充功能

> 使用mybatis-plus的增强功能，继承MetaObjectHandler，实现元对象处理器接口

目的主要对自动填充的字段，创建时间，更新时间，创建人，更新人字段的自动填充功能，创建人和更新人的这都能过填充是通过request请求域内的用户进行传递，自动填充，可通过BaseContextHandler.getUserId()
获取，具体可参考`BaseContextHandler`类当中的方法进行选择

- 自动配置属性

```yaml
mybatis-plus:
  custom:
    id-type: snowflake_id
    ignore-table:
    data-center-id:
    enable-tenant: false
    injection:
      aop-enabled: true
    mapper-scan: 
```

- 多租户实现

TenantLineHandlerImpl
原理参考mybatis-plus实现 [tenantlineinnerinterceptor](https://mp.baomidou.com/guide/interceptor-tenant-line.html#tenantlineinnerinterceptor)

更多装配属性参考`CustomMybatisProperties`类 雪花算法的数据id生成号段，不填默认

## 使用方法

1. 引入依赖

```xml

<dependency>
    <groupId>com.github.sparkzxl</groupId>
    <artifactId>sparkzxl-database-starter</artifactId>
    <version>${sparkzxl.version}</version>
</dependency>
```

## 示例项目

# 公众号

学习不走弯路，关注公众号「凛冬王昭君」

![wechat-sparkzxl.jpg](../../images/wechat-sparkzxl.jpg)
