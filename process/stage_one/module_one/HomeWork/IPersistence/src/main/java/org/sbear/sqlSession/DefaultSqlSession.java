package org.sbear.sqlSession;

import org.sbear.pojo.Configuration;
import org.sbear.pojo.MappedStatement;

import java.util.List;

public class DefaultSqlSession implements SqlSession {

    private Configuration configuration;

    public DefaultSqlSession(Configuration configuration) {
        this.configuration = configuration;
    }

    @Override
    public <E> List<E> selectList(String statementId, Object... params) {
        SimpleExecutor simpleExecutor = new SimpleExecutor();
        MappedStatement mappedStatement = this.configuration.getMappedStatementMap().get(statementId);
        List<E> query = simpleExecutor.query(this.configuration, mappedStatement, params);
        return query;
    }

    @Override
    public <E> E selectOne(String statementId, Object... params) {
        List<E> objects = selectList(statementId, params);
        return objects.get(0);
    }
}
