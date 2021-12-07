# Bean Searcher

[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.ejlchina/bean-searcher/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.ejlchina/bean-searcher/)
[![License](https://img.shields.io/badge/license-Apache%202-4EB1BA.svg)](https://www.apache.org/licenses/LICENSE-2.0.html)
[![Troy.Zhou](https://img.shields.io/badge/%E4%BD%9C%E8%80%85-ejlchina-orange.svg)](https://github.com/ejlchina)

English | [中文](./README.zh-CN.md)

* Document：https://searcher.ejlchina.com/
* Getting start：https://juejin.cn/post/7027733039299952676
* Change log：[CHANGELOG](./CHANGELOG.md)
* Only one line of code to achieve:
  - Multi table associated query
  - Paging search
  - Any field combination filtering
  - Any field sort
  - Multi field statistics
* Architecture:

![](./assets/architecture.jpg)

### ✨ Features

* Support **one entity mapping to multi tables**
* Support **dynamic field operator**
* Support **group aggregation query**
* Support **Select | Where | From subquery**
* Support **embedded params in entity**
* Support **field converter**
* Support **sql interceptor**
* Support **sql dialect extension**
* Support **Multi data source and dynamic data source**
* Support **annotations omitting and customizing**
* Support **JDK module mechanism**
* and so on

### ⁉️WHY

#### This is not a repeating wheel

Although CREATE/UPDATE/DELETE are the strengths of Hibernate, MyBatis, DataJDBC and other ORM, queries, especially complex list queries with **multi conditions**, **multi tables**, **paging**, **sorting**, have always been their weaknesses.

Traditional ORM is difficult to realize a complex list retrieval with less code, but **Bean Searcher** has made great efforts in this regard. These complex queries can be solved in almost one line of code.

* For example, such a typical requirement：

![](./assets/case.png)

The back-end needs to write a retrieval api, and if it is written with traditional ORM, the complexity of the code is very high

But Bean Searcher can：

### 💥 Achieved with one line of code

Whether simple or complex of requirements, Bean Searcher requires only one line of code

```java
@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private BeanSearcher beanSearcher;              // Inject BeanSearcher

    @GetMapping("/index")
    public SearchResult<User> index(HttpServletRequest request) {
        // Only one line of code written here
        return beanSearcher.search(User.class, MapUtils.flat(request.getParameterMap()), new String[]{ "age" });
    }

}
```

This line of code can achieve：

* **Multi table associated query**
* **Paging search**
* **Any field combination filtering**
* **Any field sort**
* **Field statistics**

例如，该接口支持如下查询：

* `/user/index?type=1&page=1&size=10`
  - 检索 type = 1 的用户，返回第 2 页，每页 10 条
* `/user/index?type=1&name=张&name-op=sw`
  - 检索 type = 1 并且 name 以 `张` 开头的用户，默认分页（第 0 页，每页 15 条）
* `/user/index?type=1&sort=age&order=desc`
  - 检索 type = 1 的用户，以 age 排序，降序输出，默认分页
* `/user/index?onlySelect=name,age`
  - 检索 所有用户，默认分页，但只查询 name 和 age 字段
* `/user/index?selectExclude=dateCreated`
  - 检索 所有用户，默认分页，但不查询 dateCreated  字段

### ✨ Parameter builder

```java
Map<String, Object> params = MapUtils.builder()
        .field(User::getType, 1).op("eq")           // 条件：type 等于 1
        .field(User::getName, "张").op("sw")        // 条件：姓名以"张"开头
        .field(User::getAge, 20, 30).op("bt")       // 条件：年龄在 20 与 30 之间
        .field(User::getNickname, "Jack").ic()      // 条件：昵称等于 Jack, 忽略大小写
        .orderBy(User::getAge, "asc")               // 排序：年龄，从小到大
        .page(0, 15)                                // 分页：第 0 页, 每页 15 条
        .build();
SearchResult<User> result = beanSearcher.search(User.class, params);
```

**DEMO 快速体验**：

* [v3.x 的 spring-boot-demo](./bean-searcher-demos/spring-boot-demo)
* [v3.x 的 grails-demo](./bean-searcher-demos/grails-demo)
* [v2.x 的 demo](https://gitee.com/ejlchina-zhxu/bean-searcher-demo)

### 🚀 Rapid development

使用 Bean Searcher 可以极大地节省后端的复杂列表检索接口的开发时间！

* 普通的复杂列表查询只需一行代码
* 单表检索可复用原有 `Domain`，无需定义 `SearchBean`

### 🌱 Easy integration

可以和任意 Java Web 框架集成，如：SpringBoot、Spring MVC、Grails、Jfinal 等等。

#### Spring Boot 项目，添加依赖即集成完毕：

```groovy
implementation 'com.ejlchina:bean-searcher-boot-stater:3.1.2'
```

接着便可在 `Controller` 或 `Service` 里注入检索器：

```groovy
/**
 * 注入 Map 检索器，它检索出来的数据以 Map 对象呈现
 */
@Autowired
private MapSearcher mapSearcher;

/**
 * 注入 Bean 检索器，它检索出来的数据以 泛型 对象呈现
 */
@Autowired
private BeanSearcher beanSearcher;
```

#### 其它框架，使用如下依赖：

```groovy
implementation 'com.ejlchina:bean-searcher:3.1.2'
```

然后可以使用 `SearcherBuilder` 构建一个检索器：

```java
DataSource dataSource = ...     // 拿到应用的数据源

// DefaultSqlExecutor 也支持多数据源
SqlExecutor sqlExecutor = new DefaultSqlExecutor(dataSource);

// 构建 Map 检索器
MapSearcher mapSearcher = SearcherBuilder.mapSearcher()
        .sqlExecutor(sqlExecutor)
        .build();

// 构建 Bean 检索器
BeanSearcher beanSearcher = SearcherBuilder.beanSearcher()
        .sqlExecutor(sqlExecutor)
        .build();
```

### 🔨 Easy extended

面向接口设计，用户可自定义扩展 Bean Searcher 中的任何组件！

比如你可以：
* 自定义数据库映射（[`DbMapping`](/bean-searcher/src/main/java/com/ejlchina/searcher/DbMapping.java)）来实现自定义注解，或让 Bean Searcher 识别其它 ORM 的注解
* 自定义参数解析器（[`ParamResolver`](/bean-searcher/src/main/java/com/ejlchina/searcher/ParamResolver.java)）来支持 JSON 形式的检索参数
* 自定义字段转换器（[`FieldConvertor`](/bean-searcher/src/main/java/com/ejlchina/searcher/FieldConvertor.java)）来支持任意的 字段类型
* 自定义数据库方言（[`Dialect`](/bean-searcher/src/main/java/com/ejlchina/searcher/Dialect.java)）来支持更多的数据库
* 等等..

### 📚 Detailed documentation

Reference ：https://searcher.ejlchina.com/

### 🤝 Friendship links

[**[ Sa-Token ]** 一个轻量级 Java 权限认证框架，让鉴权变得简单、优雅！](https://github.com/dromara/Sa-Token)

[**[ OkHttps ]** 轻量却强大的 HTTP 客户端，前后端通用，支持 WebSocket 与 Stomp 协议](https://gitee.com/ejlchina-zhxu/okhttps)

[**[ hrun4j ]** 接口自动化测试解决方案 --工具选得好，下班回家早；测试用得对，半夜安心睡 ](https://github.com/lematechvip/hrun4j)

[**[ JsonKit ]** 超轻量级 JSON 门面工具，用法简单，不依赖具体实现，让业务代码与 Jackson、Gson、Fastjson 等解耦！](https://gitee.com/ejlchina-zhxu/jsonkit)

[**[ Free UI ]** 基于 Vue3 + TypeScript，一个非常轻量炫酷的 UI 组件库 ！](https://gitee.com/phoeon/free-ui)


### ❤️ How to contribute

1. Fork code!
2. Create your own branch: `git checkout -b feat/xxxx`
3. Submit your changes: `git commit -am 'feat(function): add xxxxx'`
4. Push your branch: `git push origin feat/xxxx`
5. submit `pull request`
