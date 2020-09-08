package servlet;

import server.Request;
import server.Response;

/**
 * @author xxyWi
 */
public interface Servlet {
    /**
     * 初始化
     * @throws Exception
     */
    void init() throws Exception;

    /**
     * 销毁
     * @throws Exception
     */
    void destory() throws Exception;

    /**
     * 执行业务
     * @param request
     * @param response
     * @throws Exception
     */
    void service(Request request, Response response) throws Exception;
}
