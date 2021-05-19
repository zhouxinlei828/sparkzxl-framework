# sparkzxl-user-starter

> 职能：
> 全局获取当前登录用户信息，与业务代码彻底解耦

## POM

```xml
<dependencies>
    <dependency>
        <groupId>com.github.sparkzxl</groupId>
        <artifactId>sparkzxl-core</artifactId>
        <scope>provided</scope>
    </dependency>
    <dependency>
        <groupId>com.github.sparkzxl</groupId>
        <artifactId>sparkzxl-cache-starter</artifactId>
        <scope>provided</scope>
    </dependency>
    <dependency>
        <groupId>org.springframework</groupId>
        <artifactId>spring-webmvc</artifactId>
    </dependency>
</dependencies>
```

## 场景

- 在现有的业务场景，获取当前登录用户信息，在业务代码中到处可见，污染了业务代码，没有较高的代码可读性，作为有原则的业务程序员，我忍不了，所以本项目横空出世

## 功能

- 在微服务中，全局获取用户信息，在各个微服务当中行走自如

## 优点

> 减少用户信息获取的频率，提高开发效率

## 原理

在登录过后，将当前登录信息存入缓存（redis，内存）中，通过token获取用户缓存数据，注入业务代码当中

## 使用方法

1. 引入依赖

```xml

<dependency>
    <groupId>com.github.sparkzxl</groupId>
    <artifactId>sparkzxl-user-starter</artifactId>
    <version>${sparkzxl.version}</version>
</dependency>
```

2. 登录用户存入缓存

```java
    private void accessToken(String username,OAuth2AccessToken oAuth2AccessToken){
        AuthUserInfo<Long> authUserInfo=authUserService.getAuthUserInfo(username);
        log.info("AuthUserInfo json is {}",JSONUtil.toJsonPrettyStr(authUserInfo));
        String buildKey=KeyUtils.generateKey(BaseContextConstant.AUTH_USER,oAuth2AccessToken.getValue());
        generalCacheService.set(buildKey,authUserInfo,(long)oAuth2AccessToken.getExpiresIn());
        }
```

2. 在controller请求入参中加入AuthUserInfo<Long> authUserInfo：示例如下

```java
@PatchMapping("/role/{id}")
public void updateAuthRoleStatus(@ApiIgnore AuthUserInfo<Long> authUserInfo){
    log.info("当前登录信息为：{}",JSONUtil.toJsonPrettyStr(authUserInfo))
}
```

AuthUserInfo<Long>  泛型Long代表主键类型

# 公众号

学习不走弯路，关注公众号「凛冬王昭君」

![wechat-sparkzxl.jpg](https://oss.sparksys.top/sparkzxl-component/wechat-sparkzxl.jpg)
