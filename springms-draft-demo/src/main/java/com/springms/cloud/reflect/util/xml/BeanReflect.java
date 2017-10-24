package com.springms.cloud.reflect.util.xml;

import ognl.DefaultMemberAccess;
import ognl.NoSuchPropertyException;
import ognl.Ognl;
import ognl.OgnlException;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * java bean对象的反射机制封装，用于获取和设置对象的属性。
 *
 * @author hmilyylimh
 *
 * @version 0.0.1
 *
 * @date 2017/10/24
 *
 */
public class BeanReflect {

    /* xml报文的java对象的基础抽象类 */
    private AdapterXmlBean baseBean;

    /* 要进行xml与java对象转换的java对象 */
    private Object bean;

    /* 使用Ognl执行方法时需要的map对象 */
    private Map context;

    /* 要进行xml与java对象转换的java对象对应的类 */
    private Class beanClass;

    /**
     * 构造函数
     * 
     * @param bean
     *            ：传入的java实体对象
     */
    public BeanReflect(Object bean) {
        this.baseBean = (AdapterXmlBean) bean;
        this.bean = bean;
        beanClass = bean.getClass();
        context = Ognl.createDefaultContext(bean);

    }

    public BeanReflect(Class beanClass) throws RuntimeException {
        this.beanClass = beanClass;
        try {
            this.bean = beanClass.newInstance();
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
        baseBean = (AdapterXmlBean) bean;
        context = Ognl.createDefaultContext(baseBean);
    }

    public BeanReflect(AdapterXmlBean baseBean) {
        this.baseBean = baseBean;
        this.bean = baseBean;
        beanClass = bean.getClass();
        context = Ognl.createDefaultContext(bean);
    }

    public Object getBean() {
        return bean;
    }

    /**
     * @param name
     *            对象中某个属性的名字
     * @return 返回对象中name属性的类型
     * @throws NoSuchPropertyException
     */
    private Class getSetType(String name) throws RuntimeException {
        // System.out.println(name);
        Method setMethod = findSetMethod(beanClass, name);

        if (setMethod == null) {
            throw new RuntimeException("无法找到set方法：" + name);
        }
        Class c = setMethod.getParameterTypes()[0];

        return c;

    }

    public Property getRawProperty(String name) throws RuntimeException {
        String beanFieldName = null;
        String xmlNodeName = null;
        List fieldMapping = baseBean.getMapping();
        for (int i = 0; i < fieldMapping.size(); i++) {
            MappingField mappingField = (MappingField) fieldMapping.get(i);
            xmlNodeName = mappingField.getXmlField();
            if (xmlNodeName.equalsIgnoreCase(name)) {
                beanFieldName = mappingField.getBeanField();
                return new Property(beanFieldName, getSetType(beanFieldName));
            }
        }

        return null;
    }

    /**
     * 取出对象的属性集，以集合形式返回
     * 
     */
    public List getPropertyList() {
        List result = new ArrayList();
        List fieldMapping = baseBean.getMapping();
        for (int i = 0; i < fieldMapping.size(); i++) {
            MappingField mappingField = (MappingField) fieldMapping.get(i);
            String name = mappingField.getBeanField();
            Object value = getValue(name);
            String xmlName = mappingField.getXmlField();
            boolean isNode = mappingField.isNode();
            result.add(new Property(name, value, xmlName, isNode));
        }
        return result;
    }

    /**
     * 把List转成数组形式
     * 
     * @param propertyList
     *            Property对象的集合
     * @return Property对象的数组
     */
    private Property[] list2Array(List propertyList) {
        Property[] result = new Property[propertyList.size()];

        for (int i = 0; i < propertyList.size(); i++) {
            result[i] = (Property) propertyList.get(i);
        }
        return result;
    }

    /**
     * 取出对象的属性集，以数组形式返回
     * 
     * @return
     * @throws Exception
     */
    public Property[] getProperties() throws Exception {
        return list2Array(getPropertyList());
    }

    /**
     * 设置对象属性的值
     * 
     * @param property
     * @return true 设置成功，false 设置失败
     */
    public boolean setProperty(Property property) {
        DefaultMemberAccess aMemberAccess = new DefaultMemberAccess(true);
        Ognl.setMemberAccess(context, aMemberAccess);
        try {
            Ognl.setValue(property.getName(), context, bean, property.getValue());
            return true;
        }
        catch (OgnlException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 设置一组属性的值
     * 
     * @param properties
     * @return 设置成功的个数
     */
    public int setProperties(Property[] properties) {
        int result = 0;

        for (int i = 0; i < properties.length; i++) {
            if (setProperty(properties[i])) {
                ++result;
            }
        }
        return result;
    }

    private Object getValue(String methodName) {
        DefaultMemberAccess aMemberAccess = new DefaultMemberAccess(true);
        Ognl.setMemberAccess(context, aMemberAccess);
        try {
            return Ognl.getValue(methodName, context, baseBean);
        }
        catch (OgnlException e) {
            e.printStackTrace(); // throw new
            // Exception("ognl表达式解析失败:"+e.getMessage());
        }
        return null;
    }

    /**
     * 查找set方法
     * 
     * @param theClass
     * @param name
     * @return
     */
    private Method findSetMethod(Class theClass, String name) {

        Method[] methods = theClass.getMethods();

        for (int i = 0; i < methods.length; i++) {
            Method method = methods[i];
            String setMethodName = "set" + name.substring(0, 1).toUpperCase() + name.substring(1);
            if (method.getName().equals(setMethodName) && method.getParameterTypes().length == 1) {
                return method;
            }
        }

        return null;
    }
}