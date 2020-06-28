package org.sbear.sqlSession;

import org.sbear.config.BoundSql;
import org.sbear.pojo.Configuration;
import org.sbear.pojo.MappedStatement;
import org.sbear.utils.GenericTokenParser;
import org.sbear.utils.ParameterMapping;
import org.sbear.utils.ParameterMappingTokenHandler;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author xxyWi
 */
public class SimpleExecutor implements Executor {


    private Configuration configuration;

    public SimpleExecutor(Configuration configuration) {
        this.configuration = configuration;
    }


    @Override
    public <E> List<E> query(MappedStatement mappedStatement, Object... params) throws Exception {

        String sql = mappedStatement.getSql();
        BoundSql boundSql = getBoundSql(sql);
        PreparedStatement preparedStatement = getConnection().prepareStatement(boundSql.getSqlText());

        String parameterType = mappedStatement.getParameterType();
        Class<?> parameterTypeClass = getClassType(parameterType);

        List<ParameterMapping> parameterMappingList = boundSql.getParameterMappingList();

        for (int i = 0; i < parameterMappingList.size(); i++) {
            ParameterMapping parameterMapping = parameterMappingList.get(i);
            String content = parameterMapping.getContent();

            Field declaredField = parameterTypeClass.getDeclaredField(content);
            declaredField.setAccessible(true);
            Object o = declaredField.get(params[0]);
            preparedStatement.setObject(i + 1, o);
        }

        // exec sql
        ResultSet resultSet = preparedStatement.executeQuery();

        String resultType = mappedStatement.getResultType();
        Class<?> resultTypeClass = getClassType(resultType);

        List<Object> res = new ArrayList<>();
        while (resultSet.next()) {
            Object o = resultTypeClass.newInstance();
            ResultSetMetaData metaData = resultSet.getMetaData();
            for (int i = 1; i < metaData.getColumnCount(); i++) {
                String columnName = metaData.getColumnName(i);
                //这里有一个处理,我查询的数据很有可能不在我的实体中，导致报错，所以我判断了是否存在实体中
                if (Arrays.stream(resultTypeClass.getDeclaredFields()).filter(x -> x.getName().equals(columnName)).findAny().orElse(null
                ) != null) {
                    Object value = resultSet.getObject(columnName);
                    PropertyDescriptor propertyDescriptor = new PropertyDescriptor(columnName, resultTypeClass);
                    Method writeMethod = propertyDescriptor.getWriteMethod();
                    writeMethod.invoke(o, value);
                }
            }
            res.add(o);
        }

        return (List<E>) res;
    }

    @Override
    public int update(MappedStatement mappedStatement, Object... params) throws Exception {
        String sql = mappedStatement.getSql();
        BoundSql boundSql = getBoundSql(sql);
        PreparedStatement preparedStatement = getConnection().prepareStatement(boundSql.getSqlText());

        String parameterType = mappedStatement.getParameterType();
        Class<?> parameterTypeClass = getClassType(parameterType);
        List<ParameterMapping> parameterMappingList = boundSql.getParameterMappingList();

        if (parameterTypeClass == Integer.class) {
            preparedStatement.setInt(1, Integer.parseInt(params[0].toString()));
        } else {
            for (int i = 0; i < parameterMappingList.size(); i++) {
                ParameterMapping parameterMapping = parameterMappingList.get(i);
                String content = parameterMapping.getContent();
                Field declaredField = parameterTypeClass.getDeclaredField(content);
                declaredField.setAccessible(true);
                Object o = declaredField.get(params[0]);
                preparedStatement.setObject(i + 1, o);
            }
        }


        int row = preparedStatement.executeUpdate();
        return row;
    }


    private Class<?> getClassType(String parameterType) throws ClassNotFoundException {
        if (parameterType != null) {
            Class<?> aClass = Class.forName(parameterType);
            return aClass;
        }
        return null;
    }

    private Connection getConnection() throws SQLException {
        return configuration.getDataSource().getConnection();
    }

    private BoundSql getBoundSql(String sql) {
        ParameterMappingTokenHandler parameterMappingTokenHandler = new ParameterMappingTokenHandler();
        GenericTokenParser genericTokenParser = new GenericTokenParser("#{", "}", parameterMappingTokenHandler);
        String parseSql = genericTokenParser.parse(sql);
        List<ParameterMapping> parameterMappings = parameterMappingTokenHandler.getParameterMappings();
        BoundSql boundSql = new BoundSql(parseSql, parameterMappings);
        return boundSql;
    }
}
