package com.springms.cloud.reflect.util.xml;

/**
 * xml标签名和bean属性名的对应
 *
 * @author hmilyylimh
 *
 * @version 0.0.1
 *
 * @date 2017/10/24
 *
 */
public class MappingField {

    private String xmlField;
    private String beanField;
    private boolean isNode = true;

    public MappingField() {
    }

    public MappingField(String xmlField, String beanField) {
        this.xmlField = xmlField;
        this.beanField = beanField;
    }

    public MappingField(String xmlField, String beanField, boolean isNode) {
        this.xmlField = xmlField;
        this.beanField = beanField;
        this.isNode = isNode;
    }

    public String getXmlField() {
        return xmlField;
    }

    public void setXmlField(String xmlField) {
        this.xmlField = xmlField;
    }

    public String getBeanField() {
        return beanField;
    }

    public void setBeanField(String beanField) {
        this.beanField = beanField;
    }

    public boolean isNode() {
        return isNode;
    }

    public void setNode(boolean node) {
        isNode = node;
    }

    @Override
    public String toString() {
        return "MappingField{" +
                "xmlField='" + xmlField + '\'' +
                ", beanField='" + beanField + '\'' +
                ", isNode=" + isNode +
                '}';
    }
}