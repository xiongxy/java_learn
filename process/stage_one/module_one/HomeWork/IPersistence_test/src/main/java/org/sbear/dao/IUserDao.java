package org.sbear.dao;

import org.sbear.pojo.User;

import java.util.List;

/**
 * @author xxyWi
 */
public interface IUserDao {

    /**
     * 查询所有用户
     *
     * @return
     */
    public List<User> selectAllUser();


    /**
     * 按条件查询单个用户
     *
     * @param user
     * @return
     */
    public User findByCondition(User user);

    /**
     * 新增用户
     *
     * @param user
     * @return
     */
    public int insertUser(User user);

    /**
     * 修改用户
     *
     * @param user
     * @return
     */
    public int updateUser(User user);

    /**
     * 按照Id删除用户
     *
     * @param id
     * @return
     */
    public int deleteUser(int id);

}
