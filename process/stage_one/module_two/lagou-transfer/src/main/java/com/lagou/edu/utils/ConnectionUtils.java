package com.lagou.edu.utils;

import com.lagou.edu.annotation.Service;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * @author 应癫
 */
@Service()
public class ConnectionUtils {

    /**
     * 存储当前线程的连接
     */
    private ThreadLocal<Connection> threadLocal = new ThreadLocal<>();

    /**
     * 从当前线程获取连接
     */
    public Connection getCurrentThreadConn() throws SQLException {
        Connection connection = threadLocal.get();
        if(connection == null) {
            // 从连接池拿连接并绑定到线程
            connection = DruidUtils.getInstance().getConnection();
            // 绑定到当前线程
            threadLocal.set(connection);
        }
        return connection;
    }
}
