package com.springms.cloud.reflect.util.xml;

import java.lang.reflect.Array;

/**
 * 跟一个JAVA BEAN关联的属性类。
 *
 * @author hmilyylimh
 *
 * @version 0.0.1
 *
 * @date 2017/10/24
 *
 */
public class Property {
    private String name;

    private Object value;

    private Class valueClass;

    private String xmlName;

    // 当前xml元素为节点还是属性
    private boolean isNode = true;

    public Property(String name, Object value) {
        this.name = name;
        this.value = value;
        if (value != null) {
            this.valueClass = value.getClass();
        }
    }

    public Property(String name, Class valueClass) {
        this.name = name;
        this.value = null;
        this.valueClass = valueClass;
    }

    public Property(String name, Object value, String xmlName) {
        this.name = name;
        this.value = value;
        this.xmlName = xmlName;
        if (value != null) {
            this.valueClass = value.getClass();
        }
    }

    public Property(String name, Object value, String xmlName, boolean isNode) {
        this.name = name;
        this.value = value;
        this.xmlName = xmlName;
        this.isNode = isNode;
        if (value != null) {
            this.valueClass = value.getClass();
        }
    }

    /**
     * 获取属性名称。
     * 
     * @return
     */
    public String getName() {
        return name;
    }

    /**
     * 设置属性名称。
     * 
     * @param name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * 获取属性值。
     * 
     * @return
     */
    public Object getValue() {
        return value;
    }

    /**
     * 设置属性值。
     * 
     * @param value
     */
    public void setValue(Object value) {
        this.value = value;
    }

    /**
     * 获取属性的CLASS类型。
     * 
     * @return
     */
    public Class getType() {
        return valueClass;
    }

    /**
     * 创建属性值。
     * 
     * @throws Exception
     */
    public void createValue() throws Exception {
        value = valueClass.newInstance();
    }

    /**
     * 属性是否数组。
     * 
     * @return
     */
    public boolean isArray() {
        return valueClass.isArray();
    }

    /**
     * 创建数组。
     * 
     * @param len
     *            数组的长度。
     */
    public void createArray(int len) {
        value = Array.newInstance(valueClass.getComponentType(), len);
    }

    /**
     * 获取数组元素的属性。
     * 
     * @return
     */
    public Class getArrayType() {
        return valueClass.getComponentType();
    }

    /**
     * 设置数组的值。
     * 
     * @param newValue
     * @param index
     */
    public void setArrayValue(Object newValue, int index) {
        Array.set(this.value, index, newValue);
    }

    /**
     * 属性是否叶节点。
     * 
     * @return
     */
    public boolean isLeaf() {
        boolean isLeaf = valueClass.isPrimitive() || valueClass.getName().equals("java.lang.String");
        return isLeaf;
    }

    /**
     * @return the xmlName
     */
    public String getXmlName() {
        return xmlName;
    }

    /**
     * @param xmlName
     *            the xmlName to set
     */
    public void setXmlName(String xmlName) {
        this.xmlName = xmlName;
    }

    /**
     * @return the isNode
     */
    public boolean isNode() {
        return isNode;
    }

    /**
     * @param isNode
     *            the isNode to set
     */
    public void setNode(boolean isNode) {
        this.isNode = isNode;
    }

}