package org.sbear.test;

import org.dom4j.DocumentException;
import org.sbear.io.Resources;
import org.sbear.pojo.User;
import org.sbear.sqlSession.SqlSession;
import org.sbear.sqlSession.SqlSessionFactory;
import org.sbear.sqlSession.SqlSessionFactoryBuilder;

import java.beans.PropertyVetoException;
import java.io.InputStream;

/**
 * @author xxyWi
 */
public class IPersistenceTest {

    public static void main(String[] args) {
        IPersistenceTest iPersistenceTest = new IPersistenceTest();
        try {
            iPersistenceTest.test();
        } catch (DocumentException e) {
            e.printStackTrace();
        } catch (PropertyVetoException e) {
            e.printStackTrace();
        }
    }

    public void test() throws DocumentException, PropertyVetoException {
        InputStream resourceAsSteam = Resources.getResourceAsSteam("sqlMapConfig.xml");
        SqlSessionFactory build = new SqlSessionFactoryBuilder().build(resourceAsSteam);
        SqlSession sqlSession = build.openSession();





        User user = sqlSession.selectOne("user.selectOne", new User(1, "张三"));


    }

}
