package com.springms.cloud.reflect.util;

import java.lang.annotation.*;

/**
 * 成员字段注解（注解加在解析银行返回的对象中）。
 *
 * @author hmilyylimh
 *
 * @version 0.0.1
 *
 * @date 2017/10/24
 *
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface CustomFieldAnnotation {

    /**
     * 自定义注解名称。
     *
     * @return
     */
    String customFieldName() default "";

    /**
     * 自定义注解类型。
     *
     * @return
     */
    CustomFieldType customFieldType() default CustomFieldType.PRIMITIVE;

    /**
     * 标识字段是否有效。
     *
     * @return
     */
    boolean isEnable() default true;

    /**
     * 是否重新刷写。
     *
     * @return
     */
    boolean isReWrite() default true;

    /**
     * 是否是子类属性，是的话，则根据后面的子类所属 Class 寻找字段属性。
     *
     * @return
     */
    boolean isChild() default false;

    /**
     * 自定义注解类型
     */
    public static enum CustomFieldType {

        /**
         * 未知类型
         */
        Unknow,

        /**
         * 原生类型
         */
        PRIMITIVE,

        /**
         * 类成员类型
         */
        CLASS,

        /**
         * 数组类型
         */
        ARRAY,

        /**
         * 列表类型
         */
        LIST;

        public static CustomFieldType valueof(String fieldType) {
            if (CustomFieldType.PRIMITIVE.toString().equalsIgnoreCase(fieldType)) {
                return PRIMITIVE;
            } else if (CustomFieldType.CLASS.toString().equalsIgnoreCase(fieldType)) {
                return CLASS;
            } else if (CustomFieldType.ARRAY.toString().equalsIgnoreCase(fieldType)) {
                return ARRAY;
            } else if (CustomFieldType.LIST.toString().equalsIgnoreCase(fieldType)) {
                return LIST;
            } else {
                return Unknow;
            }
        }
    }
}