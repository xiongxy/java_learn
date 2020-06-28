package org.sbear.sqlSession;

import org.sbear.pojo.Configuration;
import org.sbear.pojo.MappedStatement;

import java.sql.SQLException;
import java.util.List;

/**
 * @author xxyWi
 */
public interface Executor {

    public <E> List<E> query(Configuration configuration, MappedStatement mappedStatement, Object... params) throws Exception;
}
