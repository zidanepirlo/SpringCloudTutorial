package com.springms.cloud.reflect.util.xml;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;import org.slf4j.LoggerFactory;
import org.jdom.Attribute;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import org.jdom.output.XMLOutputter;

/**
 * java对象与xml报文的转换类，实现java对象与xml报文之间的互转。
 *
 * @author hmilyylimh
 *
 * @version 0.0.1
 *
 * @date 2017/10/24
 *
 */
public class BeanXml {

    private static final Logger LOGGER = LoggerFactory.getLogger(BeanXml.class);

    /**
     * 将一个BaseBean对象转换为xml，并以字符串输出
     * 
     * @param javabean
     * @return String
     * @throws Exception
     */
    public static String bean2xml(AdapterXmlBean javabean){
        // 参数为空时抛出异常
        if (javabean == null) {
            throw new RuntimeException("转换Bean不能为空");
        }

        Element result = bean2Xml(javabean, javabean.getRootClassName());
        String xmlString = element2String(result);
        // if (LOGGER.isDebugEnabled())
        // {
        // LOGGER.debug("bean2xml String is:" + xmlString);
        // }
        return xmlString;
    }

    /**
     * 将一个BaseBean对象转换为xml，并以字符串输出
     * 
     * @param javabean
     *            待转换成XML的类
     * @param encoding
     *            XML的Encoding
     * @return String
     * @throws Exception
     */
    public static String bean2xml(AdapterXmlBean javabean, String encoding) {
        Element result = bean2Xml(javabean, javabean.getRootClassName());
        String xmlString = element2String(result);
        String headString = getXMLHeader(encoding);
        return headString + xmlString;
    }

    /**
     * 将一个BaseBean对象转换为xml，并以字符串输出
     * 
     * @param javabean
     * @param encoding
     * @param standalone
     * @return
     */
    public static String bean2xml(AdapterXmlBean javabean, String encoding, String standalone) {
        Element result = bean2Xml(javabean, javabean.getRootClassName());
        String xmlString = element2String(result);
        String headString = getXMLHeader(encoding, standalone);
        return headString + xmlString;
    }

    /**
     * java对象到xml报文的转换
     * 
     * @param javabean
     *            ：java实体对象
     * @param xmlRoot
     *            ：要转换的xml节点
     * @return
     */
    private static Element bean2Xml(Object javabean, String xmlRoot) {
        /* 如果实体对象为空，抛异常 */
        if (javabean == null) {
            throw new RuntimeException("javaBean为空");
        }
        /* 如果节点为空，从实体对象中获取 */
        if (xmlRoot == null || xmlRoot.equals("")) {
            xmlRoot = getClassName(javabean.getClass());
        }

        Element result = new Element(xmlRoot);

        List properties = new BeanReflect(javabean).getPropertyList();
        if (properties.size() == 0) {// 没有属性，则直接使用toString。
            result.setText(javabean.toString());
            return result;
        }
        for (int i = 0; i < properties.size(); i++) {
            Property prop = (Property) properties.get(i);

            if (prop == null || prop.getXmlName() == null || prop.getName() == null) {
                throw new RuntimeException("获取属性(" + prop.getName() + ")失败");
            }
            // 处理节点
            if (prop.isNode()) {
                String itemName = prop.getXmlName();
                Element item = new Element(itemName);
                Object itemValue = prop.getValue();
                if (itemValue != null) {
                    if (prop.isLeaf()) {
                        item.setText(itemValue.toString());
                        result.addContent(item);
                    } else if (itemValue instanceof Collection) {
                        coll2Xml((Collection) itemValue, itemName, result);
                    } else if (prop.isArray()) {
                        coll2Xml(Arrays.asList((Object[]) itemValue), itemName, result);
                    } else {
                        item = bean2Xml(itemValue, itemName);
                        result.addContent(item);
                    }
                }
            }
            // 处理节点属性
            else {
                Attribute attribute = new Attribute(prop.getXmlName(), prop.getValue().toString());
                result.setAttribute(attribute);
            }

        }

        return result;
    }

