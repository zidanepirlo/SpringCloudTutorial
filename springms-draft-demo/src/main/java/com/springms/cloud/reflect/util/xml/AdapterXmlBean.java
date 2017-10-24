package com.springms.cloud.reflect.util.xml;

import java.util.List;

/**
 * 所有xml所对应的java对象的抽象类。
 *
 * @author hmilyylimh
 *
 * @version 0.0.1
 *
 * @date 2017/10/24
 *
 */
public interface AdapterXmlBean {

    /**
     * 获取xml field与object field名称之间的对应
     * 
     * @return MappingField的列表
     */
    public List getMapping();

    /**
     * 获取根节点对应类的名称
     * 
     * @return String
     */
    public String getRootClassName();
}