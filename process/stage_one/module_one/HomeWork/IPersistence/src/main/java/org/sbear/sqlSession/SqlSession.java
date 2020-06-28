package org.sbear.sqlSession;

import java.util.List;

/**
 * @author xxyWi
 */
public interface SqlSession {

    public <E> List<E> selectList(String statementId, Object... params) throws Exception;

    public <E> E selectOne(String statementId, Object... params) throws Exception;

    public int update(String statementId, Object... params) throws Exception;

    public int insert(String statementId, Object... params) throws Exception;

    public int delete(String statementId, Object... params) throws Exception;

    public <E> E getMapper(Class<?> mapperClass);
}