    /*
     * 解析器，可以使用 public static Object xml2Bean(Element element, Class beanClass)
     * throws RuntimeException { if (!(element == null)) { BeanReflect result
     * = new BeanReflect(beanClass); List items = element.getChildren(); Set
     * elementKindsSet = new HashSet(); Map elementKindsMap = new HashMap();
     * //过滤掉重复数组节点，把重复的数组节点看作一个节点来处理 for (int i = 0 ; i < items.size(); i++ ) {
     * Element item = (Element) items.get(i);
     * elementKindsSet.add(item.getName()); elementKindsMap.put(item.getName(),
     * item); }
     * 
     * Iterator iterator = elementKindsSet.iterator(); while
     * (iterator.hasNext()) { String itemName = (String) iterator.next();
     * Element item = (Element) elementKindsMap.get(itemName); Property prop =
     * result.getRawProperty(item.getName()); if (prop == null) {
     * LOGGER.error("prop is null, item:" + item.getName() + ", bean:" +
     * beanClass.getName()); //throw new RuntimeException("XML 解析失败，无法获取属性" +
     * item.getName()); continue; } //如果是叶子节点（最终节点）直接赋值 if (prop.isLeaf()) {
     * prop.setValue(item.getTextTrim()); } //如果是数组节点，遍历当前节点下的所有值 else if
     * (prop.isArray()) { List arrList = new ArrayList(); for (int j = 0; j <
     * items.size(); j++) { Element subItem = (Element) items.get(j); if
     * (subItem.getName().equals(item.getName())) { Object value =
     * xml2Bean(subItem, prop.getArrayType()); arrList.add(value); } }
     * 
     * prop.createArray(arrList.size()); for (int t = 0; t < arrList.size();
     * t++) { prop.setArrayValue(arrList.get(t), t); } } //如果是单一BEAN，递归 else {
     * prop.setValue(xml2Bean(item, prop.getType())); }
     * result.setProperty(prop); }
     * 
     * Object rtn = result.getBean(); return rtn; }
     * 
     * return null; }
     */
    /**
     * xml报文到java对象的转换
     * 
     * @param element
     * @param beanClass
     * @return
     * @throws Exception
     */
    public static Object xml2Bean(Element element, Class beanClass) throws RuntimeException {
        if (!(element == null)) {
            BeanReflect result = new BeanReflect(beanClass);
            List items = element.getChildren();
            int i = 0;
            while (i < items.size()) {
                Element item = (Element) items.get(i);
                /*
                 * System.out.println("******************************"); List
                 * att=item.getAttributes(); if(att.size()>0){ for(int
                 * k=0;k<att.size();k++){ Attribute at=(Attribute)att.get(k);
                 * System.out.println(at.getName()+" "+at.getValue()); } }
                 * System.out.println("******************************");
                 */
                Property prop = result.getRawProperty(item.getName());
                if (prop == null) {
                    LOGGER.error("prop is null, item:" + item.getName() + ", bean:" + beanClass.getName());
                    i++;
                    // throw new RuntimeException("XML 解析失败，无法获取属性" +
                    // item.getName());
                    continue;
                }

                if (prop.isLeaf()) {
                    prop.setValue(item.getTextTrim());
                    i++;
                } else if (prop.isArray()) {
                    // 对数组类型会重复运算，效率低，但逻辑上没有错误
                    List arrList = new ArrayList();
                    for (int j = i; j < items.size(); j++) {
                        Element subItem = (Element) items.get(j);
                        if (subItem.getName().equals(item.getName())) {
                            Object value = xml2Bean(subItem, prop.getArrayType());
                            arrList.add(value);
                            // 当遇到数组接点时，将上层循环的变量减少，剔除重复解析
                            i++;
                        }
                    }
                    prop.createArray(arrList.size());
                    for (int t = 0; t < arrList.size(); t++) {
                        prop.setArrayValue(arrList.get(t), t);
                    }
                } else {// is a bean
                    prop.setValue(xml2Bean(item, prop.getType()));
                    i++;
                }
                result.setProperty(prop);
            }

            Object rtn = result.getBean();
            return rtn;
        }

        return null;
    }

