package org.sbear.sqlSession;

import org.sbear.pojo.Configuration;
import org.sbear.pojo.MappedStatement;

import java.lang.reflect.*;
import java.util.List;
import java.util.Map;

/**
 * @author xxyWi
 */
public class DefaultSqlSession implements SqlSession {

    private Configuration configuration;

    public DefaultSqlSession(Configuration configuration) {
        this.configuration = configuration;
    }

    @Override
    public <E> List<E> selectList(String statementId, Object... params) throws Exception {
        SimpleExecutor simpleExecutor = new SimpleExecutor(configuration);
        MappedStatement mappedStatement = this.configuration.getMappedStatementMap().get(statementId);
        return simpleExecutor.query(mappedStatement, params);
    }

    @Override
    public <E> E selectOne(String statementId, Object... params) throws Exception {
        List<E> objects = selectList(statementId, params);
        return objects.get(0);
    }

    @Override
    public int update(String statementId, Object... params) throws Exception {
        SimpleExecutor simpleExecutor = new SimpleExecutor(configuration);
        MappedStatement mappedStatement = this.configuration.getMappedStatementMap().get(statementId);
        return simpleExecutor.update(mappedStatement, params);
    }

    @Override
    public int insert(String statementId, Object... params) throws Exception {
        SimpleExecutor simpleExecutor = new SimpleExecutor(configuration);
        MappedStatement mappedStatement = this.configuration.getMappedStatementMap().get(statementId);
        return simpleExecutor.update(mappedStatement, params);
    }

    @Override
    public int delete(String statementId, Object... params) throws Exception {
        SimpleExecutor simpleExecutor = new SimpleExecutor(configuration);
        MappedStatement mappedStatement = this.configuration.getMappedStatementMap().get(statementId);
        return simpleExecutor.update(mappedStatement, params);
    }

    @Override
    public <E> E getMapper(Class<?> mapperClass) {
        Object proxyInstance = Proxy.newProxyInstance(DefaultSqlSession.class.getClassLoader(), new Class[]{mapperClass}, new InvocationHandler() {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

                String className = method.getDeclaringClass().getName();
                String methodName = method.getName();
                String statementId = className + "." + methodName;
                Map<String, MappedStatement> mappedStatementMap = configuration.getMappedStatementMap();
                MappedStatement mappedStatement = mappedStatementMap.get(statementId);

                switch (mappedStatement.getType()) {
                    case SELECT:
                        Type genericReturnType = method.getGenericReturnType();
                        if (genericReturnType instanceof ParameterizedType) {
                            List<Object> objects = selectList(statementId, args);
                            return objects;
                        }
                        return selectOne(statementId, args);
                    case UPDATE:
                        return update(statementId, args);
                    case INSERT:
                        return insert(statementId, args);
                    case DELETE:
                        return delete(statementId, args);
                    case UNKNOWN:
                    default:
                        throw new RuntimeException("找不到相应的实现方法");
                }
            }
        });
        return (E) proxyInstance;
    }
}
