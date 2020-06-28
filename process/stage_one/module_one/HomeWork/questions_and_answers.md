# 1、Mybatis 动态 sql 是做什么的？都有哪些动态 sql？简述一下动态 sql 的执行原理？

## 动态 sql 是 Mybatis 强大特性之一，可以动态的完成 sql 拼接处理，需要在 xml 中定义动态 sql 的标签

## 有哪些动态 sql
- if 作为条件判断
  ```xml
    <select>
    SELECT * FROM user WHERE 1 = 1
        <if test="username != null">
        AND username = #{username}
        </if>
    </select>
  ```

- choose、when、otherwise 类似于 switch 
  ```xml
    <select>
    SELECT * FROM user WHERE 1 = 1
        <choose>
            <when test="id != 0">
        AND id = #{id}
            </when>
            <when test="username != null">
        AND username = #{username}
            </when>
            <otherwise>
        AND 1 = 1
            </otherwise>
        </choose>
    </select>
  ```

- where 一般和if结合使用，是官方提供的正规方式，省去你写 where 1 = 1 ,会自动帮你加上去掉And 但是不会自动帮你加上And，所以where 中的第二个if 需要写and
  ```xml
    <select>
    SELECT * FROM user WHERE 1 = 1
        <where>
            <if test="username != null">
            username = #{username}
            </if>
            <if test="id != 0">
            AND id = #{id}
            </if>
        </where>
    </select>
  ```

- set 一般和if结合使用，是官方提供的正规方式数据更新方式，会帮助你动态的处理掉首行的逗号
  ```xml
    <update>
    update user
        <set>
        <if test="username != null">username=#{username}</if>
        </set>
    where id=#{id}
    </update>
  ```

- trim 用来代替where 和 set ，包含四个属性，prefix 前缀 suffix 后缀 prefixOverrides 前缀覆盖 suffixOverrides 后缀覆盖
  ```xml
    <select>
        SELECT * FROM user 
        <trim prefix = "WHERE" prefixOverrides = "AND">
            <if test="username != null">
            AND  username = #{username}
            </if>
            <if test="id != 0">
            AND  AND id = #{id}
            </if>
        </trim>
    </select>
  ```

- foreach标签运行你迭代 list map set int[] 等 index为当前序号，item是本次迭代的值（如果是map index是key item是value）
  ```xml
    <select>
        SELECT * FROM user WHERE id in
        <foreach item="item" index="index" collection="list" open="(" separator="," close=")">
        #{item}
        </foreach>
    </select>
  ```

##  动态sql的执行原理  

#### 基本介绍

`SqlNode`接口，xml中每个标签的抽象接口，分别有很多实现如`IfSqlNode`等
`SqlSource`接口，用于创建BoundSql，有实现类`DynamicSqlSource`、`ProviderSqlSource`、`RawSqlSource`、`StaticSqlSource`  
`BoundSql`类 封装了mybatis最终生产sql的类
`BaseBuilder`抽象类，是`XMLMapperBuilder`、`MapperBuilderAssistant`、`XMLScriptBuilder`、`XMLConfigBuilder`、`XMLStatementBuilder`,`SqlSourceBuilder`的抽象接口  
动态sql主要在`XMLScriptBuilder`中处理  
`SqlSessionFactoryBuilder`方法内部会使用`XMLConfigBuilder`解析属性`configLocation`中配置的路径，还会使用`XMLMapperBuilder`属性解析`mapperLocations`属性中的各个xml文件  

#### 运行步骤
1. `SqlSessionFactoryBuilder` -> `build`方法 -> 创建一个`XMLConfigBuilder` -> 调用`parse`方法 -> `mapperElement`处理mapper标签
2. 创建一个`XMLMapperBuilder`对象 -> 调用`parse`方法 -> `parsePendingStatements`方法 -> 循环遍历调用 `parseStatementNode`

