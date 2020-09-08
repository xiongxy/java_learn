package homework;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.concurrent.*;

/**
 * @author xxyWi
 */
public class HttpServer {
    private ServerConfig serverConfig;
    private Map<String,  HttpContext> contextMap = new ConcurrentHashMap<>();
    private ThreadPoolExecutor executor;

    /**
     * 初始化线程池
     */
    private  void  initExecutor(){
        int corePoolSize = 2;
        int maximumPoolSize = 2;
        long keepAliveTime = 100L;
        TimeUnit unit = TimeUnit.SECONDS;
        BlockingQueue<Runnable> workQueue = new ArrayBlockingQueue<>(50);
        ThreadFactory threadFactory = Executors.defaultThreadFactory();
        RejectedExecutionHandler handler = new ThreadPoolExecutor.AbortPolicy();
        this.executor = new ThreadPoolExecutor(
                corePoolSize,
                maximumPoolSize,
                keepAliveTime,
                unit,
                workQueue,
                threadFactory,
                handler
        );
    }


    public void init() {
        initExecutor();
        contextMap.forEach((k, v) -> {
            try {
                v.init();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        });
    }

    public void start() throws IOException {
        ServerSocket serverSocket = new ServerSocket(serverConfig.getPort());
        System.out.println("=====>>>Minicat's Sever[" + serverConfig.getName() + "] start on port：" + serverConfig.getPort());
        new Thread(() -> {
            while (true) {
                try {
                    Socket socket = serverSocket.accept();
                    RequestProcessorPlus requestProcessor = new RequestProcessorPlus(socket, contextMap);
                    executor.execute(requestProcessor);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();

    }

    public  ServerConfig getServerConfig() {
        return serverConfig;
    }

    public void setServerConfig( ServerConfig serverConfig) {
        this.serverConfig = serverConfig;
    }

    public void addContext( HttpContext context) {
        if (contextMap.containsKey(context.getContextPath())) {
            throw new RuntimeException("Service[" + serverConfig.getName() + "] contextPath[" + context.getContextPath() + "] is repeat.");
        }
        contextMap.put(context.getContextPath(), context);
    }
}
