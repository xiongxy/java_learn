package homework;


import server.*;
import servlet.HttpServlet;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.util.Map;

/**
 * @author wzq.Jolin
 * @company none
 * @create 2020-08-02 15:05
 */
public class RequestProcessorPlus  extends  Thread{
    private Socket socket;
    private Map<String,  HttpContext> httpContextMap;

    public RequestProcessorPlus(Socket socket, Map<String,  HttpContext> httpContextMap) {
        this.socket = socket;
        this.httpContextMap = httpContextMap;
    }

    @Override
    public void run() {
        Request request = null;
        Response response = null;
        try {
            InputStream inputStream = socket.getInputStream();
            // 封装Request对象和Response对象
            request = new Request(inputStream);
            response = new Response(socket.getOutputStream());
            String url = request.getUrl();
            String contextPath;
            if (url.startsWith("/")) {
                if (url.lastIndexOf('/') > 1) {
                    contextPath = url.substring(0, url.indexOf('/', 1));
                } else {
                    contextPath = "/";
                }
            } else {
                contextPath = url.substring(0, url.indexOf('/'));
            }
            HttpContext httpContext = httpContextMap.get(contextPath);
            if (httpContext == null) {
                httpContext = httpContextMap.get("/");
            }
            if (httpContext == null) {
                response.outputHtml(request.getUrl());
                return;
            }
            Map<String, HttpServlet> servletMap = httpContext.getServletMap();
            // 静态资源处理
            if (servletMap.get(url) == null) {
                response.outputHtml(request.getUrl());
            } else {
                // 动态资源servlet请求
                HttpServlet httpServlet = servletMap.get(url);
                httpServlet.service(request, response);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                socket.close();
                if (request != null) {
                    request.close();
                }
                if (response != null) {
                    response.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }


}
