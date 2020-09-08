package homework;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import servlet.HttpServlet;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


/**
 * @author xxyWi
 */
public class HttpContext {

    private static final String webPath = "/WEB-INF/web.xml";
    private String contextPath;
    private String appBase;
    private  ServerConfig serverConfig;
    private final Map<String, HttpServlet> servletMap = new ConcurrentHashMap<>();

    public HttpContext() {
    }


    public HttpContext( ServerConfig serverConfig) {
        this.serverConfig = serverConfig;
    }

    public Map<String, HttpServlet> getServletMap() {
        return servletMap;
    }

    public String getContextPath() {
        return contextPath;
    }

    public void setContextPath(String contextPath) {
        this.contextPath = contextPath;
    }

    public String getAppBase() {
        return appBase;
    }

    public void setAppBase(String appBase) {
        this.appBase = appBase;
    }

    public void init() throws FileNotFoundException {
        loadServlet();
    }

    private void loadServlet() throws FileNotFoundException {
        File file = new File(appBase + contextPath + webPath);
        if (!file.exists()) {
            throw new RuntimeException("Service[" + serverConfig.getName() + "] Context[" + contextPath + "] web.xml not exist");
        }
        InputStream resourceAsStream = new FileInputStream(file);
        SAXReader saxReader = new SAXReader();

        try {
            Document document = saxReader.read(resourceAsStream);
            Element rootElement = document.getRootElement();

            List<Element> selectNodes = rootElement.selectNodes("//servlet");
            for (int i = 0; i < selectNodes.size(); i++) {
                Element element = selectNodes.get(i);
                // <servlet-name>lagou</servlet-name>
                Element servletnameElement = (Element) element.selectSingleNode("servlet-name");
                String servletName = servletnameElement.getStringValue();
                // <servlet-class>demo.LagouServlet</servlet-class>
                Element servletclassElement = (Element) element.selectSingleNode("servlet-class");
                String servletClass = servletclassElement.getStringValue();


                // 根据servlet-name的值找到url-pattern
                Element servletMapping = (Element) rootElement.selectSingleNode("/web-app/servlet-mapping[servlet-name='" + servletName + "']");
                // /lagou
                String urlPattern = servletMapping.selectSingleNode("url-pattern").getStringValue();

                servletMap.put(contextPath + urlPattern, (HttpServlet) Class.forName(servletClass).newInstance());
            }
        } catch (DocumentException | InstantiationException | IllegalAccessException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