    /**
     * xml报文到java对象的转换
     * 
     * @param xml
     * @param beanClass
     * @return
     * @throws Exception
     */
    public static Object xml2Bean(String xml, Class beanClass) throws RuntimeException {
        if (xml == null) {
            try {
                return beanClass.newInstance();
            }
            catch (Exception e) {
                throw new RuntimeException("无法创建XML Bean对象：" + beanClass.getName(), e);
            }
        }
        Document doc;
        try {
            doc = new SAXBuilder().build(new StringReader(xml));
        }
        catch (Exception e) {
            throw new RuntimeException("无法将字符串解析为xml文档" + xml, e);
        }
        return xml2Bean(doc.getRootElement(), beanClass);
    }

    /**
     * 把集合对象转换成xml元素
     * 
     * @param coll
     * @param name
     * @return
     * @throws RuntimeException
     * @throws Exception
     */
    private static void coll2Xml(Collection coll, String name, Element parent) throws RuntimeException

    {
        if (name == null) {
            name = getClassName(coll.getClass());
        }

        for (Iterator iterator = coll.iterator(); iterator.hasNext();) {
            Element item = bean2Xml(iterator.next(), name);
            parent.addContent(item);
        }
    }

    private static String element2String(Element element) {
        String s = new XMLOutputter().outputString(element);
        // 将<tag/>替换为<tag></tag>
        String patt = "<(\\w+\\W+)/>";
        Pattern p = Pattern.compile(patt);
        Matcher m = p.matcher(s);
        while (m.find()) {
            String tag = m.group(0);
            tag = tag.substring(1, tag.length() - 2).trim();
            s = m.replaceFirst("<" + tag + ">" + "</" + tag + ">");
            m = p.matcher(s);
        }
        return s;
    }

    /**
     * 获取类的名称,不含包路径
     * 
     * @return
     */
    private static String getClassName(Class theClass) {
        String result = theClass.getName();

        /* 取最后一个.号之后的 */
        int point = result.lastIndexOf('.');
        if (point < result.length()) {
            result = result.substring(point + 1);
        }

        // 如果是类的内部类，截取$后面部分
        point = result.lastIndexOf('$');
        if (point < result.length()) {
            result = result.substring(point + 1);
        }

        return result;
    }

    /**
     * 根据编码类型获取xml文件头部
     * 
     * @param encoding
     * @return xml head String
     */
    public static String getXMLHeader(String encoding) {
        if (encoding == null) {
            return "<?xml version=\"1.0\"?>";
        } else {
            return "<?xml version = \"1.0\" encoding = \"" + encoding + "\"?>";
        }
    }

    /**
     * 根据编码类型获取xml文件头部
     * 
     * @param encoding
     * @return xml head String
     */
    public static String getXMLHeader(String encoding, String standalone) {
        if (encoding == null) {
            return "<?xml version=\"1.0\"?>";
        } else {
            return "<?xml version=\"1.0\" encoding=\"" + encoding + "\" standalone=\"" + standalone + "\"  ?>";
        }
    }

    public static void main(String[] args) {
        System.out.println("********************");
        Element el = new Element("TEST");
        el.setText("test");
        el.setAttribute("TEST1", "TEST2");
        el.setAttribute("TEST3", "TEST4");
        System.out.println(element2String(el));
    }

    public static String bean2xml2(AdapterXmlBean javabean) throws RuntimeException {
        // 参数为空时抛出异常
        if (javabean == null) {
            RuntimeException e = new RuntimeException("转换Bean不能为空");
            throw e;
        }

        Element result = bean2Xml2(javabean, javabean.getRootClassName());
        String xmlString = element2String(result);
        // if (LOGGER.isDebugEnabled())
        // {
        // LOGGER.debug("bean2xml String is:" + xmlString);
        // }
        return xmlString;
    }

