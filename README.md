# Mybatis

环境：

- JDK1.8
- Mysql 5.7
- maven 3.6.1
- IDEA

SSM框架：配置文件。最好方式：看官方文档 https://mybatis.net.cn/getting-started.html

## 1.简介

### 1.1、什么是Mybatis

- MyBatis 是一款优秀的**持久层框架**
- 它支持自定义 SQL、存储过程以及高级映射。
- MyBatis 免除了几乎所有的 JDBC 代码以及设置参数和获取结果集的工作。
- MyBatis 可以通过简单的 XML 或注解来配置和映射原始类型、接口和 Java POJO（Plain Old Java Objects，普通老式 Java 对象）为数据库中的记录。
- MyBatis 本是apache的一个[开源项目](https://baike.baidu.com/item/开源项目/3406069)iBatis。
- 2010年这个[项目](https://baike.baidu.com/item/项目/477803)由apache software foundation 迁移到了[google code](https://baike.baidu.com/item/google code/2346604)，并且改名为MyBatis 。
- 2013年11月迁移到[Github](https://baike.baidu.com/item/Github/10145341)。

### 1.2、持久化

数据持久化

- 持久化就是把程序的数据在持久层状态(数据库)和瞬时状态(内存)转化的过程
- 内存：**断电即失**
- 数据库(Jdbc)，io文件持久化。
- 生活：冷藏、罐头

#### 为什么需要持久化？

- 有一些对象，不能让它丢掉。
- 内存太贵

### 1.3、持久层

Dao层、Service层、Controller层...

- 完成持久化工作的代码块
- 层界限十分明显

### 1.4、为什么需要Mybatis?

- 简单易学：本身就很小且简单。没有任何第三方依赖，最简单安装只要两个jar文件+配置几个sql映射文件易于学习，易于使用，通过文档和源代码，可以比较完全的掌握它的设计思路和实现。
- 灵活：mybatis不会对应用程序或者数据库的现有设计强加任何影响。 sql写在xml里，便于统一管理和优化。通过sql语句可以满足操作数据库的所有需求。
- 解除sql与程序代码的耦合：通过提供DAO层，将业务逻辑和数据访问逻辑分离，使系统的设计更清晰，更易维护，更易单元测试。sql和代码的分离，提高了可维护性。
- 提供映射标签，支持对象与数据库的orm字段关系映射
- 提供对象关系映射标签，支持对象关系组建维护
- 提供xml标签，支持编写动态sql。

- **最重要的一点：使用的人多**

## 2、第一个Mybatis程序[代码：mybatis-01]

思路：搭建环境-->导入Mybatis-->编写代码--->测试！

### 2.1、搭建环境

搭建数据库

```mysql
create database mybatis;
use mybatis;
create table user (
   id int(20) not null primary key,
   name varchar(30) default null,
   pwd varchar(30) default null
   )engine=innodb default charset=utf8;
   
 insert into user(id,name,pwd) values
 	(1,'张三','123456'),
 	(2,'李三','123456'),
 	(3,'lisa','123456');

```

新建项目

1. 新建一个普通的maven项目
2. 删除src
3. 导入maven依赖

```xml
 <!--导入依赖-->
    <dependencies>
        <!--mysql驱动-->
        <dependency>
            <groupId>mysql</groupId>
            <artifactId>mysql-connector-java</artifactId>
            <version>8.0.19</version>
        </dependency>
        <!--mybatis-->
        <dependency>
            <groupId>org.mybatis</groupId>
            <artifactId>mybatis</artifactId>
            <version>3.5.3</version>
        </dependency>
        <!--junit-->
        <dependency>
            <groupId>junit</groupId>
            <artifactId>junit</artifactId>
            <version>4.12</version>
            <scope>test</scope>
        </dependency>
    </dependencies>
```

2.2、创建一个模块

- 编写mybatis的核心配置文件

```xml
<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE configuration
        PUBLIC "-//mybatis.org//DTD Config 3.0//EN"
        "http://mybatis.org/dtd/mybatis-3-config.dtd">
<configuration>
    <environments default="development">
        <environment id="development">
            <transactionManager type="JDBC"/><!--事务管理-->
            <dataSource type="POOLED">
                <property name="driver" value="c0m.mysql.cj.jdbc.Driver"/>
                <property name="url" value="jdbc:mysql://localhost:3306/mybatis?useSSL=true&amp;useUnicode&amp;characterEncoding=utf-8"/>
                <property name="username" value="root"/>
                <property name="password" value="123"/>
            </dataSource>
        </environment>
    </environments>
    <mappers>
        <mapper resource="org/mybatis/example/BlogMapper.xml"/>
    </mappers>
</configuration>
```

- 编写mybatis工具类

   把资源加载进来，然后拿到资源进行对此执行

```java
package com.mybatis.utils;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import java.io.IOException;
import java.io.InputStream;

/**
 * 这是mybatis的工具类，读取到mybatis的相关配置文件
 * 这些都是官方给出的文档，写死
 *
 *从 XML 中构建 SqlSessionFactory
 * 每个基于 MyBatis 的应用都是以一个 SqlSessionFactory 的实例为核心的。
 * SqlSessionFactory 的实例可以通过 SqlSessionFactoryBuilder 获得。
 * 而 SqlSessionFactoryBuilder 则可以从 XML 配置文件或一个预先配置
 * 的 Configuration 实例来构建出 SqlSessionFactory 实例。
 * @author Orange
 * @create 2021-07-28  12:09
 */
//sqlSessionFactory --> sqlSession
public class MybatisUtils {
    private static SqlSessionFactory sqlSessionFactory;
    static {//这些是mybatis的配置文件，最好一启动就可以读取部署
        try {
            //使用mybatis第一步：获取SqlSessionFactory对象
            String resource = "mybatis-config.xml";
            InputStream inputStream = Resources.getResourceAsStream(resource);
            sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    //从 SqlSessionFactory 中获取 SqlSession
    //既然有了 SqlSessionFactory，顾名思义，我们可以从中获得 SqlSession 的实例。
    // SqlSession 提供了在数据库执行 SQL 命令所需的所有方法。
    // 你可以通过 SqlSession 实例来直接执行已映射的 SQL 语句。例如：
    public static SqlSession getSqlSession(){
        SqlSession sqlSession = sqlSessionFactory.openSession();
        return sqlSession;

    }

}
```

![4](mybatis/4.png)

### 2.3、编写代码

- 实体类

```java
package com.mybatis.pojo;

/**
 * @author Orange
 * @create 2021-07-28  18:17
 */
//实体类
public class User {
    private int id;
    private String name;
    private String pwd;

    public User() {
    }

    public User(int id, String name, String pwd) {
        this.id = id;
        this.name = name;
        this.pwd = pwd;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPwd() {
        return pwd;
    }

    public void setPwd(String pwd) {
        this.pwd = pwd;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", pwd='" + pwd + '\'' +
                '}';
    }
}
```



- Dao接口

```java
package com.mybatis.dao;

import com.mybatis.pojo.User;

import java.util.List;

/**
 * @author Orange
 * @create 2021-07-28  18:30
 */
//操作实体类的接口
public interface UserDao {
    List<User> getUserList();
}
```

- 接口实现类,有原来的UserDaoImpl转换为一个Mapper配置文件

```xml
<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE mapper
        PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN"
        "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
<!--namespace:命名空间，绑定一个对应的Dao(Mapper)接口-->
<mapper namespace="com.mybatis.dao.UserDao">
    <!--select查询语句，id事对应接口里面的方法名字
    resultType=返回的类型，要指向pojo那个封装类的类型或者其他类型
    resultMap=返回的集合
    -->
    <select id="getUserList" resultType="com.mybatis.pojo.User">
        select * from user
    </select>

</mapper>
```

### 2.4、测试

##### 注意点：

异常1：org.apache.ibatis.binding.BindingException: Type interface com.mybatis.dao.UserDao is not known to the MapperRegistry.这是mybatis菜鸟级别的错误

解决1：是因为UserMapper.xml的配置文件没有写入mybatis核心配置文件

##### MapperRegistry是什么？

核心配置文件中的注册

![1](mybatis/1.png)

异常2：The error may exist in com/mybatis/dao/UserMapper.xml，找不到UserMapper.xml但是我目录是有xml文件的为什么？

解决：Maven由于它的预定大于配置文件，它固定了配置文件只能放在resources配置文件夹，蓝色的java文件夹只能放java类不能配置文件，所以导致UserMapper.xml放在java文件夹读取不到，所以要在maven下手动配置过滤

```xml
<!--在build中配置resources,来防止我们资源导出失败的问题-->
    <build>
        <resources>
            <resource>
                <directory>src/main/resources</directory>
                <includes>
                    <include>**/*.properties</include>
                    <include>**/*.xml</include>
                </includes>
                <filtering>true</filtering>
            </resource>
            <resource>
                <directory>src/main/java</directory>
                <includes>
                    <include>**/*.properties</include>
                    <include>**/*.xml</include>
                </includes>
                <filtering>true</filtering>
            </resource>
        </resources>
</build>
```

![3](../mybatis/3.png)

![2](../mybatis/2.png)

在mybatis的测试中遇到了包utf-8无效：

```xml
java.lang.ExceptionInInitializerError
	at com.mybatis.dao.UserDaoTest.test(UserDaoTest.java:20)
	at sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method)
	at sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:62)
	at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)
	at java.lang.reflect.Method.invoke(Method.java:498)
	at org.junit.runners.model.FrameworkMethod$1.runReflectiveCall(FrameworkMethod.java:50)
	at org.junit.internal.runners.model.ReflectiveCallable.run(ReflectiveCallable.java:12)
	at org.junit.runners.model.FrameworkMethod.invokeExplosively(FrameworkMethod.java:47)
	at org.junit.internal.runners.statements.InvokeMethod.evaluate(InvokeMethod.java:17)
	at org.junit.runners.ParentRunner.runLeaf(ParentRunner.java:325)
	at org.junit.runners.BlockJUnit4ClassRunner.runChild(BlockJUnit4ClassRunner.java:78)
	at org.junit.runners.BlockJUnit4ClassRunner.runChild(BlockJUnit4ClassRunner.java:57)
	at org.junit.runners.ParentRunner$3.run(ParentRunner.java:290)
	at org.junit.runners.ParentRunner$1.schedule(ParentRunner.java:71)
	at org.junit.runners.ParentRunner.runChildren(ParentRunner.java:288)
	at org.junit.runners.ParentRunner.access$000(ParentRunner.java:58)
	at org.junit.runners.ParentRunner$2.evaluate(ParentRunner.java:268)
	at org.junit.runners.ParentRunner.run(ParentRunner.java:363)
	at org.junit.runner.JUnitCore.run(JUnitCore.java:137)
	at com.intellij.junit4.JUnit4IdeaTestRunner.startRunnerWithArgs(JUnit4IdeaTestRunner.java:69)
	at com.intellij.rt.junit.IdeaTestRunner$Repeater.startRunnerWithArgs(IdeaTestRunner.java:33)
	at com.intellij.rt.junit.JUnitStarter.prepareStreamsAndStart(JUnitStarter.java:220)
	at com.intellij.rt.junit.JUnitStarter.main(JUnitStarter.java:53)
Caused by: org.apache.ibatis.exceptions.PersistenceException: 
### Error building SqlSession.
### Cause: org.apache.ibatis.builder.BuilderException: Error creating document instance.  Cause: com.sun.org.apache.xerces.internal.impl.io.MalformedByteSequenceException: 2 字节的 UTF-8 序列的字节 2 无效。
	at org.apache.ibatis.exceptions.ExceptionFactory.wrapException(ExceptionFactory.java:30)
	at org.apache.ibatis.session.SqlSessionFactoryBuilder.build(SqlSessionFactoryBuilder.java:80)
	at org.apache.ibatis.session.SqlSessionFactoryBuilder.build(SqlSessionFactoryBuilder.java:64)
	at com.mybatis.utils.MybatisUtils.<clinit>(MybatisUtils.java:31)
	... 23 more
Caused by: org.apache.ibatis.builder.BuilderException: Error creating document instance.  Cause: com.sun.org.apache.xerces.internal.impl.io.MalformedByteSequenceException: 2 字节的 UTF-8 序列的字节 2 无效。
	at org.apache.ibatis.parsing.XPathParser.createDocument(XPathParser.java:263)
	at org.apache.ibatis.parsing.XPathParser.<init>(XPathParser.java:127)
	at org.apache.ibatis.builder.xml.XMLConfigBuilder.<init>(XMLConfigBuilder.java:81)
	at org.apache.ibatis.session.SqlSessionFactoryBuilder.build(SqlSessionFactoryBuilder.java:77)
	... 25 more
Caused by: com.sun.org.apache.xerces.internal.impl.io.MalformedByteSequenceException: 2 字节的 UTF-8 序列的字节 2 无效。
	at com.sun.org.apache.xerces.internal.impl.io.UTF8Reader.invalidByte(UTF8Reader.java:701)
	at com.sun.org.apache.xerces.internal.impl.io.UTF8Reader.read(UTF8Reader.java:372)
	at com.sun.org.apache.xerces.internal.impl.XMLEntityScanner.load(XMLEntityScanner.java:1793)
	at com.sun.org.apache.xerces.internal.impl.XMLEntityScanner.scanData(XMLEntityScanner.java:1292)
	at com.sun.org.apache.xerces.internal.impl.XMLScanner.scanComment(XMLScanner.java:778)
	at com.sun.org.apache.xerces.internal.impl.XMLDocumentFragmentScannerImpl.scanComment(XMLDocumentFragmentScannerImpl.java:1039)
	at com.sun.org.apache.xerces.internal.impl.XMLDocumentFragmentScannerImpl$FragmentContentDriver.next(XMLDocumentFragmentScannerImpl.java:2985)
	at com.sun.org.apache.xerces.internal.impl.XMLDocumentScannerImpl.next(XMLDocumentScannerImpl.java:606)
	at com.sun.org.apache.xerces.internal.impl.XMLDocumentFragmentScannerImpl.scanDocument(XMLDocumentFragmentScannerImpl.java:510)
	at com.sun.org.apache.xerces.internal.parsers.XML11Configuration.parse(XML11Configuration.java:848)
	at com.sun.org.apache.xerces.internal.parsers.XML11Configuration.parse(XML11Configuration.java:777)
	at com.sun.org.apache.xerces.internal.parsers.XMLParser.parse(XMLParser.java:141)
	at com.sun.org.apache.xerces.internal.parsers.DOMParser.parse(DOMParser.java:243)
	at com.sun.org.apache.xerces.internal.jaxp.DocumentBuilderImpl.parse(DocumentBuilderImpl.java:339)
	at org.apache.ibatis.parsing.XPathParser.createDocument(XPathParser.java:261)
	... 28 more
```

解决：在maven加入utf-8

```xml
	<properties>
        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
    </properties>

```

- junit

```java
package com.mybatis.dao;

import com.mybatis.pojo.User;
import com.mybatis.utils.MybatisUtils;
import org.apache.ibatis.session.SqlSession;
import org.junit.Test;

import java.util.List;

/**
 * @author Orange
 * @create 2021-07-28  22:49
 */
public class UserDaoTest {
    @Test
    public void test(){
        /**
         * 第一步：从MybatisUtils获取到sqlsession对象
         */
        SqlSession sqlSession = MybatisUtils.getSqlSession();
        /**
         * 方式一：getMapper（执行mysql）
         * sqlSession.getMapper(UserDao.class)中的UserDao类在mybatis叫做Mapper类
         */
        //UserMapper(UserDao实现类)是UserDao(接口)实现类，两个都是一个东西，
        // 在面向接口编程，只要拿到接口类就可以实现实现类
        UserDao mapper = sqlSession.getMapper(UserDao.class);
        //实现接口的方法
        List<User> userList = mapper.getUserList();
        /**
         * 方式二：
         * 要具体到返回的类型二设对象类型，不建议用
         */
//        List<User> userList = sqlSession.selectList("com.mybatis.dao.UserDao.getUserList");

        for (User user : userList){
            System.out.println(user);
        }
        //关闭SqlSession
        sqlSession.close();

    }
}
```

可能遇到的问题:

1. 配置文件欸有注册
2. 绑定接口错误
3. 方法名不对
4. 返回类型不对
5. Maven导出资源问题

### 3.CRUD(针对XxxMapper.xml)[代码：mybatis-02]

![5](../mybatis/5.png)

#### 1.namespace

namespace中的包名要和Dao/mapper接口的包名一致!!!

#### 2.select

选择,查询语句;

- id:就是对应的namespace中的方法名
- resultType : sql语句执行的返回值
- parameterType: 传递的参数类型

1. 直接写接口(UserMapper.java)

```java
//根据id查询用户
    User getUserById(int id);
```

  2.编写对应的mapper中的sql语句(UserMapper.xml)

```xml
 <!--根据id查询用户-->
    <select id="getUserById" resultType="com.mybatis.pojo.User" parameterType="int">
        select * from user where id=#{id}
    </select>
```

  3.测试(UserMapperTest)

```java
/**
     * 测试根据id获取信息
     */
    @Test
    public void test1(){
        SqlSession sqlSession = MybatisUtils.getSqlSession();
        UserMapper mapper = sqlSession.getMapper(UserMapper.class);
        User userById = mapper.getUserById(2);
        System.out.println(userById);

        //关闭sqlSession
        sqlSession.close();

    }
```

#### 3.Insert

1. 直接写接口(UserMapper.java)

```java
    //insert一个用户
    int addUser(User user);
```

  2.编写对应的mapper中的sql语句(UserMapper.xml)

```xml

    <!--对象中的属性,可以直接取出来-->
    <insert id="addUser" parameterType="com.mybatis.pojo.User" >
        insert into user(id,name,pwd) value (#{id},#{name},#{pwd});
    </insert>
```

  3.测试(UserMapperTest)

```java
 /**
     * 插入数据
     */
    @Test
    public void test3(){
        SqlSession sqlSession = MybatisUtils.getSqlSession();
        UserMapper mapper = sqlSession.getMapper(UserMapper.class);
        User user = new User(4,"林彪","123456");
        int i = mapper.addUser(user);
        if (i>0){
            System.out.println(i);
            System.out.println("插入数据成功!!!");
        }
        //增删查改,要改动到数据库的,都要提交事务
        sqlSession.commit();
        //关闭sqlSession
        sqlSession.close();

    }
```

#### 4.Update

```xml
  <update id="updateUser" parameterType="com.mybatis.pojo.User">
        update user
        set name = #{name},pwd=#{pwd}
        where id = #{id};
    </update>
```

#### 5.Delete

```xml
<!--删除一个用户-->
    <delete id="deleteUser" parameterType="int">
        delete from user where id=#{id};
    </delete>
```

##### 思路构思

![6](../mybatis/6.png)

##### 注意:

> 增删查改,要改动到数据库的,都要提交事务:sqlSession.commit();

##### 源码

###### UserMapper类

```java
package com.mybatis.dao;

import com.mybatis.pojo.User;

import java.util.List;

/**
 * @author Orange
 * @create 2021-07-28  18:30
 */
//操作实体类的接口
public interface UserMapper {
    //查询全部用户
    List<User> getUserList();

    //根据id查询用户
    User getUserById(int id);

    //insert一个用户
    int addUser(User user);

    //修改用户
    int updateUser(User user);

    //删除一个用户
    int deleteUser(int id);
}
```

UserMapper.xml

```xml
<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE mapper
        PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN"
        "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
<!--namespace:命名空间，绑定一个对应的Dao(Mapper)接口-->
<mapper namespace="com.mybatis.dao.UserMapper">
    <!--select查询语句，id事对应接口里面的方法名字
    resultType=返回的类型，要指向pojo那个封装类的类型或者其他类型
    resultMap=返回的集合
    -->
    <select id="getUserList" resultType="com.mybatis.pojo.User">
        select * from mybatis.user
    </select>

    <!--根据id查询用户-->
    <select id="getUserById" resultType="com.mybatis.pojo.User" parameterType="int">
        select * from user where id=#{id}
    </select>

    <!--对象中的属性,可以直接取出来-->
    <insert id="addUser" parameterType="com.mybatis.pojo.User" >
        insert into user(id,name,pwd) value (#{id},#{name},#{pwd});
    </insert>

    <!--修改用户-->
    <update id="updateUser" parameterType="com.mybatis.pojo.User">
        update user
        set name = #{name},pwd=#{pwd}
        where id = #{id};
    </update>

    <!--删除一个用户-->
    <delete id="deleteUser" parameterType="int">
        delete from user where id=#{id};
    </delete>
</mapper>
```

测试类：UserDaoTest类

```java
package com.mybatis.dao;

import com.mybatis.pojo.User;
import com.mybatis.utils.MybatisUtils;
import org.apache.ibatis.session.SqlSession;
import org.junit.Test;

import java.util.List;

/**
 * @author Orange
 * @create 2021-07-28  22:49
 */
public class UserDaoTest {
    @Test
    public void test(){
        /**
         * 第一步：从MybatisUtils获取到sqlsession对象
         */
        SqlSession sqlSession = MybatisUtils.getSqlSession();
        /**
         * 方式一：getMapper（执行mysql）
         * sqlSession.getMapper(UserDao.class)中的UserDao类在mybatis叫做Mapper类
         */
        //UserMapper(UserDao实现类)是UserDao(接口)实现类，两个都是一个东西，
        // 在面向接口编程，只要拿到接口类就可以实现实现类
        UserMapper mapper = sqlSession.getMapper(UserMapper.class);
        //实现接口的方法
        List<User> userList = mapper.getUserList();
        /**
         * 方式二：
         * 要具体到返回的类型二设对象类型，不建议用
         */
//        List<User> userList = sqlSession.selectList("com.mybatis.dao.UserDao.getUserList");

        for (User user : userList){
            System.out.println(user);
        }
        //关闭SqlSession
        sqlSession.close();

    }


    /**
     * 测试根据id获取信息
     */
    @Test
    public void test1(){
        SqlSession sqlSession = MybatisUtils.getSqlSession();
        UserMapper mapper = sqlSession.getMapper(UserMapper.class);
        User userById = mapper.getUserById(2);
        System.out.println(userById);

        //关闭sqlSession
        sqlSession.close();

    }

    /**
     * 插入数据
     */
    @Test
    public void test3(){
        SqlSession sqlSession = MybatisUtils.getSqlSession();
        UserMapper mapper = sqlSession.getMapper(UserMapper.class);
        User user = new User(4,"林彪","123456");
        int i = mapper.addUser(user);
        if (i>0){
            System.out.println(i);
            System.out.println("插入数据成功!!!");
        }
        //增删查改,要改动到数据库的,都要提交事务
        sqlSession.commit();
        //关闭sqlSession
        sqlSession.close();

    }

    /**
     * 修改用户谢谢
     */
    @Test
    public void test4(){
        SqlSession sqlSession = MybatisUtils.getSqlSession();
        UserMapper mapper = sqlSession.getMapper(UserMapper.class);
        User user = new User(3,"刘备","123456");
        int i = mapper.updateUser(user);
        if (i>0){
            System.out.println("修改数据成功!!!");
        }
        //提交事务
        sqlSession.commit();
        //关闭sqlSession
        sqlSession.close();
    }

    /**
     * 删除一个用户
     */
    @Test
    public void test5(){
        SqlSession sqlSession = MybatisUtils.getSqlSession();
        UserMapper mapper = sqlSession.getMapper(UserMapper.class);
        int i = mapper.deleteUser(2);
        if (i>0){
            System.out.println("删除成功一个用户");
        }
        //提交事务
        sqlSession.commit();
        //关闭sqlSession
        sqlSession.close();
    }
}
```

#### 6.分析错误

- 标签不要匹配错误
- resource 绑定mapper,需要使用路径
- 程序配置文件必须符合规范!
- NullPointerException,没有注册到资源
- 输出的xml文件存在中文乱码问题!
- maven资源没有导出的问题(识别不出在java包写的xml配置文件)

#### 7.万能的Map

使用map的好处就是插入数据可以随便插入，这样说不清，那就看一下下面的例子

##### eg:添加一个新用户

1. 直接写接口(UserMapper.java)

```java
//使用万能map集合添加一个用户
int addUserMap(Map<String,Object> map);
```

  2.编写对应的mapper中的sql语句(UserMapper.xml)

```xml
    <!--使用map添加数据，对象中的属性,可以直接取出来-->
    <insert id="addUserMap" parameterType="map" >
        insert into user(id,name,pwd) 
        value (#{userId},#{userName},#{password});
    </insert>

```

  3.测试(UserMapperTest)

```java
 	/**
     * 使用万能的map插入数据
     */
    @Test
    public void test3_1(){
        SqlSession sqlSession = MybatisUtils.getSqlSession();
        UserMapper mapper = sqlSession.getMapper(UserMapper.class);
        HashMap<String, Object> map = new HashMap<>();
        map.put("userId",7);
        map.put("userName","vivien");
        map.put("password","123456");

        int i = mapper.addUserMap(map);
        if (i>0){
            System.out.println("插入数据成功");
        }
        //提交事务
        sqlSession.commit();
        //关闭sqlSession
        sqlSession.close();
    }
```

Map传递参数，直接在sql中取出key即可！			【parameterType="map"】

对象传递参数，直接在sql中取出对象的属性即可！ 【parameterType="Object"】

只有一个基本类型参数的情况下，可以直接在sql中取到！

多个参数用Map，**或者注解**

##### 分析：

###### 可以很清楚看出来，使用map时候，可以根据自己对用户要添加什么信息时候，就写那个信息，在第三步中创建要添加的信息，而且名字可以人自己写

```java
HashMap<String, Object> map = new HashMap<>();
        map.put("userId",7);
        map.put("userName","vivien");
        map.put("password","123456");
```

###### 那么在map写的对应的数据名，要在第二步对应起来不然，会添加不了数据

```sql
 		insert into user(id,name,pwd) 
        value (#{userId},#{userName},#{password});
```

##### 对比：

###### 使用封装类（pojo层）进行添加用户的，特别是一个用户有很多属性时候，你创建一个用户信息，要根据pojo层是的构造函数决定填写多少个属性信息，这样有点费时，现得代码很臃肿

1. 直接写接口(UserMapper.java)

```java
//insert一个用户
int addUser(User user);
```

  2.编写对应的mapper中的sql语句(UserMapper.xml)

```xml
<!--添加数据，对象中的属性,可以直接取出来-->
    <insert id="addUser" parameterType="com.mybatis.pojo.User" >
        insert into user(id,name,pwd) 
        value (#{id},#{name},#{pwd});
    </insert>
```

  3.测试(UserMapperTest)

```java
 	 public void test3(){
        SqlSession sqlSession = MybatisUtils.getSqlSession();
        UserMapper mapper = sqlSession.getMapper(UserMapper.class);
        User user = new User(6,"林o","123456");
        mapper.addUser(user);
        sqlSession.commit();
        sqlSession.close();

    }
```

###### 特别是这个的第3步，一个封装了太多属性时候，要写很多属性信息new User(6,"林o","123456",...);因此在生产环境中一般会使用Map灵活操作

#### 8.思考题

模糊查询这么写？

​	1.java代码执行的时候，传递通配符 %%

```java
List<User> users = mapper.getUserLike("%林%");
```

​	2.在sql拼接中使用通配符！写死sql语句，防止sql注入

```xml
select * from user where  name like "%"#{value}"%"
```

### 4、配置解析

```xml
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE configuration
        PUBLIC "-//mybatis.org//DTD Config 3.0//EN"
        "http://mybatis.org/dtd/mybatis-3-config.dtd">
<configuration>
    <environments default="development">
        <environment id="development">
            <transactionManager type="JDBC"/><!--事务管理-->
            <dataSource type="POOLED">
                <property name="driver" value="com.mysql.cj.jdbc.Driver"/>
                <property name="url" value="jdbc:mysql://localhost:3306/mybatis?useSSL=true&amp;useUnicode&amp;characterEncoding=UTF-8&amp;serverTimezone=UTC"/>
                <property name="username" value="root"/>
                <property name="password" value="123"/>
            </dataSource>
        </environment>
        <environment id="test">
            <transactionManager type=""></transactionManager>
            <dataSource type=""></dataSource>
        </environment>
    </environments>
    <!--每一个Mapper.xml都需要在Mybatis核心配置文件中注册-->
    <mappers>
        <mapper resource="com/mybatis/mapper/UserMapper.xml"></mapper>

    </mappers>
</configuration>
```

#### 1、核心配置文件

- mybatis-config.xml

- MyBatis的配置文件包含了会深深影响MyBatis行为的设置和属性信息

  ```xml
  configuration（配置）
      #properties（属性）
      #settings（设置）
      #typeAliases（类型别名）
      typeHandlers（类型处理器）
      objectFactory（对象工厂）
      plugins（插件）
      environments（环境配置）
      environment（环境变量）
      transactionManager（事务管理器）
      dataSource（数据源）
      databaseIdProvider（数据库厂商标识）
      mappers（映射器）
  ```

#### 2、环境配置(environments)

MyBatis可以配置成适应多种环境

**不过要记住：尽管可以配置多个环境，单每个sqlSessionFacory实例只能选择一种环境**

> 注意一些关键点:
>
> - 默认使用的环境 ID（比如：default="development"）。
> - 每个 environment 元素定义的环境 ID（比如：id="development"）。
> - 事务管理器的配置（比如：type="JDBC"）。
> - 数据源的配置（比如：type="POOLED"）。
>
> 默认环境和环境 ID 顾名思义。 环境可以随意命名，但务必保证默认的环境 ID 要匹配其中一个环境 ID。
>
> 
>
> **事务管理器（transactionManager）**：有两种类型的事务管理器（也就是 type="[JDBC|MANAGED]"）
>
> 
>
> **数据源（dataSource）**：有三种内建的数据源类型（也就是 type="[UNPOOLED|POOLED|JNDI]"）

MyBatis的默认事务管理器就是JDBC  ,   连接池：POOLED

#### 3、属性（properties）

我们可以通过properties属性来实现引用配置文件

这些属性都是可以外部配置文件且可动态替换的，既可以在典型的java属性文件种配置，亦可以通过properties元素的子元素来传递。【db.properties】

编写一个配置文件

db.properties

```properties
driver=com.mysql.cj.jdbc.Driver
url=jdbc:mysql://localhost:3306/mybatis?useSSL=true&useUnicode&characterEncoding=UTF-8&serverTimezone=UTC
username=root
password=123
```

在核心配置文件中引入

```xml
<!--引入外部配置文件-->
    <properties resource="db.properties">
        <property name="username" value="root"/>
        <property name="password" value="123456"/>
    </properties>
```

- 可以直接引入外部文件
- 可以在其中增加一些属性配置
- 如果两个文件有同一个字段，优先使用外部中配置文件的！

##### 注意：在mybatis核心配置文件中的标签元素要符合顺序

![7](../mybatis/7.png)

#### 4、类型别名(typeAliases)

- 类型别名是为java类型设置的一个段的名字。
- 存在的意义仅在用于减少类的完全限定名的冗余。

###### 方法1：

```xml
    <!--可以给实体类型期别名-->
    <typeAliases>
        <typeAlias type="com.mybatis.pojo.User" alias="User"/>
    </typeAliases>
```

###### 方法2：

也可以指定一个包名，MyBatis 会在包名下面搜索需要的 Java Bean，比如：

扫描实体类的包，在没有注解的情况下，会使用 Bean 的首字母小写的非限定类名来作为它的别名！

```xml
    <!--可以给实体类型期别名-->
    <typeAliases>
        <package name="com.mybatis.pojo"/>
    </typeAliases>
```

###### 如果给了注解，注解的名字可以随便起

![9](../mybatis/9.png)



###### 在实体类比较少的时候，使用方法1的方式

###### 如果实体类十分多，建议使用方法2的方式

###### 方法1可以DIY(自定义)别名；方法2则不行，如果非要改，需要在实体类上增加注解



那么主要作用在哪呢？举个例子

![8](../mybatis/8.png)

#### 5、设置

这是MyBatis中极为重要的调整设置，它们会改变MyBatis的运行时行为。

![10](../mybatis/10.png)

![11](../mybatis/11.png)

#### 6、其他配置

- [typeHandlers（类型处理器）](https://mybatis.net.cn/configuration.html#typeHandlers)
- [objectFactory（对象工厂）](https://mybatis.net.cn/configuration.html#objectFactory)
- plugins（插件）
  - mybatis-generator-core(自动生成crud代码，会有错)
  - mybatis-plus(和mybatis互补互利，再使用它不要写crud业务，直接简化了)
  - 通用mapper

#### 7、映射器(mappers)

MapperRegistry:注册绑定我们的Mapper文件：

###### 方式一：【推荐使用】

```xml
<!--每一个Mapper.xml都需要在Mybatis核心配置文件中注册-->
<mappers>
     <mapper resource="com/mybatis/mapper/UserMapper.xml"></mapper>
</mappers>
```

###### 方式二：

```xml
<mappers>
   <mapper class="com.mybatis.mapper.UserMapper"/>
</mappers>
```

注意点：

- 接口和它的Mapper配置文件必须同名！
- 接口和它的Mapper配置文件必须再同一个包下！



###### 方式三：使用扫描包进行注入绑定

```xml
<mappers>
	<package name="com.mybatis.mapper"/>
</mappers>
```

注意点：

- 接口和它的Mapper配置文件必须同名！
- 接口和它的Mapper配置文件必须再同一个包下！

#### 8、生命周期

![12](../mybatis/12.png)

生命周期、和作用域，是至关重要的，因为错误的使用会导致非常严重的**并发问题**

**SqlSessionFactoryBulider:**

- 一旦创建了SqlSessionFactory，就不再需要它了
- 局部变量

**SqlSessionFactory**:

- 说白了就是可以想象为：数据库连接池
- SqlSessionFactory一旦被创建就应该应用运行期间一直存在，**没有任何理有丢弃它或者重新创建另一个实例。**
- 因此SqlSessionFactory的最佳作用域是应用作用域。
- 最简单的就是使用**单例模式**或者静态单例模式。

**SqlSession**

- 连接到连接池的一个请求！

- SqlSession的实例不是线程安全的，因此是不能被共享的，所以它的最佳的作用域是请求或方法作用域。

- 用完之后需要赶紧关闭，否则资源被占用！

  ![13](../mybatis/13.png)

这里面的每一个Mapper，就代表一个具体的业务！

### 5、解决属性姓名和字段名不一致的问题[代码：mybatis=03]

#### 1、问题

数据库中的字段

![](../mybatis/14.png)

新建一个项目，拷贝之前的，测试实体类字段不一致的情况,eg：对数据库字段封装的java类（与数据库字段名不一致）

```java
public class User {
    private int id;
    private String name;
    private String password;
}
```

测试出现的问题

![15](../mybatis/15.png)

```sql
//select * from user where id=#{id}
//类处理器
//select id,name,pwd from user where id=#{id}
```

解决方法：

- 起别名

```xml
<!--根据id查询用户-->
<select id="getUserById" resultType="user" parameterType="int">
    select id,name,pwd as password from user where id=#{id}
</select>
```

#### 2、resultMap

结果集映射

```xml
id  name  pwd
id  name  password
```

```xml
<!--结果集映射-->
<resultMap id="userMap" type="user">
     <!-- column数据库中的字段， property实体类中的属性-->
     <result column="id" property="id"/>
     <result column="name" property="name"/>
     <result column="pwd" property="password"/>
</resultMap>
<!--根据id查询用户-->
<select id="getUserById" resultMap="userMap" parameterType="int">
     select * from user where id=#{id}
</select>
```

- resultMap元素是Mybatis中最重要强大的元素

- ResultMap的设计思想是，对于简单的语句根本不需要配置显式的结果映射，而对于复杂一点的语句只需要描述它们的关系就行了。

- ResultMap最优秀额地方在于，虽然你已经对它相当于了解，但是根本就不需要显示地用到他们。

- 如果世界总是这么简单就好了。

  

  **后面再学**

  在复杂的数据库时候，不能运用到上面的单一结果集映射，需要用到高级结果映射，eg：

  ```xml
  <!-- 非常复杂的语句 -->
  <select id="selectBlogDetails" resultMap="detailedBlogResultMap">
    select
         B.id as blog_id,
         B.title as blog_title,
         B.author_id as blog_author_id,
         A.id as author_id,
         A.username as author_username,
         A.password as author_password,
         A.email as author_email,
         A.bio as author_bio,
         A.favourite_section as author_favourite_section,
         P.id as post_id,
         P.blog_id as post_blog_id,
         P.author_id as post_author_id,
         P.created_on as post_created_on,
         P.section as post_section,
         P.subject as post_subject,
         P.draft as draft,
         P.body as post_body,
         C.id as comment_id,
         C.post_id as comment_post_id,
         C.name as comment_name,
         C.comment as comment_text,
         T.id as tag_id,
         T.name as tag_name
    from Blog B
         left outer join Author A on B.author_id = A.id
         left outer join Post P on B.id = P.blog_id
         left outer join Comment C on P.id = C.post_id
         left outer join Post_Tag PT on PT.post_id = P.id
         left outer join Tag T on PT.tag_id = T.id
    where B.id = #{id}
  </select>
  ```

  它是一个非常复杂的结果映射（假设作者，博客，博文，评论和标签都是类型别名）。 不用紧张，我们会一步一步地来说明。虽然它看起来令人望而生畏，但其实非常简单。

  ```xml
  <!-- 非常复杂的结果映射 -->
  <resultMap id="detailedBlogResultMap" type="Blog">
    <constructor>
      <idArg column="blog_id" javaType="int"/>
    </constructor>
    <result property="title" column="blog_title"/>
    <association property="author" javaType="Author">
      <id property="id" column="author_id"/>
      <result property="username" column="author_username"/>
      <result property="password" column="author_password"/>
      <result property="email" column="author_email"/>
      <result property="bio" column="author_bio"/>
      <result property="favouriteSection" column="author_favourite_section"/>
    </association>
    <collection property="posts" ofType="Post">
      <id property="id" column="post_id"/>
      <result property="subject" column="post_subject"/>
      <association property="author" javaType="Author"/>
      <collection property="comments" ofType="Comment">
        <id property="id" column="comment_id"/>
      </collection>
      <collection property="tags" ofType="Tag" >
        <id property="id" column="tag_id"/>
      </collection>
      <discriminator javaType="int" column="draft">
        <case value="1" resultType="DraftPost"/>
      </discriminator>
    </collection>
  </resultMap>
  ```

  ### 6、日志[代码：mybatis-04-review]

  ##### 6.1、日志工厂

  如果一个数据库操作，出现了异常，我们需要排错。日志就是最好的助手！

  曾经:   sout 、 debug

  现在：日志工厂！

   ![10](../Mybatis.assets/10.png)

>  SLF4J 
>
> LOG4J 	【掌握】	
>
>  LOG4J2 
>
>  JDK_LOGGING 
>
>  COMMONS_LOGGING 
>
>  STDOUT_LOGGING  	【掌握】
>
>  NO_LOGGING

在Mybatis中具体使用那个一日志实现，在设置中设定！

 **STDOUT_LOGGING标准日志输出**(Mybatis自带这个日志包，其他日志设置需要导入包)

在Mybatis核心配置文件中，配置我们的日志！

```xml
<!--设置日志-->
<settings>
    <setting name="logImpl" value="STDOUT_LOGGING"/>
</settings>
```

![16](../mybatis/16.png)

##### 6.2、Log4j

什么是log4j?

- 可以控制日志信息输送的目的地是[控制台](https://baike.baidu.com/item/控制台/2438626)、文件、[GUI](https://baike.baidu.com/item/GUI)组件，甚至是套接口服务器、[NT](https://baike.baidu.com/item/NT/3443842)的事件记录器、[UNIX](https://baike.baidu.com/item/UNIX) [Syslog](https://baike.baidu.com/item/Syslog)[守护进程](https://baike.baidu.com/item/守护进程/966835)等
- 可以控制每一条日志的输出格式
- 通过定义每一条日志信息的级别，我们能够更加细致地控制日志的生成过程
- 通过一个[配置文件](https://baike.baidu.com/item/配置文件/286550)来灵活地进行配置，而不需要修改应用的代码

1.导入log4j的包

```xml
<!-- https://mvnrepository.com/artifact/log4j/log4j -->
<dependency>
    <groupId>log4j</groupId>
    <artifactId>log4j</artifactId>
    <version>1.2.17</version>
</dependency>
```

2.log4j.properties

```properties
# 将等级为DEBUG的日志信息输出到console和file这两个目的地：console和file的定义在下面的代码
log4j.rootLogger=debug,CONSOLE,file

# 控制台暑促的相关设置
# 指定输出信息到控制台
log4j.appender.CONSOLE=org.apache.log4j.ConsoleAppender
# thread属性，指定日志level.根据日志的重要程度，可以分为off,fatal,error,warn,info,debug。
log4j.appender.CONSOLE.Threshold=DEBUG
log4j.appender.CONSOLE.Target=System.out
log4j.appender.CONSOLE.Encoding= UTF-8
log4j.appender.CONSOLE.layout=org.apache.log4j.PatternLayout
log4j.appender.CONSOLE.layout.ConversionPattern=[%c]-%m%n

##文件输出的相关设置
log4j.appender.file=org.apache.log4j.RollingFileAppender
#生成文件地址
log4j.appender.file.File=./log/zhang.log
log4j.appender.file.MaxFileSize=10MB
log4j.appender.file.Threshold=DEBUG
log4j.appender.file.layout=org.apache.log4j.PatternLayout  
log4j.appender.file.layout.ConversionPattern=[%p][%d{yy-MM-dd}] [%c]%m%n
# 每天的日志打印就不需要设置大小属性
# log4j.appender.file.MaxFileSize=20MB
# log4j.appender.file.MaxBackupIndex=10

##日志输出级别
log4j.logger.org.mybatis=DEBUG
log4j.logger.java.sql=DEBUG
log4j.logger.java.sql.Statement=DEBUG
log4j.logger.java.sql.ResultSet=DEBUG
log4j.logger.java.sql.PreparedStatement=DEBUG
```

3.配置log4j为日志的实现

```xml
 <!--设置日志-->
<settings>
	<setting name="logImpl" value="LOG4J"/>
</settings>
```

4.Log4j的使用！直接运行测试代码

![17](../mybatis/17.png)

**简单使用**

1.在要使用log4j的类中，导入包 import org.apache.log4j.Logger;

2.日志对象，参数为当前类的class

```java
static Logger logger = Logger.getLogger(UserMapperTest.class);
```

3.日志级别

```java
logger.info("info:进入testLog4j");
logger.debug("debug:进入testLog4j");
logger.error("error:进入testLog4j");
```

具体代码

```java
package com.mybatis.mapper;


import com.mybatis.pojo.User;
import com.mybatis.utils.MybatisUtils;
import org.apache.ibatis.session.SqlSession;
import org.apache.log4j.Logger;
import org.junit.Test;


/**
 * @author Orange
 * @create 2021-08-01  22:07
 */
public class UserMapperTest {
    //括号是填写一个类，想那个类输出就选那个类
    static Logger logger = Logger.getLogger(UserMapperTest.class);

    @Test
    public void test1(){
        SqlSession sqlSession = MybatisUtils.getSqlSession();
        UserMapper mapper = sqlSession.getMapper(UserMapper.class);
        logger.info("成功进入到test1方法");
        User userById = mapper.getUserById(1);
        System.out.println(userById);

        //关闭sqlSession
        sqlSession.close();

    }
    @Test
    public void testLog4j(){
        //log4j的级别
        //info：信息
        logger.info("info:进入testLog4j");
        // 调试
        logger.debug("debug:进入testLog4j");
        //紧急错误
        logger.error("error:进入testLog4j");
    }
}

```

### 7、分页[代码：mybatis-04-review]

**思考：为什么要分页**

- 减少数据的处理量

#### 7.1使用Limit分页

```sql
语法：select * from user limit startIndex,pagesSize;#startIndex（页数）,pagesSize（一页显示的数据量）
select * from user limit 3; #[0,3]
```

使用Mybatis实现分页，核心SQL

1. 接口

```java
//分页
    List<User> getUserByLimit(Map<String,Integer> map);
```

2.Mapper.xml

```xml
<!--分页-->
<select id="getUserByLimit" parameterType="map"  resultMap="userMap">
    select * from user limit #{startIndex},#{pageSize}
</select>
```

3.测试

```java
@Test
public void getUserByLimit(){
   SqlSession sqlSession = MybatisUtils.getSqlSession();
   UserMapper mapper = sqlSession.getMapper(UserMapper.class);
   Map<String, Integer> map = new HashMap<>();
   map.put("startIndex",0);
   map.put("pageSize",2);
   List<User> userByLimit = mapper.getUserByLimit(map);
   for (User user:userByLimit){
       System.out.println(user);
    }
    sqlSession.close();
}
```

#### 7.2、 RowBounds分页

不再使用SQL实现分页

1.接口

```java
//分页2
List<User> getUserByRowBounds();
```

2.mapper.xml

```xml
<!--分页2-->
<select id="getUserByRowBounds" resultMap="userMap">
    select * from user
</select>
```

3.测试

```java
/**
 * 使用RowBound分页，是完全使用java的面对对象思想
*/
@Test
public void getUserByRowBounds(){
    SqlSession sqlSession = MybatisUtils.getSqlSession();

    //RowBounds实现分页
    RowBounds rowBounds = new RowBounds(1,2);

    //通过java代码层面实现分页
    List<User> listUser = sqlSession.selectList("com.mybatis.mapper.UserMapper.getUserByRowBounds",null,rowBounds);

    for (User user : listUser) {
        System.out.println(user);
    }
    sqlSession.close();
}
```

#### 7.3、分页插件

![18](mybatis/18.png)

了解即可，万一以后公司的架构师，需要使用，需要知道它是什么东西!

### 8.使用注解开发[代码：mybatis-05]

#### 8.1面向接口编程

-大家之前都学过面向对象编程，也学习过接口，但在真正的开发中，很多时候我们会选择面向接口编程

**-根本原因：解耦、可拓展、提高复用、分层开发中，上层不用管具体的实现，大家都遵守共同的标准，使得开发变得容易，规范性更好**

-在一个面向对象的系统中，系统的各种功能是由许许多多的不同对象协作完成的。在这种情况下，各个对象内部是如何实现自己的，对系统设计人员来说就不那么重要了；

-而各个对象之间的协作关系则成为系统设计的关键。小到不同类之间的通信，大到各模块之间的交互，在系统设计之初都是要着重要考虑，这也是系统设计的主要工作内容。面向接口编程是指按照这种思想来编程。

**关于接口的理解**

-接口从更深层次的理解，应是定义(规范、约束)与实现(名实分离的原则)的分离。

-接口的本身反映了系统设计人员对系统的抽象理解

-接口应有的两类：

​	-第一类是对一个个体的抽象，它可对应为一个抽象体(abstract class)

​	-第二类是对一个个体某一方面的抽象，即形成一个抽象面(interface);

-一个体有可能有多个抽象面。抽象体与抽象面是有区别的。

**三个面像的区别**

-面向对象是指，我们考虑问题是，一对象为单位，考虑它的属性即方法。

-面向过程是指，我们考虑问题时，以一个具体的流程（事务过程）为单位，考虑它的实现。

-接口设计与非接口设计时针对复用技术而言，与面向对象(过程)，不是一个问题，更多的体现就是对系统整体的架构。

#### 8.2、使用注解开发

1.注解在接口上实现

```java
public interface UserMapper {
    //使用注解
    @Select("select * from user")
    List<User> getUser();

}
```

2.需要在核心配置文件中绑定接口！

```xml
<mappers>
    <mapper class="com.mybatis.mapper.UserMapper"></mapper>
</mappers>
```

3.测试

```java
import com.mybatis.mapper.UserMapper;
import com.mybatis.pojo.User;
import com.mybatis.utils.MybatisUtils;
import org.apache.ibatis.session.SqlSession;
import org.junit.Test;

import java.util.List;

/**
 * @author Orange
 * @create 2021-08-07  17:17
 */
public class UserMapperTest {
    @Test
    public void test(){
        SqlSession sqlSession = MybatisUtils.getSqlSession();
        //底层主要应用反射
        UserMapper mapper = sqlSession.getMapper(UserMapper.class);
        List<User> user = mapper.getUser();
        for (User user1 : user) {
            System.out.println(user1);
        }
        sqlSession.close();
    }
}
```

本质：反射机制实现

底层：动态代理!

**Mybatis详细的执行流程！（结合debug去了解）**

![19](../mybatis/19.jpg)

#### 8.3、CRUD

我们可以在工具类创建的时候实现自动提交事务！

```java
public static SqlSession getSqlSession(){
    //如果openSession()里面的参数是true的话，就变成了事务自动commit
    //不用手动sql提交commit
    return sqlSessionFactory.openSession(true);
}
```

编写接口，增加注解

```xml
package com.mybatis.mapper;

import com.mybatis.pojo.User;
import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.Map;

/**
* @author Orange
* @create 2021-07-28  18:30
*/
//操作实体类的接口
public interface UserMapper {
    //根据id查找
    //方法存在多个参数，所有的参数前面必须加上:@Param("")注解
    @Select("select * from user where id=#{id}")
    User getUserById(@Param("id") int id);
    //    User getUserById(@Param("id") int id,@Param("name") String name);

    //增加数据
    @Insert("insert into user values(#{id},#{name},#{pwd})")
    int addUser(User user);

    //改数据
    @Update("update user set id=#{id},name=#{name} where id = #{id}")
    int updateUser(User user);

    //删除数据
    @Delete("delete from user where id = #{uid}")
    int deleteUserById(@Param("uid") int id);
    }

```

测试类

【注意：我们必须要讲接口注册绑定到我们的核心配置文件中！】

```java
import com.mybatis.mapper.UserMapper;
import com.mybatis.pojo.User;
import com.mybatis.utils.MybatisUtils;
import org.apache.ibatis.session.SqlSession;
import org.junit.Test;
import java.util.List;

/**
 * @author Orange
 * @create 2021-08-07  17:17
 */
public class UserMapperTest {
  
    //根据id查询
    @Test
    public void getUserById(){
        SqlSession sqlSession = MybatisUtils.getSqlSession();
        UserMapper mapper = sqlSession.getMapper(UserMapper.class);
        User userById = mapper.getUserById(6);
        System.out.println(userById);
        sqlSession.close();
    }

    //插入数据
    @Test
    public void addUser(){
        SqlSession sqlSession = MybatisUtils.getSqlSession();
        UserMapper mapper = sqlSession.getMapper(UserMapper.class);
        User user = new User(8,"lisa","21235");
        int addUser = mapper.addUser(user);
        //在工具类中sqlSessionFactory.openSession(true)，选择了自动提交事务
        //        sqlSession.commit();
        sqlSession.close();
    }

    //修改数据
    @Test
    public void updateUser(){
        SqlSession sqlSession = MybatisUtils.getSqlSession();
        UserMapper mapper = sqlSession.getMapper(UserMapper.class);
        int updateUser = mapper.updateUser(new User(6, "Jack", "12334"));
        System.out.println(updateUser);
        sqlSession.close();
    }

    //删除用户信息
    @Test
    public void deleteUserById(){
        SqlSession sqlSession = MybatisUtils.getSqlSession();
        UserMapper mapper = sqlSession.getMapper(UserMapper.class);
        int i = mapper.deleteUserById(6);
        System.out.println(i);
        sqlSession.close();
    }
}
```

**关于@Param()注解**

- 基本类型的参数或者String类型，需要加上
- 引用类型不需要加
- 如果只有一个基本类型的话，可以忽略，但建议大家都加上！
- 我们在SQL中引用的就是我们这里的@Param()中设定的属性名！



**#{}     ${}区别**



### 9、Lombok

> Project Lombok is a java library that automatically plugs into your editor and build tools, spicing up your java.
> Never write another getter or equals method again, with one annotation your class has a fully featured builder, Automate your logging variables, and much more.

-  java library
- plugs
- build tools
- with one annotation your class

使用步骤：

1.在IDEA中安装Lombok插件!

2.在项目中导入lombok的jar包

```xml
<!-- https://mvnrepository.com/artifact/org.projectlombok/lombok -->
<dependency>
    <groupId>org.projectlombok</groupId>
    <artifactId>lombok</artifactId>
    <version>1.18.12</version>
</dependency>

```

3.在实体类上加注解即可！

```java
package com.mybatis.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author Orange
 * @create 2021-07-28  18:17
 */
//实体类
@Data
@AllArgsConstructor
@NoArgsConstructor
public class User {
    private int id;
    private String name;
    private String pwd;
    
}

```



```java
@Getter and @Setter
@FieldNameConstants
@ToString
@EqualsAndHashCode
@AllArgsConstructor, @RequiredArgsConstructor and @NoArgsConstructor
@Log, @Log4j, @Log4j2, @Slf4j, @XSlf4j, @CommonsLog, @JBossLog, @Flogger, @CustomLog
@Data   		#使用在java是实体类
@Builder
@SuperBuilder
@Singular
@Delegate
@Value
@Accessors
@Wither
@With
@SneakyThrows
@val
@var
experimental @var
@UtilityClass
Lombok config system
Code inspections
Refactoring actions (lombok and delombok)
```

说明：

```java
@Data：无参构造器,get,set,toString,equals,hashcode
@AllArgsConstructor：创建有参构造器(在只有@Data和它是，没有无参构造器了)
@NoAllArgsConstructor:创建无参构造器
@EqualsAndHashCode
@ToString
@Getter
```

### 10.多对一处理(复杂查询)[代码：mybatis-06]



![19](mybatis/19.png)

- 多个学生，对应一个老师
- 对于学生这边而言 ， **关联**..多个学生，关联一个老师【多对一】
- 对于老师而言，**集合**，一个老师，有多个学生【一对多】

SQL:

```sql
use mybatis;

create table teacher(
	id int(10)  not null,
    name varchar(30) default null,
    primary key(id)
)engine=innodb default charset=UTF8

insert into teacher(id,name) values(1,'五老师')

create table student(
	id int(10)  not null,
    name varchar(30) default null,
    tid int(10) default null,
    primary key(id),
    key fktid (tid),
    constraint fktid foreign key (tid) references teacher (id)
)engine=innodb default charset=utf8

insert into student(id,name,tid) values(1,'lisa',1);
insert into student(id,name,tid) values(2,'make',1);
insert into student(id,name,tid) values(3,'amos',1);
insert into student(id,name,tid) values(4,'jon',1);
insert into student(id,name,tid) values(5,'marria',1);

```

![20](../mybatis/20.png)

#### 测试环境搭建

1.搭建好mybatis环境

2.导入lombok

3.新建实体类 Teacher,Student

```java
student类：
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public class Student {
        private int id;
        private String name;
        //学生需要关联一个老师
        private Teacher teacher;
    }

teacher类：
    @Data
    public class Teacher {
        private int id;
        private String name;
    }


```

4.建立Mapper接口

```java
package com.mybatis.mapper;

import com.mybatis.pojo.Student;


import java.util.List;

/**
 * @author Orange
 * @create 2021-08-09  21:22
 */
public interface StudentMapper {
//    @Select("select * from student")
    List<Student> getStudent();
}

```

```java
package com.mybatis.mapper;

import com.mybatis.pojo.Teacher;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @author Orange
 * @create 2021-08-09  21:23
 */
public interface TeacherMapper {
//    @Select("select * from teacher")
    List<Teacher> getTeacher();
}

```



5.建立Mapper.xml文件

```xml
<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE mapper
        PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN"
        "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
<mapper namespace="com.mybatis.mapper.TeacherMapper">

    <select id="getTeacher" resultType="teacher">
        select * from teacher
    </select>

</mapper>
```

```xml
<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE mapper
        PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN"
        "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
<mapper namespace="com.mybatis.mapper.StudentMapper">
    <select id="getStudent" resultMap="student">
        select * from student
    </select>
  
</mapper>
```

6.在核心配置文件中绑定注册我们的Mapper接口或者文件！【方式很多，随心选】

```xml
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE configuration
        PUBLIC "-//mybatis.org//DTD Config 3.0//EN"
        "http://mybatis.org/dtd/mybatis-3-config.dtd">
<configuration>
    <!--引入外部配置文件-->
    <properties resource="db.properties">
    </properties>

    <!--可以给实体类型期别名-->
    <typeAliases>
        <!--        <typeAlias type="com.mybatis.pojo.User" alias="user"/>-->
        <!--可以使用注解随便起别名，否则只能是pojo开头小写类名-->
        <package name="com.mybatis.pojo"/>
    </typeAliases>

    <environments default="development">
        <environment id="development">
            <transactionManager type="JDBC"/><!--事务管理-->
            <dataSource type="POOLED">
                <property name="driver" value="${driver}"/>
                <property name="url" value="${url}"/>
                <property name="username" value="${username}"/>
                <property name="password" value="${password}"/>
            </dataSource>
        </environment>
    </environments>

    <!--每一个Mapper.xml都需要在Mybatis核心配置文件中注册-->
    <mappers>
        <mapper resource="com/mybatis/mapper/TeacherMapper.xml"></mapper>
        <mapper resource="com/mybatis/mapper/StudentMapper.xml"></mapper>
<!--        <package name="com.mybatis.mapper"/>-->
    </mappers>
</configuration>
```

7.测试查询是否能够成功！

```java
package com.mybatis.mapper;

import com.mybatis.pojo.Student;
import com.mybatis.pojo.Teacher;
import com.mybatis.utils.MybatisUtils;
import org.apache.ibatis.session.SqlSession;
import org.junit.Test;

import java.util.List;

/**
 * @author Orange
 * @create 2021-08-09  21:32
 */
public class MapperTest {
    @Test
    public void getTeacher(){
        SqlSession sqlSession = MybatisUtils.getSqlSession();
        TeacherMapper mapper = sqlSession.getMapper(TeacherMapper.class);
        List<Teacher> teacher = mapper.getTeacher();
        for (Teacher tea : teacher) {
            System.out.println(tea);
        }
        sqlSession.close();
    }

    @Test
    public void getStudent(){
        SqlSession sqlSession = MybatisUtils.getSqlSession();
        StudentMapper mapper = sqlSession.getMapper(StudentMapper.class);
        List<Student> studentList = mapper.getStudent();
        for (Student student : studentList) {
            System.out.println(student);
        }

        sqlSession.close();
    }
}

```

#### 多对一

##### 如何查询Student信息时候，也把每个学生归属的老师查出来？

![21](../mybatis/21.png)

###### 如果使用sql语句就是

```sql
select * from student a,teacher b where a.tid=b.id
```

![22](../mybatis/22.png)

但是在mybatis中不可以直接使用这个语句，需要使用复杂查询语句：嵌套查询

##### 方法一：按照查询嵌套处理（多表查询）

直接就修改StudentMapper.xml即可

```xml
<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE mapper
        PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN"
        "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
<mapper namespace="com.mybatis.mapper.StudentMapper">

    <!--
        思路：
        1.查询所有的学生信息
        2.根据查询出来的学生的tid，寻找对应的老师！
    -->
    <select id="getStudent" resultMap="studentTeacher">
        select * from student
    </select>
    <resultMap id="studentTeacher" type="student">
        <result property="id" column="id"/><!--这是映射数据库名字和实体类名字-->
        <result property="name" column="name"/>
        <!--复杂的属性需要单独处理
            对象：association
            集合：collection
        -->
        <!--要为下面的语句要赋值一个类型为Teacher：javaType="teacher"
        嵌套查询：select="getTeacher"
        -->
        <association property="teacher" column="tid" javaType="teacher" select="getTeacher"/>
    </resultMap>

    <select id="getTeacher" resultType="teacher">
        select * from teacher where id=#{id}
    </select>

</mapper>
```

![23](../mybatis/23.png)

#### 方法二：按照结果嵌套处理

```xml
<!--按照结果嵌套处理-->
<select id="getStudent2" resultMap="getStudentTeacher2">
    select s.id sid,s.name sname,t.name tname from student s,teacher t
    where s.tid=t.id
</select>
<!--使用结果集合-->
<resultMap id="getStudentTeacher2" type="student">
    <result property="id" column="id"/>
    <result property="name" column="name"/>
    <association property="teacher" javaType="teacher">
        <result property="name" column="tname"/>
    </association>
</resultMap>
```

回顾Mysql多对一查询方式：

- 子查询
- 联表查询

### 11、一对多处理[代码：mybatis-07]

比如：一个老师拥有多个学生！

对于老师而言，就是一对多的关系！

1.环境搭建，和刚才一样

##### 实体类

```java
package com.mybatis.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Orange
 * @create 2021-08-09  21:20
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Student {
    private int id;
    private String name;
    private int tid;
}
```

```java
package com.mybatis.pojo;

import lombok.Data;

import java.util.List;

/**
 * @author Orange
 * @create 2021-08-09  21:21
 */
@Data
public class Teacher {
    private int id;
    private String name;
    //一个老师拥有多个学生
    private  List<Student> student;

```

##### 按照结果嵌套查询

```xml
 <!--按结果嵌套查询-->
    <select id="getTeacher" resultMap="getTeacherStudents">
        select s.id sid ,t.name tname , s.name sname ,t.id tid
        from student s,teacher t
        where s.tid=t.id and t.id=#{tid}
    </select>
    <resultMap id="getTeacherStudents" type="teacher">
        <result property="id" column="tid"/>
        <result property="name" column="tname"/>
        <!--复杂的属性，我们需要单独处理 对象：association ; 集合:collection
            javaType=""：指定属性类型
            集合中的泛型信息，我们使用ofType=""
        -->
        <collection property="students" ofType="student">
            <result property="id" column="sid"/>
            <result property="name" column="sname"/>
            <result property="tid" column="tid"/>
        </collection>
    </resultMap>
```

##### 按照查询嵌套查询

```xml
<select id="getTeacher2" resultMap="getTeacherStudents2">
        select * from teacher where id =#{tid}
    </select>

    <resultMap id="getTeacherStudents2" type="teacher">
        <collection property="students" javaType="ArrayList" ofType="student" select="getStudentById" column="id"/>
    </resultMap>
    <select id="getStudentById" resultType="student">
        select * from student where tid = #{tid}
    </select>
```

#### 小结

1.关联-association	【多对一】

2.集合-collection	【一对多】

3.javaType   &	ofType

​	1.用来指定实体类中属性的类型

​	2.ofType用来指定映射到List或集合中的pojo实体类类型，泛型中的约束类型！



在生产环境中我们不可能写这种简单的sql，面对千万级别数据量要使用索引等等

我们现在写的是属于 **慢SQL   我们10000s     人家 1s**

### 面试高频

- Mysql引擎
- InnoDB底层原理
- 索引
- 索引优化！

### 12、动态SQL [mybatis-08]

##### 什么是动态SQL：动态SQL就是指根据不同的条件生成不同的SQL语句

利用动态SQL这一特性可以彻底摆脱这种痛苦。

```xml
如果你之前用过 JSTL 或任何基于类 XML 语言的文本处理器，你对动态 SQL 元素可能会感觉似曾相识。在 MyBatis 之前的版本中，需要花时间了解大量的元素。借助功能强大的基于 OGNL 的表达式，MyBatis 3 替换了之前的大部分元素，大大精简了元素种类，现在要学习的元素种类比原来的一半还要少。

if
choose (when, otherwise)
trim (where, set)
foreach
```

##### 搭建环境

```sql
create table blog(
	id varchar(50) not null comment '博客id',
    title varchar(100) not null comment '博客标题',
    author varchar(30) not null comment '博客作者',
    create_time datetime not null comment '创建时间',
    views int(30) not null comment'浏览器'
)engine=innodb default charset=utf8
```

创建一个基础工程

​	1.导包

​	2.编写配置文件

```
package com.mybatis.utils;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import java.io.IOException;
import java.io.InputStream;

//sqlSessionFactory --> sqlSession
public class MybatisUtils {
    private static SqlSessionFactory sqlSessionFactory;
    static {
        try {
            String resource = "mybatis-config.xml";
            InputStream inputStream = Resources.getResourceAsStream(resource);
            sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static SqlSession getSqlSession(){ 
       return sqlSessionFactory.openSession();


    }

}
```

​	3.编写实体类

```java
package com.mybatis.pojo;

import lombok.Data;

import java.util.Date;

/**
 * @author Orange
 * @create 2021-08-12  23:26
 */
@Data
public class Blog {
    private String id;
    private String title;
    private String author;
    private Date createTime;
    private int views;
}
```

​	4.编写实体类对应Mapper接口和Mapper.xml文件

#### IF

Mapper接口：

```java
//查询博客
List<Blog> queryBlogIF(Map map);
```

Mapper.xml

```xml
 <!--查询博客-->
    <select id="queryBlogIF" resultType="blog" parameterType="map">
        select * from blog where 1=1
        <if test="title != null">
            and title = #{title}
        </if>
        <if test="author != null">
            and author=#{author}
        </if>
    </select>
```

Test类

```java
//动态SQL语句
    @Test
    public void queryBlogIF(){
        SqlSession sqlSession = MybatisUtils.getSqlSession();
        BlogMapper mapper = sqlSession.getMapper(BlogMapper.class);
        HashMap<String, Object> map = new HashMap<>();
//        map.put("title","Mybatis如此简单");
        map.put("author","Orange");
        List<Blog> blogs = mapper.queryBlogIF(map);
        for (Blog blog : blogs) {
            System.out.println(blog);
        }
        sqlSession.close();
    }
```

#### choose (when, otherwise)

mapper接口：

```java
//查询博客，使用mybatisSQl的choose
List<Blog> queryBlogChoose(Map map);
```

mapper.xml:

```xml
<!--
	choose:只能执行一个语句，如果语句都成立按顺序执行第一个
-->
<select id="queryBlogChoose" parameterType="map" resultType="blog">
    select * from blog
    <where>
        <choose>
            <when test="title != null">
                title=#{title}
            </when>
            <when test="author != null">
                and author = #{author}
            </when>
            <otherwise>
                and views=#{views}
            </otherwise>
        </choose>
    </where>
</select>
```

Test类:

```java
 @Test
    public void queryBlogChoose(){
        SqlSession sqlSession = MybatisUtils.getSqlSession();
        BlogMapper mapper = sqlSession.getMapper(BlogMapper.class);
        HashMap map = new HashMap<>();
        map.put("title","Mybatis如此简单");
//        map.put("author","Orange");
        map.put("views",999);
        List<Blog> blogs = mapper.queryBlogChoose(map);
        for (Blog blog : blogs) {
            System.out.println(blog);
        }
        sqlSession.close();
    }
```





#### trim (where, set)

> *where* 元素只会在子元素返回任何内容的情况下才插入 “WHERE” 子句。而且，若子句的开头为 “AND” 或 “OR”，*where* 元素也会将它们去除。

```xml
<select id="queryBlogIF" resultType="blog" parameterType="map">
    select * from blog 
    <where>
        <if test="title != null">
            title = #{title}
        </if>
        <if test="author != null">
            and author=#{author}
        </if>
    </where>

</select>
```

> *set* 元素会动态地在行首插入 SET 关键字，并会删掉额外的逗号（这些逗号是在使用条件语句给列赋值时引入的）。

```xml
<update id="updateBlog" parameterType="map">
        update blog
        <set>
            <if test="title !=null">
                title=#{title},
            </if>
            <if test="author != null">
                author=#{author},
            </if>
        </set>
        where id = #{id}
    </update>
```

> trim 元素是重新定义where、set这些规则。eg:如果 *where* 元素与你期望的不太一样，你也可以通过自定义 trim 元素来定制 *where* 元素的功能。比如，和 *where* 元素等价的自定义 trim 元素为：
>
> ```
> <trim prefix="WHERE" prefixOverrides="AND |OR ">
>   ...
> </trim>
> ```
>
> *prefixOverrides* 属性会忽略通过管道符分隔的文本序列（注意此例中的空格是必要的）。上述例子会移除所有 *prefixOverrides* 属性中指定的内容，并且插入 *prefix* 属性中指定的内容。
>
> *set* 元素等价的自定义 *trim* 元素吧：
>
> ```
> <trim prefix="SET" suffixOverrides=",">
>   ...
> </trim>
> ```

##### 所谓的动态SQL，本质还是SQL语句，只是我们可以在SQL层面，去执行一个逻辑代码



### Foreach

```sql
select * from user where 1=1 and
(id=1 or id=2 or id=3)
等价于：

select * from user where 1=1 and
<foreach item="id" collection="ids"
	open=")" separator="or" close=")">
</foreach>
```

![24](../mybatis/24.png)

Mapper接口

```java
//查询第1-2-3号记录的博客
List<Blog> queryBlogForeach(Map map);
```

Mapper.xml

```xml
<!--
        select * from blog where 1=1 and (id=1 oor id=2 or id=3)
        我们现在传递一个万能的map，这map中存在一个集合
    -->
<select id="queryBlogForeach" parameterType="map" resultType="blog">
    select * from blog
    <where>
        <foreach collection="ids" item="id"
                 open="and (" separator="or" close=")">
            id=#{id}
        </foreach>
    </where>
</select>
```

Test类

```java
//测试使用SQL的foreach
@Test
public void queryBlogForeach(){
    SqlSession sqlSession = MybatisUtils.getSqlSession();
    BlogMapper mapper = sqlSession.getMapper(BlogMapper.class);
    HashMap<String, Object> map = new HashMap<>();

    ArrayList<Integer> ids = new ArrayList<Integer>();
    ids.add(1);
    ids.add(2);
    ids.add(3);
    map.put("ids",ids);
    List<Blog> blogs = mapper.queryBlogForeach(map);
    for (Blog blog : blogs) {
        System.out.println(blog);
    }
    sqlSession.close();
}
```

动态SQL就是在拼接SQL语句，我们只有保证SQL的正确性，按照SQL的格式，去排列组合就可以了

建议：

- 现在中的Mybatis中写出完整的SQL再对应的去修改成为我们的动态SQL实现通用即可！

#### SQL片段

有时候，我们可能会将一些功能的部分抽取出来，方便复用！

​	1.使用SQL标签抽取公共的部分

```sql
<sql id="if-title-author">
     <if test="title != null">
        and title = #{title}
     </if>
     <if test="author != null">
        and author=#{author}
     </if>
</sql>
```

​	2.在需要使用的地方使用include标签引用即可

```sql
<!--查询博客-->
<select id="queryBlogIF" resultType="blog" parameterType="map">
    select * from blog 
    <where>
        <include refid="if-title-author"></include>
	</where>
</select>
```

注意事项：

- 最好基于单表来定义SQL片段
- 不要存在where标签

###  13、缓存

#### 13.1、简介

```
查询  --->   连接数据库  ：耗资源！
	一次查询结果，给他暂存在一个可以直接取到的地方！---->内存  ：缓存

我们再次查询相同数据的时候，直接走缓存，就不用走数据库了
```

1.什么是缓存[Cache]?

- 存在内存中的临时数据。
- 将用户经常查询的数据放在缓存(内存)中，用户去查询数据就不用从磁盘上(关系型数据库数据文件)查询，从缓存中查询，从而提高查询效率，解决了高并发系统的性能问题。

2.为什么使用缓存？

- 减少和数据库的交互次数，减少系统的开销，提高系统效率。

3.什么样的数据能使用缓存？

- 经常查询并不经常改变的数据。【可以使用缓存】

#### 13.2、Mybatis缓存

- Mybatis包含一个非常强大的查询缓存特性，它可以非常方便地定制和配置缓存，缓存可以极大的提升查询效率。

- Mybatis系统中默认定义了两级缓存：**一级缓存**和**二级缓存**
  - 默认情况下，只有一级缓存开启。
  - 二级缓存需要手动开启和配置，他是基于namespace（接口）级别的缓存。
  - 为了提高扩展性，Mybatis定义了缓存接口Cache。我们可以通过实现Cache接口来定义二级缓存

#### 13.3、一级缓存

- 一级缓存也叫本地缓存
  - 与数据库同一次会话期间查询到的数据回放在本地缓存中
  - 以后如果需要获取相同的数据，直接从缓存中拿走，没必须再去查询数据库；

测试步骤：

​	1.开启日志！

```xml
<!--在核心配置文件-->
<!--设置日志-->
<settings>
    <setting name="logImpl" value="STDOUT_LOGGING"/>
    <!--下面是把数据库的经典字段名(带下划线)自动变成驼峰命名，与实体类(使用驼峰命名)一致-->
    <setting name="mapUnderscoreToCamelCase" value="true"/>
</settings>
```

​	2.编写接口和业务

接口：

```java
package com.mybatis.dao;

import com.mybatis.pojo.User;
import org.apache.ibatis.annotations.Param;

/**
 * @author Orange
 * @create 2021-08-19  0:05
 */
public interface UserMapper {

    //查询一个用户
    User queryUserById(@Param("id") int id);

    //修改用户
    int updateUser(User user);
}
```

Mapper.xml

```xml
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE mapper
        PUBLIC "-//mybatis.org//DTD Config 3.0//EN"
        "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
<mapper namespace="com.mybatis.dao.UserMapper">
    <select id="queryUserById" resultType="user" parameterType="_int">
        select * from user where id = #{id}
    </select>

    <update id="updateUser" parameterType="user">
        update user set name=#{name},pwd=#{pwd} where id=#{id}
    </update>

</mapper>
```



​	3.测试再一个SqlSession中查询两次相同记录

```java
import com.mybatis.dao.UserMapper;
import com.mybatis.pojo.User;
import com.mybatis.utils.MybatisUtils;
import org.apache.ibatis.session.SqlSession;
import org.junit.Test;

/**
 * @author Orange
 * @create 2021-08-19  15:59
 */
public class MyTest {
    //通过id查询用户信息
    @Test
    public void queryUserById(){
        SqlSession sqlSession = MybatisUtils.getSqlSession();
        UserMapper mapper = sqlSession.getMapper(UserMapper.class);
        User user = mapper.queryUserById(1);
        System.out.println(user);
        //mapper.updateUser(new User(2,"ccc","11111"));
        System.out.println("==================");
        User user1 = mapper.queryUserById(1);
        System.out.println(user1);
        System.out.println(user==user1);
        sqlSession.close();
    }
}

```

​	4.查看日志输出

![26](../mybatis/26.png)



缓存失效的情况：

1.查询不同的东西

2.增删改操作，可能会改变原来数据，则必定会刷新缓存！

```java
@Test
public void queryUserById(){
    SqlSession sqlSession = MybatisUtils.getSqlSession();
    UserMapper mapper = sqlSession.getMapper(UserMapper.class);
    User user = mapper.queryUserById(1);
    System.out.println(user);

    mapper.updateUser(new User(2,"ccc","11111"));

    System.out.println("==================");
    User user1 = mapper.queryUserById(1);
    System.out.println(user1);
    System.out.println(user==user1);
    sqlSession.close();
}
```

![27](../mybatis/27.png)

3.查询不同的Mapper.xml(sqlSession可能都不一样了，肯定失效)

4.手动清理缓存

```java
@Test
public void queryUserById(){
    SqlSession sqlSession = MybatisUtils.getSqlSession();
    UserMapper mapper = sqlSession.getMapper(UserMapper.class);
    User user = mapper.queryUserById(1);
    System.out.println(user);

    //mapper.updateUser(new User(2,"ccc","11111"));
    sqlSession.clearCache();//手动清理缓存

    System.out.println("==================");
    User user1 = mapper.queryUserById(1);
    System.out.println(user1);
    System.out.println(user==user1);
    sqlSession.close();
}
```

![28](../mybatis/28.png)

小结：一级缓存默认是开启的，只在一次SqlSession中有效，也就是拿到连接到关闭这个连接这个区间段！（那么一级缓存有啥用？--》但我们一直刷新一个页面是，就不用重复读取内存数据库查，直接再缓存上查，速度快很多）

**一级缓存就是一个mapper**

#### 13.4、二级缓存(代码省略，主要是思路)

- 二级缓存也叫全局缓存，一级缓存作用域太低了，所以诞生了二级缓存
- 基于namespace级别的缓存，一个名称空间，对应一个二级缓存；
- 工作机制

  - 一个会话查询一条数据，这个数据就会被放在当前会话的一级缓存中；
  - 如果当前会话关闭了，这个会话对应的一级缓存就没了；但是我们想要的是，会话关闭了，一级缓存中的数据被保存到二级缓存中；
  - 新的会话查询信息，就可以从二级缓存中获取内容；
  - 不同的mapper查出的数据会放在自己对应的缓存(map)中;


步骤：

​	1.开启全局缓存(再mybatis默认开启，但是我们要显式出来)

```xml
<!--显式开启缓存-->
<setting name="cacheEnabled" value="true"/>
```

​	2.在使用二级缓存是，要在mapper.xml上，SQL 映射文件中添加一行：

```xml
<cache/>
```

也可以自定义参数

```xml
<cache
  eviction="FIFO"
  flushInterval="60000"
  size="512"
  readOnly="true"/>
```

> 这个更高级的配置创建了一个 FIFO 缓存，每隔 60 秒刷新，最多可以存储结果对象或列表的 512 个引用，而且返回的对象被认为是只读的，因此对它们进行修改可能会在不同线程中的调用者产生冲突。
>
> 可用的清除策略有：
>
> - `LRU` – 最近最少使用：移除最长时间不被使用的对象。
> - `FIFO` – 先进先出：按对象进入缓存的顺序来移除它们。
> - `SOFT` – 软引用：基于垃圾回收器状态和软引用规则移除对象。
> - `WEAK` – 弱引用：更积极地基于垃圾收集器状态和弱引用规则移除对象。
>
> 默认的清除策略是 LRU。
>
> flushInterval（刷新间隔）属性可以被设置为任意的正整数，设置的值应该是一个以毫秒为单位的合理时间量。 默认情况是不设置，也就是没有刷新间隔，缓存仅仅会在调用语句时刷新。
>
> size（引用数目）属性可以被设置为任意正整数，要注意欲缓存对象的大小和运行环境中可用的内存资源。默认值是 1024。
>
> readOnly（只读）属性可以被设置为 true 或 false。只读的缓存会给所有调用者返回缓存对象的相同实例。 因此这些对象不能被修改。这就提供了可观的性能提升。而可读写的缓存会（通过序列化）返回缓存对象的拷贝。 速度上会慢一些，但是更安全，因此默认值是 false。

3.测试

​	会有报错：当在mapper.xml只添加了“<cache/>“二级缓存标识，就会报错！原因是我们没有给pojo实体类进行序列化(我们最好都把实体类进行序列化)  --->  我们需要将实体类序列化！

```java
Caused by: java.io.NotSerializableException: com.mybatis.pojo.User
```



```java
 //通过id查询用户信息
    @Test
    public void queryUserById(){
        SqlSession sqlSession = MybatisUtils.getSqlSession();
        SqlSession sqlSession2 = MybatisUtils.getSqlSession();
        UserMapper mapper = sqlSession.getMapper(UserMapper.class);
        User user = mapper.queryUserById(1);
        System.out.println(user);
        sqlSession.close();//第一个会话结束后，一级缓存会存导二级缓存


        System.out.println("==================");
        
        UserMapper mapper2 = sqlSession2.getMapper(UserMapper.class);
        User user1 = mapper2.queryUserById(1);
        System.out.println(user1);
        System.out.println(user==user1);
        sqlSession2.close();
    }
```



![29](../mybatis/29.png)



小结：

- 只要开启了二级缓存，在同一个Mapper下有效
- 所有的数据都会先放在一级缓存中；
- 只有当会话提交，或者关闭的时候，才会提交到二级缓存中！

#### 13.5、缓存原理

 ![30](../mybatis/30.png)

#### 13.6、自定义缓存-ehcache

> Ehcache是一个广泛使用的开源java分布式缓存。主要面向通用缓存

要在程序中使用ehcahe,先要导包！

```xml
<!-- https://mvnrepository.com/artifact/org.mybatis.caches/mybatis-ehcache -->
<dependency>
    <groupId>org.mybatis.caches</groupId>
    <artifactId>mybatis-ehcache</artifactId>
    <version>1.1.0</version>
</dependency>
```



在Mapper中指定使用我们的ehcache缓存实现！

```xml
<!--再当前Mapper.xml中使用二级缓存-->
<cache type="org.mybatis.caches.ehcache.EhcacheCache"/>
```



ehcache.xml

```xml
<?xml version="1.0" encoding="UTF-8"?>
<ehcache xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:noNamespaceSchemaLocation="http://ehcache.org/ehcache.xsd"
         updateCheck="false">
    <!--
       diskStore：为缓存路径，ehcache分为内存和磁盘两级，此属性定义磁盘的缓存位置。参数解释如下：
       user.home – 用户主目录
       user.dir  – 用户当前工作目录
       java.io.tmpdir – 默认临时文件路径
     -->
    <diskStore path="java.io.tmpdir/Tmp_EhCache"/>

    <defaultCache
            eternal="false"
            maxElementsInMemory="10000"
            overflowToDisk="false"
            diskPersistent="false"
            timeToIdleSeconds="1800"
            timeToLiveSeconds="259200"
            memoryStoreEvictionPolicy="LRU"/>

    <cache
            name="cloud_user"
            eternal="false"
            maxElementsInMemory="5000"
            overflowToDisk="false"
            diskPersistent="false"
            timeToIdleSeconds="1800"
            timeToLiveSeconds="1800"
            memoryStoreEvictionPolicy="LRU"/>
    <!--
          defaultCache：默认缓存策略，当ehcache找不到定义的缓存时，则使用这个缓存策略。只能定义一个。
        -->
    <!--
      name:缓存名称。
      maxElementsInMemory:缓存最大数目
      maxElementsOnDisk：硬盘最大缓存个数。
      eternal:对象是否永久有效，一但设置了，timeout将不起作用。
      overflowToDisk:是否保存到磁盘，当系统宕机时
      timeToIdleSeconds:设置对象在失效前的允许闲置时间（单位：秒）。仅当eternal=false对象不是永久有效时使用，可选属性，默认值是0，也就是可闲置时间无穷大。
      timeToLiveSeconds:设置对象在失效前允许存活时间（单位：秒）。最大时间介于创建时间和失效时间之间。仅当eternal=false对象不是永久有效时使用，默认是0.，也就是对象存活时间无穷大。
      diskPersistent：是否缓存虚拟机重启期数据 Whether the disk store persists between restarts of the Virtual Machine. The default value is false.
      diskSpoolBufferSizeMB：这个参数设置DiskStore（磁盘缓存）的缓存区大小。默认是30MB。每个Cache都应该有自己的一个缓冲区。
      diskExpiryThreadIntervalSeconds：磁盘失效线程运行时间间隔，默认是120秒。
      memoryStoreEvictionPolicy：当达到maxElementsInMemory限制时，Ehcache将会根据指定的策略去清理内存。默认策略是LRU（最近最少使用）。你可以设置为FIFO（先进先出）或是LFU（较少使用）。
      clearOnFlush：内存数量最大时是否清除。
      memoryStoreEvictionPolicy:可选策略有：LRU（最近最少使用，默认策略）、FIFO（先进先出）、LFU（最少访问次数）。
      FIFO，first in first out，这个是大家最熟的，先进先出。
      LFU， Less Frequently Used，就是上面例子中使用的策略，直白一点就是讲一直以来最少被使用的。如上面所讲，缓存的元素有一个hit属性，hit值最小的将会被清出缓存。
      LRU，Least Recently Used，最近最少使用的，缓存的元素有一个时间戳，当缓存容量满了，而又需要腾出地方来缓存新的元素的时候，那么现有缓存元素中时间戳离当前时间最远的元素将被清出缓存。
   -->
</ehcache>
```



#### 在java注解中@SuppressWarnings("all")//抑制警告





# GET和POST的区别？

> > **GET产生一个TCP数据包；POST产生两个TCP数据包。**
>
> 具体点说来就是：
>
> *对于GET方式的请求，浏览器会把http header和data一并发送出去，服务器响应200（返回数据）；*
>
> *而对于POST，浏览器先发送header，服务器响应100 continue，浏览器再发送data，服务器响应200 ok（返回数据）。*
>
> 这样看起来，因为POST需要两步，时间上消耗的要多一点，所以GET比POST更有效。那么可不可以用GET替换POST来优化网站性能？
>
> \1. GET与POST都有自己的语义，不能随便混用。
>
> \2. 如果网络环境好的话，发一次包的时间和发两次包的时间差别基本可以无视。如果网络环境差的话，两次包的TCP在验证数据包完整性上，有非常大的优点。
>
> \3. 并不是所有浏览器都会在POST中发送两次包，Firefox就只发送一次。
>
> 另外，w3schools上面的参考答案也是可以酌情说一些的=。=
>
> - GET在浏览器回退时是无害的，而POST会再次提交请求。
> - GET产生的URL地址可以被Bookmark，而POST不可以。
> - GET请求会被浏览器主动cache，而POST不会，除非手动设置。
> - GET请求只能进行url编码，而POST支持多种编码方式。
> - GET请求参数会被完整保留在浏览器历史记录里，而POST中的参数不会被保留。
> - GET请求在URL中传送的参数是有长度限制的，而POST么有。
> - 对参数的数据类型，GET只接受ASCII字符，而POST没有限制。
> - GET比POST更不安全，因为参数直接暴露在URL上，所以不能用来传递敏感信息。
> - GET参数通过URL传递，POST放在Request body中。

