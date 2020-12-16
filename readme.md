# mmb
一个简单的非spring环境下的mybatis封装

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
1.配置daoService所在的包名
2.配置sqlSessionFactory 工厂
### 1.3 使用

```java
 testService.run(TaskMapper::initTask).forEach(System.err::println);
```
1.所有daoService run方法 会传入一个mapper并自动关闭和提交 session
