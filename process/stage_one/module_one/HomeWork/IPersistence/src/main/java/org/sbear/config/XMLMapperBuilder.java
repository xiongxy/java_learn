package org.sbear.config;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.sbear.enums.SqlCommandType;
import org.sbear.pojo.Configuration;
import org.sbear.pojo.MappedStatement;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * @author xxyWi
 */
public class XMLMapperBuilder {
    private Configuration configuration;

    public XMLMapperBuilder(Configuration configuration) {
        this.configuration = configuration;
    }

    public void parse(InputStream inputStream) throws DocumentException {
        Document document = new SAXReader().read(inputStream);
        Element rootElement = document.getRootElement();
        String namespace = rootElement.attributeValue("namespace");

        List<Element> elementList = new ArrayList<>();

        // 各式各样的标签
        List<Element> selects = rootElement.selectNodes("//select");
        elementList.addAll(selects);
        List<Element> updates = rootElement.selectNodes("//update");
        elementList.addAll(updates);
        List<Element> inserts = rootElement.selectNodes("//insert");
        elementList.addAll(inserts);
        List<Element> deletes = rootElement.selectNodes("//delete");
        elementList.addAll(deletes);

        for (Element element : elementList) {
            String id = element.attributeValue("id");
            String resultType = element.attributeValue("resultType");
            String parameterType = element.attributeValue("parameterType");
            String sqlText = element.getTextTrim();
            MappedStatement mappedStatement = new MappedStatement(id, resultType, parameterType, sqlText);

            //element.getName 拿到的就是xml中的标签名称，根据标签名称判断使用的那种sqlCommand
            switch (element.getName()) {
                case "select":
                    mappedStatement.setType(SqlCommandType.SELECT);
                    break;
                case "update":
                    mappedStatement.setType(SqlCommandType.UPDATE);
                    break;
                case "insert":
                    mappedStatement.setType(SqlCommandType.INSERT);
                    break;
                case "delete":
                    mappedStatement.setType(SqlCommandType.DELETE);
                    break;
                default:
                    mappedStatement.setType(SqlCommandType.UNKNOWN);
                    break;
            }
            String key = namespace + "." + id;
            configuration.getMappedStatementMap().put(key, mappedStatement);
        }

    }
}
