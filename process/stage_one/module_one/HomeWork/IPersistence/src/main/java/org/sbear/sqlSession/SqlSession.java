package org.sbear.sqlSession;

import java.util.List;

/**
 * @author xxyWi
 */
public interface SqlSession {
    /**
     * @param <E>
     * @return
     */
    public <E> List<E> selectList(String statementId, Object... params);


    public <E> E selectOne(String statementId, Object... params);
}
