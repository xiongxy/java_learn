package org.sbear.test;

import org.dom4j.DocumentException;
import org.junit.Test;
import org.sbear.dao.IUserDao;
import org.sbear.io.Resources;
import org.sbear.pojo.User;
import org.sbear.sqlSession.SqlSession;
import org.sbear.sqlSession.SqlSessionFactory;
import org.sbear.sqlSession.SqlSessionFactoryBuilder;

import java.beans.PropertyVetoException;
import java.io.InputStream;
import java.util.List;

/**
 * @author xxyWi
 */
public class IPersistenceTest {


    /**
     * 查询list
     *
     * @throws Exception
     */
    @Test
    public void test() throws Exception {
        InputStream resourceAsSteam = Resources.getResourceAsSteam("sqlMapConfig.xml");
        SqlSessionFactory build = new SqlSessionFactoryBuilder().build(resourceAsSteam);
        SqlSession sqlSession = build.openSession();
        User user1 = new User();
        user1.setId(1);
        List<User> users = sqlSession.selectList("org.sbear.dao.IUserDao.selectAllUser");
        users.forEach(System.out::println);
    }

    /**
     * testSelectOne
     *
     * @throws Exception
     */
    @Test
    public void testSelectOne() throws Exception {
        InputStream resourceAsSteam = Resources.getResourceAsSteam("sqlMapConfig.xml");
        SqlSessionFactory build = new SqlSessionFactoryBuilder().build(resourceAsSteam);
        SqlSession sqlSession = build.openSession();
        User user1 = new User();
        user1.setId(1);
        User user = sqlSession.selectOne("org.sbear.dao.IUserDao.findByCondition", user1);
        System.out.println(user);
    }

    /**
     * testGetMapperSelectAll
     *
     * @throws Exception
     */
    @Test
    public void testGetMapperSelectAll() throws Exception {
        InputStream resourceAsSteam = Resources.getResourceAsSteam("sqlMapConfig.xml");
        SqlSessionFactory build = new SqlSessionFactoryBuilder().build(resourceAsSteam);
        SqlSession sqlSession = build.openSession();
        IUserDao userDao = sqlSession.getMapper(IUserDao.class);
        userDao.selectAllUser().forEach(System.out::println);
    }

    /**
     * testGetMapperFindByCondition
     *
     * @throws Exception
     */
    @Test
    public void testGetMapperFindByCondition() throws Exception {
        InputStream resourceAsSteam = Resources.getResourceAsSteam("sqlMapConfig.xml");
        SqlSessionFactory build = new SqlSessionFactoryBuilder().build(resourceAsSteam);
        SqlSession sqlSession = build.openSession();
        IUserDao userDao = sqlSession.getMapper(IUserDao.class);
        User user = new User();
        user.setId(1);
        System.out.println(userDao.findByCondition(user));
    }

    /**
     * testGetMapperInsertUser
     *
     * @throws Exception
     */
    @Test
    public void testGetMapperInsertUser() throws Exception {
        InputStream resourceAsSteam = Resources.getResourceAsSteam("sqlMapConfig.xml");
        SqlSessionFactory build = new SqlSessionFactoryBuilder().build(resourceAsSteam);
        SqlSession sqlSession = build.openSession();
        IUserDao userDao = sqlSession.getMapper(IUserDao.class);
        User user = new User();
        user.setUsername("我是工具人，插入的");
        System.out.println(userDao.insertUser(user));
    }

    /**
     * testGetMapperUpdateUser
     *
     * @throws Exception
     */
    @Test
    public void testGetMapperUpdateUser() throws Exception {
        InputStream resourceAsSteam = Resources.getResourceAsSteam("sqlMapConfig.xml");
        SqlSessionFactory build = new SqlSessionFactoryBuilder().build(resourceAsSteam);
        SqlSession sqlSession = build.openSession();
        IUserDao userDao = sqlSession.getMapper(IUserDao.class);
        User user = new User();
        user.setId(5);
        user.setUsername("我是要修改的工具人");
        System.out.println(userDao.updateUser(user));
    }

    /**
     * testGetMapperDeleteUser
     *
     * @throws Exception
     */
    @Test
    public void testGetMapperDeleteUser() throws Exception {
        InputStream resourceAsSteam = Resources.getResourceAsSteam("sqlMapConfig.xml");
        SqlSessionFactory build = new SqlSessionFactoryBuilder().build(resourceAsSteam);
        SqlSession sqlSession = build.openSession();
        IUserDao userDao = sqlSession.getMapper(IUserDao.class);
        System.out.println(userDao.deleteUser(5));
    }

}
