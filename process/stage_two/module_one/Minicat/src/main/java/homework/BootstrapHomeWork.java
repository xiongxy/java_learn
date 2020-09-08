package homework;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


/**
 * @author xxyWi
 */
public class BootstrapHomeWork {

    private Map<String, HttpServer> serverMap = new ConcurrentHashMap<>();
    private static String HOME_PATH;
    private static final String CONF_PATH = "/conf";
    private static final String SERVER_FILE = "/server.xml";
    private static final String APP_PATH = "/webapps";
    static {
        String hPath = System.getProperty("miniCat.homePath");
        if (hPath != null) {
            File file = new File(hPath);
            if (file.exists()) {
                HOME_PATH = hPath;
            } else {
                file = new File("");
                HOME_PATH = file.getAbsolutePath();
            }
        } else {
            File file = new File("");
            HOME_PATH = file.getAbsolutePath();
        }
        HOME_PATH = HOME_PATH.replaceAll("\\\\", "/");
        System.out.println("HOME_PATH = " + HOME_PATH);
    }


    private void loadService() throws FileNotFoundException, DocumentException {
        String conf = HOME_PATH + CONF_PATH + SERVER_FILE;
        File file = new File(conf);
        if (!file.exists()) {
            throw new RuntimeException("conf server.xml not exist");
        }
        FileInputStream inputStream = new FileInputStream(file);
        SAXReader saxReader = new SAXReader();
        Document document = saxReader.read(inputStream);
        Element rootElement = document.getRootElement();
        List<Element> list = rootElement.selectNodes("//Service");
        list.forEach(v -> {
            ServerConfig serverConfig = new ServerConfig();
            String name = v.attributeValue("name");
            String port = v.attributeValue("port");
            if (name == null) {
                name = "Server_" + port;
            }
            if (serverMap.containsKey(name)) {
                throw new RuntimeException("Service[" + name + "] is repeat.");
            }
            serverConfig.setName(name);
            serverConfig.setPort(Integer.valueOf(port));
            HttpServer httpServer = new HttpServer();
            httpServer.setServerConfig(serverConfig);
            List<Element> nodes = v.selectNodes("Context");
            nodes.forEach(n -> {
                String contextPath = n.attributeValue("contextPath");
                HttpContext httpContext = new HttpContext(serverConfig);
                httpContext.setContextPath(contextPath);
                String appBase = v.attributeValue("appBase");
                if (appBase != null) {
                    appBase = appBase.replaceAll("\\\\", "/");
                }
                if (null == appBase || "".equals(appBase.trim())) {
                    httpContext.setAppBase(HOME_PATH + APP_PATH);
                    httpServer.addContext(httpContext);
                    return;
                }
                File appFile = new File(appBase);
                if (appFile.exists()) {
                    httpContext.setAppBase(appBase);
                } else {
                    File file1 = new File(HOME_PATH + appBase);
                    if (file1.exists()) {
                        httpContext.setAppBase(HOME_PATH + appBase);
                    } else {
                        throw new RuntimeException("Service[" + serverConfig.getName() + "] appBase[" + appBase + "] not exist.");
                    }
                }
                httpServer.addContext(httpContext);
            });
            serverMap.put(serverConfig.getName(), httpServer);
        });

    }
    private void  init() throws FileNotFoundException, DocumentException {
        loadService();
        serverMap.forEach((k, v) -> {
            v.init();
        });
    }



    public void start() {
        serverMap.forEach((k, v) -> {
            try {
                v.start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    public static void main(String[] args) {
        BootstrapHomeWork bootstrap = new  BootstrapHomeWork();
        try {
            bootstrap.init();
            bootstrap.start();
        } catch (FileNotFoundException | DocumentException e) {
            e.printStackTrace();
        }

    }
}
