package com.lagou.edu.factory;

import com.lagou.edu.annotation.Autowired;
import com.lagou.edu.annotation.Service;
import com.lagou.edu.annotation.Transactional;
import com.lagou.edu.tranInterface.TransactionalCg;
import com.lagou.edu.tranInterface.TransactionalJdk;
import org.apache.commons.lang.StringUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.reflections.Reflections;

import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

/**
 * @author 应癫
 * <p>
 * 工厂类，生产对象（使用反射技术）
 */
public class BeanFactory {

    /**
     * 任务一：读取解析xml，通过反射技术实例化对象并且存储待用（map集合）
     * 任务二：对外提供获取实例对象的接口（根据id获取）
     */

    /**
     * 存储对象容器
     */
    private static Map<String, Object> map = new HashMap<>();


    static {
        getBeansByXml();
        getBeansByAnnotation();
    }


    /**
     * 从xml中获取Bean
     */
    private static void getBeansByXml() {
        // 任务一：读取解析xml，通过反射技术实例化对象并且存储待用（map集合）
        // 加载xml
        InputStream resourceAsStream = BeanFactory.class.getClassLoader().getResourceAsStream("beans.xml");
        // 解析xml
        SAXReader saxReader = new SAXReader();
        try {
            Document document = saxReader.read(resourceAsStream);
            Element rootElement = document.getRootElement();
            List<Element> beanList = rootElement.selectNodes("//bean");
            for (int i = 0; i < beanList.size(); i++) {
                Element element = beanList.get(i);
                // 处理每个bean元素，获取到该元素的id 和 class 属性
                String id = element.attributeValue("id");
                String clazz = element.attributeValue("class");
                // 通过反射技术实例化对象
                Class<?> aClass = Class.forName(clazz);
                Object o = aClass.newInstance();
                // 存储到map中待用
                map.put(id, o);

            }

            // 实例化完成之后维护对象的依赖关系，检查哪些对象需要传值进入，根据它的配置，我们传入相应的值
            // 有property子元素的bean就有传值需求
            List<Element> propertyList = rootElement.selectNodes("//property");
            // 解析property，获取父元素
            for (int i = 0; i < propertyList.size(); i++) {
                Element element = propertyList.get(i);
                String name = element.attributeValue("name");
                String ref = element.attributeValue("ref");

                // 找到当前需要被处理依赖关系的bean
                Element parent = element.getParent();

                // 调用父元素对象的反射功能
                String parentId = parent.attributeValue("id");
                Object parentObject = map.get(parentId);
                // 遍历父对象中的所有方法，找到"set" + name
                Method[] methods = parentObject.getClass().getMethods();
                for (int j = 0; j < methods.length; j++) {
                    Method method = methods[j];
                    if (method.getName().equalsIgnoreCase("set" + name)) {
                        method.invoke(parentObject, map.get(ref));
                    }
                }
                // 把处理之后的parentObject重新放到map中
                map.put(parentId, parentObject);
            }


        } catch (DocumentException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }


    /**
     * 通过反射获取添加了@Service注解的class
     */
    private static void getBeansByAnnotation() {
        Reflections reflections = new Reflections("com.lagou");
        Set<Class<?>> serviceAnnotatedWith = reflections.getTypesAnnotatedWith(Service.class);
        registerService(serviceAnnotatedWith);
    }

    private static void registerService(Set<Class<?>> serviceTypeSet) {
        //register
        serviceTypeSet.forEach(serviceClass -> {
            try {
                Object bean = serviceClass.newInstance();

                Service service = serviceClass.getAnnotation(Service.class);

                String serviceName = service.value();

                if (StringUtils.isNotBlank(serviceName)) {
                    serviceName = toLowerCaseFirstOne(serviceName);
                    if (!map.containsKey(serviceName)) {
                        map.put(serviceName, bean);
                    }
                } else {
                    String[] names = serviceClass.getName().split("\\.");
                    var name = toLowerCaseFirstOne(names[names.length - 1]);
                    if (!map.containsKey(name)) {
                        map.put(name, bean);
                    }
                }
            } catch (InstantiationException | IllegalAccessException e) {
                e.printStackTrace();
            }
        });


        //register dependency
        map.forEach((key, value) -> {
            Class<?> currentClass = value.getClass();
            //遍历属性，将持有Autowired注解的对象注入进去
            for (Field field : currentClass.getDeclaredFields()) {
                if (field.isAnnotationPresent(Autowired.class) && field.getAnnotation(Autowired.class).required()) {
                    Class<?> type = field.getType();
                    String[] names = type.getName().split("\\.");
                    String name = toLowerCaseFirstOne(names[names.length - 1]);
                    field.setAccessible(true);

                    try {
                        field.set(value, map.get(name));
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }

                    //判断当前类是否记有Transactional注解，如果有则使用代理对象
                    if (currentClass.isAnnotationPresent(Transactional.class)) {
                        //使用代理类
                        useTransactional(currentClass, value, key);
                    }
                }
            }
        });


    }

    private static void useTransactional(Class<?> curClass, Object value, String key) {
        //获取代理工厂
        ProxyFactory proxyFactory = (ProxyFactory) BeanFactory.getBean("proxyFactory");
        Class<?>[] interfaces = curClass.getInterfaces();

        if (interfaces.length == 0) {
            value = proxyFactory.getJdkProxy(value);
        } else {
            if (Arrays.stream(interfaces).anyMatch(x -> x == TransactionalJdk.class)) {
                value = proxyFactory.getJdkProxy(value);
            } else if (Arrays.stream(interfaces).anyMatch(x -> x == TransactionalCg.class)) {
                value = proxyFactory.getCglibProxy(value);
            } else {
                value = proxyFactory.getJdkProxy(value);
            }
        }
        map.put(key, value);
    }

    private static String toLowerCaseFirstOne(String s) {
        if (Character.isLowerCase(s.charAt(0))) {
            return s;
        } else {
            return (new StringBuilder()).append(Character.toLowerCase(s.charAt(0))).append(s.substring(1)).toString();
        }
    }

    /**
     * 对外提供获取实例对象的接口（根据id获取）
     *
     * @param id
     * @return
     */
    public static Object getBean(String id) {
        return map.get(id);
    }
}
