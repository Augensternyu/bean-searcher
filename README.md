# Bean Searcher

[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.ejlchina/bean-searcher/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.ejlchina/bean-searcher/)
[![License](https://img.shields.io/badge/license-Apache%202-4EB1BA.svg)](https://www.apache.org/licenses/LICENSE-2.0.html)
[![Troy.Zhou](https://img.shields.io/badge/%E4%BD%9C%E8%80%85-ejlchina-orange.svg)](https://github.com/ejlchina)

* 文档：https://searcher.ejlchina.com/
 
 若以上地址不可用，请访问：http://searcher.ejlchina-app.com/

* 案例：https://gitee.com/ejlchina-zhxu/bean-searcher-demo

### ⛳ v3.0 重大更新

* 支持 **热加载**
* 支持 **无注解**
* 支持 **Select 指定字段**
* 支持 **条件与运算符的约束**
* 支持 **参数过滤器**
* 支持 **字段转换器**
* 支持 **Sql 拦截器**
* 支持 **多数据源**
* 支持 **JDK 9+**

### ⁉️为什么用

虽然 **增删改** 是 hibernate 和 mybatis、data-jdbc 等等 ORM 的强项，但查询，特别是有 **多条件**、**联表**、**分页**、**排序** 的复杂的列表查询，却一直是它们的弱项。

传统的 ORM 很难用较少的代码实现一个复杂的列表检索，但 Bean Searcher 却在这方面下足了功夫，这些复杂的查询，几乎只用一行代码便可以解决。

* 例如，这样的一个典型的需求：

![](./assets/case.png)

后端需要写一个检索接口，而如果用传统的 ORM 来写，代码之复杂是可以想象的。

而 Bean Searcher 却可以：

### 💥 只一行代码实现以上功能

无论简单还是复杂，Bean Searcher 都只需一行代码：

```java
@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private BeanSearcher searcher;                      // 注入 BeanSearcher 的检索器

    @GetMapping("/index")
    public SearchResult<User> index(HttpServletRequest request) {
        // 只一行代码，实现包含 分页、组合过滤、任意字段排序、甚至统计、多表联查的 复杂检索功能
        return searcher.search(User.class, MapUtils.flat(request.getParameterMap()));
    }
	
}
```

这一行代码可实现：

* **多表联查**
* **分页搜索**
* **组合过滤**
* **任意字段排序**
* **字段统计**

例如，该接口支持如下查询：

* `/user/index?type=1&page=1&size=10`
  - 检索 type = 1 的用户，返回第 2 页，每页 10 条
* `/user/index?type=1&name=张&name-op=sw`
  - 检索 type = 1 并且 name 以 `张` 开头的用户，分页默认（第 0 页，每页 15 条）
* `/user/index?type=1&sort=age&order=desc`
  - 检索 type = 1 的用户，以 age 排序，降序输出，分页默认
* `/user/index?age-0=20&age-1=30&age-op=bt`
  - 检索 age 在 20 与 30 之间的用户

### ✨ 独创动态字段运算符，检索方式随心所欲

```java
Map<String, Object> params = MapUtils.builder()
        .field(User::getType, 1).op("eq")           // 条件：type 等于 1
        .field(User::getName, "张").op("sw")        // 条件：姓名以"张"开头
        .field(User::getAge, 20, 30).op("bt")       // 条件：年龄在 20 与 30 之间
        .field(User::getNickname, "Jack").ic()      // 条件：昵称等于 Jack, 忽略大小写
        .orderBy(User::getAge, "asc")               // 排序：年龄，从小到大
        .page(0, 15)                                // 分页：第 0 页, 每页 15 条
        .build();
SearchResult<User> result = searcher.search(User.class, params);
```

小 **DEMO 快速体验** 一下：

https://gitee.com/ejlchina-zhxu/bean-searcher-demo

### 🚀 快速开发

使用 Bean Searcher 可以极大地节省后端的复杂列表检索接口的开发时间！

* 普通的复杂列表查询只需一行代码
* 单表检索可复用原有 `Domain`，无需定义 `SearchBean`

### 🌱 集成简单

可以和任意 Java Web 框架集成，如：SpringBoot、Spring MVC、Grails、Jfinal 等等。

#### Spring Boot 项目，添加依赖即集成完毕：

```groovy
implementation 'com.ejlchina:bean-searcher-boot-stater:3.0.0'
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
implementation 'com.ejlchina:bean-searcher:3.0.0'
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

### 🔨 扩展性强

面向接口设计，用户可自定义扩展 Bean Searcher 中的任何组件！

比如你可以：
* 自定义注解，或让 Bean Searcher 识别 Hibernate 的注解（实现一个 [`DbMapping`](/bean-searcher/src/main/java/com/ejlchina/searcher/DbMapping.java)）
* 自定义参数解析器（[`ParamResolver`](/bean-searcher/src/main/java/com/ejlchina/searcher/ParamResolver.java)）来支持 JSON 形式的检索参数
* 自定义字段转换器（[`FieldConvertor`](/bean-searcher/src/main/java/com/ejlchina/searcher/FieldConvertor.java)）来支持 数据库没有的 字段类型
* 自定义数据库方言（[`Dialect`](/bean-searcher/src/main/java/com/ejlchina/searcher/Dialect.java)）来支持更多的数据库
* 等等..

### 📚 详细文档

参阅：https://searcher.ejlchina.com/

文档已完善！

### 🤝 友情接链

[**[ Sa-Token ]** 一个 JavaWeb 轻量级权限认证框架，功能全面，上手简单](https://github.com/dromara/Sa-Token)

[**[ OkHttps ]** 轻量却强大的 HTTP 客户端，前后端通用，支持 WebSocket 与 Stomp 协议](https://gitee.com/ejlchina-zhxu/okhttps)

[**[ JsonKit ]** 超轻量级 JSON 门面工具，用法简单，不依赖具体实现，让业务代码与 Jackson、Gson、Fastjson 等解耦！](https://gitee.com/ejlchina-zhxu/jsonkit)

### ❤️ 参与贡献

1.  Star and Fork 本仓库
2.  新建 Feat_xxx 分支
3.  提交代码
4.  新建 Pull Request



