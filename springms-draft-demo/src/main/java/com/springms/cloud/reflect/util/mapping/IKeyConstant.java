package com.springms.cloud.reflect.util.mapping;

import java.util.Map;

/**
 * 常量Key接口类。<br/>
 *
 * 获取银行对象中注解里面的customFieldName值与通用对象字段名称映射集合。<br/>
 * Key：通用对象中有功能意思的属性名称；<br/>
 * Value：数组类型，{ 通用对象字段名称, 通用对象字段名称所属处上级对象Class类型 }。
 *
 * @author hmilyylimh
 *
 * @version 0.0.1
 *
 * @date 2017/10/24
 *
 */
public interface IKeyConstant {

    /**
     * 初始化常量Key。
     *
     * @param map
     */
    void init(Map<String, Object[]> map);
}