    private static Element bean2Xml2(Object javabean, String xmlRoot) throws RuntimeException {
        /* 如果实体对象为空，抛异常 */
        if (javabean == null) {
            throw new RuntimeException("javaBean为空");
        }
        /* 如果节点为空，从实体对象中获取 */
        if (xmlRoot == null || xmlRoot.equals("")) {
            xmlRoot = getClassName(javabean.getClass());
        }

        Element result = new Element(xmlRoot);

        List properties = new BeanReflect(javabean).getPropertyList();
        if (properties.size() == 0) {// 没有属性，则直接使用toString。
            result.setText(javabean.toString());
            return result;
        }
        for (int i = 0; i < properties.size(); i++) {
            Property prop = (Property) properties.get(i);

            if (prop == null || prop.getXmlName() == null || prop.getName() == null) {
                throw new RuntimeException("获取属性(" + prop.getName() + ")失败");
            }
            // 处理节点
            if (prop.isNode() && !"ATTRIBUTE".equals(prop.getXmlName())) {
                String itemName = prop.getXmlName();
                Element item = new Element(itemName);
                Object itemValue = prop.getValue();
                if (itemValue != null) {
                    if (prop.isLeaf()) {
                        item.setText(itemValue.toString());
                        result.addContent(item);
                    } else if (itemValue instanceof Collection) {
                        coll2Xml((Collection) itemValue, itemName, result);
                    } else if (prop.isArray()) {
                        coll2Xml(Arrays.asList((Object[]) itemValue), itemName, result);
                    } else {
                        item = bean2Xml2(itemValue, itemName);
                        result.addContent(item);
                    }
                }
            }
            // 处理节点属性
            else if ("ATTRIBUTE".equals(prop.getXmlName())) {
                Attribute attribute = null;
                String itemName = prop.getXmlName();
                Element item = new Element(itemName);
                Object itemValue = prop.getValue();
                List properties2 = new BeanReflect(itemValue).getPropertyList();
                for (int j = 0; j < properties2.size(); j++) {
                    Property prop2 = (Property) properties2.get(j);
                    attribute = new Attribute(prop2.getXmlName(), prop2.getValue().toString());
                    result.setAttribute(attribute);
                }
            }

        }

        return result;
    }

    public static Object xml2Bean2(String xml, Class beanClass) throws RuntimeException {
        if (xml == null) {
            try {
                return beanClass.newInstance();
            }
            catch (Exception e) {
                throw new RuntimeException("无法创建XML Bean对象：" + beanClass.getName(), e);
            }
        }
        Document doc;
        try {
            doc = new SAXBuilder().build(new StringReader(xml));
        }
        catch (Exception e) {
            throw new RuntimeException("无法将字符串解析为xml文档" + xml, e);
        }
        return xml2Bean2(doc.getRootElement(), beanClass);
    }

    public static Object xml2Bean2(Element element, Class beanClass) throws RuntimeException {
        if (!(element == null)) {
            BeanReflect result = new BeanReflect(beanClass);
            List items = element.getChildren();
            int i = 0;
            while (i < items.size()) {
                Element item = (Element) items.get(i);
                List att = item.getAttributes();
                if (att.size() > 0) {
                    Element myAttribute = new Element("ATTRIBUTE");
                    for (int k = 0; k < att.size(); k++) {
                        Attribute at = (Attribute) att.get(k);
                        Element newElement = new Element(at.getName());
                        newElement.setText(at.getValue());
                        myAttribute.addContent(newElement);
                    }
                    item.addContent(myAttribute);
                }
                Property prop = result.getRawProperty(item.getName());
                if (prop == null) {
                    LOGGER.error("prop is null, item:" + item.getName() + ", bean:" + beanClass.getName());
                    i++;
                    // throw new RuntimeException("XML 解析失败，无法获取属性" +
                    // item.getName());
                    continue;
                }

                if (prop.isLeaf()) {
                    prop.setValue(item.getTextTrim());
                    i++;
                } else if (prop.isArray()) {
                    // 对数组类型会重复运算，效率低，但逻辑上没有错误
                    List arrList = new ArrayList();
                    for (int j = i; j < items.size(); j++) {
                        Element subItem = (Element) items.get(j);
                        if (subItem.getName().equals(item.getName())) {
                            Object value = xml2Bean2(subItem, prop.getArrayType());
                            arrList.add(value);
                            // 当遇到数组接点时，将上层循环的变量减少，剔除重复解析
                            i++;
                        }
                    }
                    prop.createArray(arrList.size());
                    for (int t = 0; t < arrList.size(); t++) {
                        prop.setArrayValue(arrList.get(t), t);
                    }
                } else {// is a bean
                    prop.setValue(xml2Bean2(item, prop.getType()));
                    i++;
                }
                result.setProperty(prop);
            }

            Object rtn = result.getBean();
            return rtn;
        }

        return null;
    }
}