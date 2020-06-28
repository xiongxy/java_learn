package org.sbear.sqlSession;

import org.sbear.config.BoundSql;
import org.sbear.pojo.Configuration;
import org.sbear.pojo.MappedStatement;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

/**
 * @author xxyWi
 */
public class SimpleExecutor implements Executor {
    @Override
    public <E> List<E> query(Configuration configuration, MappedStatement mappedStatement, Object... params) throws Exception {

        Connection connection = configuration.getDataSource().getConnection();

        String sql = mappedStatement.getSql();

        BoundSql boundSql = getBoundSql(sql);

        return null;
    }

    private BoundSql getBoundSql(String sql) {



        return null;
    }
}
