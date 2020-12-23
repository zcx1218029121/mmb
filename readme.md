# mmb
一个简单的非spring环境下的mybatis封装


① 所有自动装配的beanbean 都是单例的无需担心反复初始化的问题。
② 所有services应该直接继承于 BaseService 泛型为mapper(目前没有实现递归向上实现的方式 主要是对泛型的了解还不够深入)

## 1.如何使用

### 1.1 创建daoService

```java
package com.loafer.test;
public class TestService extends BaseService<TaskMapper> {
}

```
1.泛型是mapper接口
### 1.2 配置 DefaultDbRegistry

```java
DefaultDbRegistry defaultDbRegistry = DefaultDbRegistry.instance;

InputStream is = Object.class.getResourceAsStream("/mybatis-config.xml");
SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(is);

defaultDbRegistry.init("{daoService 所在的包名}",sqlSessionFactory);
TestService testService = (TestService) defaultDbRegistry.getBean(TestService.class);
 
```
1. 配置daoService所在的包名
2. 配置sqlSessionFactory 工厂
```java
   DefaultDbRegistry.instance.init("app.dao", "/mybatis-config.xml");
```
1. 配置daoService所在的包名
2. 配置mybatis配置文件在Resource中的位置
### 1.3 使用
不开启事务
```java
 testService.run(TaskMapper::initTask).forEach(System.err::println);
```
开启事务 
```java
       testService.transaction(testMapper -> {
            testMapper.add("1", "test");
            testMapper.add("2", "test");
            throw new RuntimeException("errrrrrrrrrrrrr");
        });
```
1. 所有daoService run方法 会传入一个mapper并自动关闭和提交 session 
2. 所有daoService transaction方法会传入一个mapper并自动关闭和提交 session 如果抛出Exception会自动回滚
## todo
- [x] 简单的事务
- [x] 根据泛型获取mapper
- [x] 根据接口自动,找到实现类
- [ ] 多实现类配置
- [ ]  daoService在多继承的情况下也能正确装配