``` java
 //获取一个LanguageDriver对象
 LanguageDriver langDriver = getLanguageDriver(lang);
 SqlSource sqlSource = langDriver.createSqlSource(configuration, context, parameterTypeClass);
```
3. 创建 `XMLScriptBuilder` 对象，执行解析 -> 判断是否是动态sql ->如果是动态sql-> 创建`DynamicSqlSource` -> 然后执行 `getBoundSql` 返回`boundSql`对象



# 2、Mybatis 是否支持延迟加载？如果支持，它的实现原理是什么？

支持延迟加载，mybatis延迟加载是针对于嵌套查询而言的。mybatis的延迟加载是默认关闭的。  
有两种方式：  
1. 第一种是在对应的`<collection>`或`<association>`标签上指定fetchType属性值为`lazy`
2. 第二种是通过开启全局的延迟加载，通过在配置文件的`<settings>`标签下加上配置
``` xml
 <setting name="lazyLoadingEnabled" value="true"/>
```

#### 原理
Mybatis的查询结果是由ResultSetHandler接口的handleResultSets()方法处理的。
在`createResultObject`方法中，判断是否包含子查询，如果包含会在我们正常返回类型对象的基础上创建对应的代理对象


# 3、Mybatis 都有哪些 Executor 执行器？它们之间的区别是什么？

`SimpleExecutor`  
每执行一次update或select 就开启一个Statement对象，用完关闭
`ReuseExecutor`  
执行update或select，以sql作为key查找Statement对象，存在就使用，不存在就创建，用完后，不关闭Statement对象，放置于Map内，其实就是可以重用的Statement
`BatchExecutor`  
执行update，将所有的sql都添加到批处理中，等待统一执行，它缓存了多个Statement对象


# 4、简述下 Mybatis 的一级、二级缓存（分别从存储结构、范围、失效场景。三个方面来作答）？


#### Mybatis 一级缓存
属于会话级别的换成，它存储的结构是Map。生命周期是一个sqlSession，在进行update(delete)操作后会失效

#### Mybatis 二级缓存
是用来解决多个sqlSession缓存的问题，存储结构也是Map  
作用域是mapper的同一个namespace  
同样也是在进行update(delete)操作后会失效



# 5、简述 Mybatis 的插件运行原理，以及如何编写一个插件？

#### 原理
Mybatis的插件是在四大组件进行动态代理创建时的增强
- Executor
- StatementHandler
- ParamteHandler
- ResultSetHandler

每个创建的对象都不是直接的对象，而是代理对象。

#### 样例
1. 继承一个Interceptor(拦截器)
```java
public class CustomInterceptor implements Interceptor{
 /**
     * 拦截目标对象的目标方法执行
     *
     * @param invocation
     * @return
     * @throws Throwable
     */
    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        //被代理对象
        Object target = invocation.getTarget();
        //代理方法
        Method method = invocation.getMethod();
        //方法参数
        Object[] args = invocation.getArgs();
        System.out.println("方法前执行的");
        //执行原来方法
        Object result = invocation.proceed();
        System.out.println("方法后执行的");
        return result;
    }
 
    /**
     * 包装目标对象：为目标对象创建代理对象
     *
     * @param target
     * @return
     */
    @Override
    public Object plugin(Object target) {
        //this表示当前拦截器，target表示目标对象，wrap方法利用mybatis封装的方法为目标对象创建代理对象（没有拦截的对象会直接返回，不会创建代理对象）
        Object wrap = Plugin.wrap(target, this);
        return wrap;
    }
 
    /**
     * 设置插件在配置文件中配置的参数值
     *
     * @param properties
     */
    @Override
    public void setProperties(Properties properties) {
        System.out.println(properties);
    }
```

2. 在配置文件中写入plugins标签
```xml
 <plugins>
        <plugin interceptor="<自定义的拦截器的Full Name>">
            <!-- 这个就是继承拦截器里面的 setProperties 里面获取的 Properties  -->
            <property name="name" value="name"/>
        </plugin>
 </plugins>
```
