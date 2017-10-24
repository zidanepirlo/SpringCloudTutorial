package com.springms.cloud.reflect.util;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * 注解反射解析器。
 *
 * @author hmilyylimh
 *
 * @version 0.0.1
 *
 * @date 2017/10/24
 *
 */
public class AnnotationReflectParser {

    private static final org.slf4j.Logger Logger = LoggerFactory.getLogger(AnnotationReflectParser.class);

    /**
     * 开始解析。
     *
     * @param bankObject：银行报文对象。
     * @param resultObject：解析后的通用对象。
     */
    public static boolean start(Object bankObject, Object resultObject) {
        try {
            convert(bankObject, resultObject);
            return true;
        } catch (Exception e) {
            Logger.error("开始解析出现最外层异常，bankObject: {}, resultObject: {}", bankObject, resultObject);
            return false;
        }
    }

    /**
     * 循环嵌套解析，该方法会被循环调用多次。
     *
     * @param bankObject：银行报文对象。
     * @param resultObject：解析后的通用对象。
     */
    private static void convert(Object bankObject, Object resultObject) {
        if (bankObject == null) {
            Logger.error("循环嵌套解析，传入 bankObject 为空, bankObject: {}, resultObject: {}", bankObject, resultObject);
            throw new RuntimeException("循环嵌套解析，传入 bankObject 为空");
        }
        if (resultObject == null) {
            Logger.error("循环嵌套解析，传入 resultObject 为空, bankObject: {}, resultObject: {}", bankObject, resultObject);
            throw new RuntimeException("循环嵌套解析，传入 resultObject 为空");
        }

        Class<?> bankObjClass = bankObject.getClass();
        List<Field> bankFields = ReflectionUtil.getDeclaredSuperFields(bankObjClass);
        if (bankFields == null || bankFields.isEmpty()) {
            Logger.error("循环嵌套解析，bankObject 对象内没有 Field 属性字段, bankObject: {}, resultObject: {}", bankObject,
                    resultObject);
            return;
        }

        CustomFieldAnnotation customFieldAnnotation = null;
        CustomFieldAnnotation.CustomFieldType customFieldType = null;
        for (Field bankField : bankFields) {
            customFieldAnnotation = bankField.getAnnotation(CustomFieldAnnotation.class);
            // 过滤没有注解的字段
            if (customFieldAnnotation == null) {
                // Logger.error("循环嵌套解析，过滤没有注解的字段, bankField: {}, bankObject: {}, resultObject: {}", bankField, bankObject, resultObject);
                continue;
            }

            // 过滤已经禁用的字段
            if (!customFieldAnnotation.isEnable()) {
                Logger.error("循环嵌套解析，过滤已经禁用的字段, bankField: {}, bankObject: {}, resultObject: {}", bankField,
                        bankObject, resultObject);
                continue;
            }

            // 过滤没有定义类型的字段
            customFieldType = customFieldAnnotation.customFieldType();
            if (customFieldType == null || customFieldType == CustomFieldAnnotation.CustomFieldType.Unknow) {
                Logger.error("循环嵌套解析，过滤没有定义类型的字段, bankField: {}, bankObject: {}, resultObject: {}", bankField,
                        bankObject, resultObject);
                continue;
            }

            // 针对不同类型走不同分支处理
            switch (customFieldType) {
                case PRIMITIVE: {
                    setPrimitiveType(bankField, bankObject, customFieldAnnotation, resultObject);
                    break;
                }
                case CLASS: {
                    setClassType(bankField, bankObject, customFieldAnnotation, resultObject);
                    break;
                }
                case ARRAY: {
                    setArrayType(bankField, bankObject, customFieldAnnotation, resultObject);
                    break;
                }
                case LIST: {
                    setListType(bankField, bankObject, customFieldAnnotation, resultObject);
                    break;
                }
                case Unknow: {
                    String msg = String.format("循环嵌套解析, 走进了没有逻辑处理的分支类型, customFieldName: %s, bankFieldName: %s",
                            customFieldAnnotation.customFieldName(), bankField.getName());
                    Logger.error(msg);
                    throw new RuntimeException(msg);
                }
            }
        }
    }

    /**
     * 设置基本类型字段。
     *
     * @param bankField
     * @param bankFieldParentObj
     * @param customFieldAnnotation
     * @param resultObject
     */
    private static void setPrimitiveType(Field bankField, Object bankFieldParentObj, CustomFieldAnnotation
            customFieldAnnotation, Object resultObject) {
        try {
            String customFieldName = customFieldAnnotation.customFieldName();
            Object bankFieldValue = ReflectionUtil.getFieldValue(bankField, bankFieldParentObj);

            Object[] fieldMapping = AnnotationMapping.getGeneralFieldMapping().get(customFieldName);
            if (fieldMapping == null) {
                String msg = String.format("设置基本类型字段, 没有设置通用字段映射关系, customFieldName: %s, bankFieldName: %s",
                        customFieldName, bankField.getName());
                Logger.error(msg);
                throw new RuntimeException(msg);
            }
            String commonMappingFieldName = (String) fieldMapping[0];
            if (StringUtils.isEmpty(commonMappingFieldName)) {
                String msg = String.format("设置基本类型字段, 通用对象中的属性字段为空, customFieldName: %s, bankFieldName: %s", customFieldName, bankField.getName());
                Logger.error(msg);
                throw new RuntimeException(msg);
            }

            // 获取 resultObject 结果对象中 commonMappingFieldName 字段对象 Field
            // 从 resultObject 当前对象找，以及从 resultObject 父类找 commonMappingFieldName 属性字段
            Field commonMappingField = ReflectionUtil.getDeclaredField(resultObject, commonMappingFieldName);
            Object fieldParentObj = resultObject;
            if (customFieldAnnotation.isChild() || commonMappingField == null) {
                // 如果找不到的话，那么则尝试从 resultObject 对象的子对象递归子对象找 commonMappingFieldName 属性字段
                Class<?> commonMappingFieldSuperClass = (Class<?>) fieldMapping[1];
                Object[] childAttr = ReflectionUtil.getChildAttr(resultObject, commonMappingFieldSuperClass,
                        commonMappingFieldName);
                if (childAttr == null) {
                    String msg = String.format("设置基本类型字段, 在通用对象的子类中没有搜索到通用属性字段, customFieldName: %s, bankFieldName: %s, " +
                            "commonMappingFieldName: %s", customFieldName, bankField.getName(), commonMappingFieldName);
                    Logger.error(msg);
                    throw new RuntimeException(msg);
                }

                fieldParentObj = childAttr[0];
                commonMappingField = (Field) childAttr[1];
            }

            // 给结果对象 resultObject 赋值，类型对等则直接赋值
            if (customFieldAnnotation.isReWrite()) {
                ReflectionUtil.setFieldValue(commonMappingField, fieldParentObj, bankFieldValue);
            } else if (commonMappingField.getType() == bankFieldValue.getClass()) {
                ReflectionUtil.setFieldValue(commonMappingField, fieldParentObj, bankFieldValue);
            }
            // 类型不对等的话，则记录错误日志
            else {
                Logger.error("设置基本类型字段, 类型不对等的话, 银行字段名称: {}, 通用对象字段名称: {}, bankFieldParentObj: {}, customFieldAnnotation: {}, " +
                        "resultObject: {}", bankField.getName(), customFieldAnnotation.customFieldName(), bankFieldParentObj, customFieldAnnotation, resultObject);
            }
        } catch (Exception e) {
            Logger.error("设置基本类型字段异常, 银行字段名称: {}, 通用对象字段名称: {}, bankFieldParentObj: {}, customFieldAnnotation: {}, " +
                    "resultObject: {}, \n\ne: " +
                    "{}", bankField.getName(), customFieldAnnotation.customFieldName(), bankFieldParentObj, customFieldAnnotation, resultObject, e);
            throw new RuntimeException("设置基本类型字段异常");
        }
    }

    /**
     * 设置类成员类型字段。
     *
     * @param bankField
     * @param bankFieldParentObj
     * @param customFieldAnnotation
     * @param resultObject
     */
    private static void setClassType(Field bankField, Object bankFieldParentObj, CustomFieldAnnotation
            customFieldAnnotation, Object resultObject) {
        try {
            String customFieldName = customFieldAnnotation.customFieldName();
            Object bankFieldValue = ReflectionUtil.getFieldValue(bankField, bankFieldParentObj);

            if (bankFieldValue == null) {
                Logger.error("设置类成员类型字段，解析银行对象中 {} 属性字段值为空。", bankField.getName());
                return;
            }

            Class<?> bankFieldObjClass = bankFieldValue.getClass();
            Field[] bankFieldObjFields = bankFieldObjClass.getDeclaredFields();
            if (bankFieldObjFields == null || bankFieldObjFields.length == 0) {
                Logger.error("设置类成员类型字段，bankField 对象内没有 Field 属性字段, bankFieldName: {}, bankFieldParentObj: {}, " +
                        "customFieldAnnotation: {}, resultObject: {}, ", bankField.getName(), bankFieldParentObj,
                        customFieldAnnotation, resultObject);
                return;
            }

            // resultObject 该对象有数据，那么就得在 resultObject 中实例化对应的对象
            Object[] fieldMapping = AnnotationMapping.getGeneralFieldMapping().get(customFieldName);
            if (fieldMapping == null) {
                String msg = String.format("设置类成员类型字段, 没有设置通用字段映射关系, customFieldName: %s, bankFieldName: %s", customFieldName, bankField.getName());
                Logger.error(msg);
                throw new RuntimeException(msg);
            }
            String commonMappingFieldName = (String) fieldMapping[0];
            if (StringUtils.isEmpty(commonMappingFieldName)) {
                String msg = String.format("设置类成员类型字段, 通用对象中的属性字段为空, customFieldName: %s, bankFieldName: %s", customFieldName, bankField.getName());
                Logger.error(msg);
                throw new RuntimeException(msg);
            }

            // 从 resultObject 当前对象找，以及从 resultObject 父类找 commonMappingFieldName 属性字段
            Field commonMappingField = ReflectionUtil.getDeclaredField(resultObject, commonMappingFieldName);
            Object fieldParentObj = resultObject;
            if (commonMappingField == null) {
                // 如果找不到的话，那么则尝试从 resultObject 对象的子对象递归子对象找 commonMappingFieldName 属性字段
                Class<?> commonMappingFieldSuperClass = (Class<?>) fieldMapping[1];
                Object[] childAttr = ReflectionUtil.getChildAttr(resultObject, commonMappingFieldSuperClass,
                        commonMappingFieldName);
                if (childAttr == null) {
                    String msg = String.format("设置类成员类型字段, 在通用对象的子类中没有搜索到通用属性字段, customFieldName: %s, bankFieldName: %s, " +
                            "commonMappingFieldName: %s", customFieldName, bankField.getName(), commonMappingFieldName);
                    Logger.error(msg);
                    throw new RuntimeException(msg);
                }

                fieldParentObj = childAttr[0];
                commonMappingField = (Field) childAttr[1];
            }

            // 获取 resultObject 结果对象中 Field 字段的值
            if (ReflectionUtil.getFieldValue(commonMappingField, fieldParentObj) == null) {
                Object newInstance = commonMappingField.getType().newInstance();
                ReflectionUtil.setFieldValue(commonMappingField, resultObject, newInstance);
            }

            convert(bankFieldValue, resultObject);
        } catch (Exception e) {
            Logger.error("设置类成员类型字段异常, bankField: {}, bankFieldParentObj: {}, customFieldAnnotation: {}, " +
                    "resultObject: {}, \n\ne: " +
                    "{}", bankField, bankFieldParentObj, customFieldAnnotation, resultObject, e);
            throw new RuntimeException("设置类成员类型字段异常");
        }
    }

    /**
     * 设置数组类型字段。
     *
     * @param bankField
     * @param bankFieldParentObj
     * @param customFieldAnnotation
     * @param resultObject
     */
    private static void setArrayType(Field bankField, Object bankFieldParentObj, CustomFieldAnnotation
            customFieldAnnotation, Object resultObject) {
        try {
            String customFieldName = customFieldAnnotation.customFieldName();
            Object bankFieldValue = ReflectionUtil.getFieldValue(bankField, bankFieldParentObj);

            if (bankFieldValue == null) {
                Logger.error("设置数组类型字段，解析银行对象中 {} 属性字段值为空。", bankField.getName());
                return;
            }

            int length = Array.getLength(bankFieldValue);
            if (length <= 0) {
                String msg = String.format("设置数组类型字段, 银行数组长度为空, customFieldName: %s, bankFieldName: %s",
                        customFieldName, bankField.getName());
                Logger.error(msg);
                return;
            }

            // resultObject 该对象有数据，那么就得在 resultObject 中实例化对应的对象
            Object[] fieldMapping = AnnotationMapping.getGeneralFieldMapping().get(customFieldName);
            if (fieldMapping == null) {
                String msg = String.format("设置数组类型字段, 没有设置通用字段映射关系, customFieldName: %s, bankFieldName: %s", customFieldName, bankField.getName());
                Logger.error(msg);
                throw new RuntimeException(msg);
            }
            String commonMappingFieldName = (String) fieldMapping[0];
            if (StringUtils.isEmpty(commonMappingFieldName)) {
                String msg = String.format("设置数组类型字段, 通用对象中的属性字段为空, customFieldName: %s, bankFieldName: %s", customFieldName, bankField.getName());
                Logger.error(msg);
                throw new RuntimeException(msg);
            }

            // 从 resultObject 当前对象找，以及从 resultObject 父类找 commonMappingFieldName 属性字段
            Field commonMappingField = ReflectionUtil.getDeclaredField(resultObject, commonMappingFieldName);
            Object fieldParentObj = resultObject;
            if (commonMappingField == null) {
                // 如果找不到的话，那么则尝试从 resultObject 对象的子对象递归子对象找 commonMappingFieldName 属性字段
                Class<?> commonMappingFieldSuperClass = (Class<?>) fieldMapping[1];
                Object[] childAttr = ReflectionUtil.getChildAttr(resultObject, commonMappingFieldSuperClass,
                        commonMappingFieldName);
                if (childAttr == null) {
                    String msg = String.format("设置数组类型字段, 在通用对象的子类中没有搜索到通用属性字段, customFieldName: %s, bankFieldName: %s, " +
                            "commonMappingFieldName: %s", customFieldName, bankField.getName(), commonMappingFieldName);
                    Logger.error(msg);
                    throw new RuntimeException(msg);
                }

                fieldParentObj = childAttr[0];
                commonMappingField = (Field) childAttr[1];
            }

            // 获取 resultObject 结果对象中 Field 字段的值
            if (ReflectionUtil.getFieldValue(commonMappingField, fieldParentObj) == null) {
                Class<?> elementType = commonMappingField.getType();
                String elementTypeName = elementType.getName();
                int startIndex = elementTypeName.indexOf("com");
                int endIndex = elementTypeName.lastIndexOf(";");
                String innerClassName = elementTypeName.substring(startIndex, endIndex);
                Class<?> innerClass = Class.forName(innerClassName);

                // 实例化数组
                Object newInstance = Array.newInstance(innerClass, length);

                // 数组赋值空对象
                Object[] arrays = (Object[]) newInstance;
                Object[] bankFieldValueArrays = (Object[]) bankFieldValue;
                for (int i = 0; i < length; i++) {
                    arrays[i] = innerClass.newInstance();
                }
                // 将空数组赋值到 resultObject 结果对象中
                ReflectionUtil.setFieldValue(commonMappingField, fieldParentObj, newInstance);

                // 循环解析 bankFieldValueArrays 的值放到结果对象中对应的索引位置中
                for (int i = 0; i < length; i++) {
                    Object itemResultObject = arrays[i];
                    convert(bankFieldValueArrays[i], itemResultObject);
                }
            }
        } catch (Exception e) {
            Logger.error("设置数组类型字段异常, bankField: {}, bankFieldParentObj: {}, customFieldAnnotation: {}, " +
                    "resultObject: {}, \n\ne: " +
                    "{}", bankField, bankFieldParentObj, customFieldAnnotation, resultObject, e);
            throw new RuntimeException("设置数组类型字段异常");
        }
    }

    /**
     * 设置列表类型字段。
     *
     * @param bankField
     * @param bankFieldParentObj
     * @param customFieldAnnotation
     * @param resultObject
     */
    private static void setListType(Field bankField, Object bankFieldParentObj, CustomFieldAnnotation
            customFieldAnnotation, Object resultObject) {
        try {
            String customFieldName = customFieldAnnotation.customFieldName();
            Object bankFieldValue = ReflectionUtil.getFieldValue(bankField, bankFieldParentObj);

            if (bankFieldValue == null) {
                Logger.error("设置列表类型字段，解析银行对象中 {} 属性字段值为空。", bankField.getName());
                return;
            }

            List bankFieldValueList = (List) bankFieldValue;
            int size = bankFieldValueList.size();
            if (size <= 0) {
                String msg = String.format("设置列表类型字段, 银行列表长度为空, customFieldName: %s, bankFieldName: %s",
                        customFieldName, bankField.getName());
                Logger.error(msg);
                return;
            }

            // resultObject 该对象有数据，那么就得在 resultObject 中实例化对应的对象
            Object[] fieldMapping = AnnotationMapping.getGeneralFieldMapping().get(customFieldName);
            if (fieldMapping == null) {
                String msg = String.format("设置列表类型字段, 没有设置通用字段映射关系, customFieldName: %s, bankFieldName: %s", customFieldName, bankField.getName());
                Logger.error(msg);
                throw new RuntimeException(msg);
            }
            String commonMappingFieldName = (String) fieldMapping[0];
            if (StringUtils.isEmpty(commonMappingFieldName)) {
                String msg = String.format("设置列表类型字段, 通用对象中的属性字段为空, customFieldName: %s, bankFieldName: %s", customFieldName, bankField.getName());
                Logger.error(msg);
                throw new RuntimeException(msg);
            }

            // 从 resultObject 当前对象找，以及从 resultObject 父类找 commonMappingFieldName 属性字段
            Field commonMappingField = ReflectionUtil.getDeclaredField(resultObject, commonMappingFieldName);
            Object fieldParentObj = resultObject;
            if (commonMappingField == null) {
                // 如果找不到的话，那么则尝试从 resultObject 对象的子对象递归子对象找 commonMappingFieldName 属性字段
                Class<?> commonMappingFieldSuperClass = (Class<?>) fieldMapping[1];
                Object[] childAttr = ReflectionUtil.getChildAttr(resultObject, commonMappingFieldSuperClass,
                        commonMappingFieldName);
                if (childAttr == null) {
                    String msg = String.format("设置列表类型字段, 在通用对象的子类中没有搜索到通用属性字段, customFieldName: %s, bankFieldName: %s, " +
                            "commonMappingFieldName: %s", customFieldName, bankField.getName(), commonMappingFieldName);
                    Logger.error(msg);
                    throw new RuntimeException(msg);
                }

                fieldParentObj = childAttr[0];
                commonMappingField = (Field) childAttr[1];
            }
            Type genericType = commonMappingField.getGenericType();
            if(!(genericType instanceof ParameterizedType)){
                String msg = String.format("设置列表类型字段, 通用对象中的属性字段类型设置有误，设置的不是列表类型, customFieldName: %s, bankFieldName: %s", customFieldName, bankField.getName());
                Logger.error(msg);
                return;
            }

            // 获取 resultObject 结果对象中 Field 字段的值
            if (ReflectionUtil.getFieldValue(commonMappingField, fieldParentObj) == null) {
                ParameterizedType parameterizedType = (ParameterizedType)genericType;
                Class<?> innerClass = (Class) parameterizedType.getActualTypeArguments()[0];//得到对象list中实例的类型

                // 实例化数组
                List newInstance = new ArrayList();

                // 数组赋值空对象
                for (int i = 0; i < size; i++) {
                    newInstance.add(innerClass.newInstance());
                }
                // 将空数组赋值到 resultObject 结果对象中
                ReflectionUtil.setFieldValue(commonMappingField, fieldParentObj, newInstance);

                // 循环解析 bankFieldValueArrays 的值放到结果对象中对应的索引位置中
                for (int i = 0; i < size; i++) {
                    Object itemResultObject = newInstance.get(i);
                    convert(bankFieldValueList.get(i), itemResultObject);
                }
            }
        } catch (Exception e) {
            Logger.error("设置列表类型字段异常, bankField: {}, bankFieldParentObj: {}, customFieldAnnotation: {}, " +
                    "resultObject: {}, \n\ne: " +
                    "{}", bankField, bankFieldParentObj, customFieldAnnotation, resultObject, e);
            throw new RuntimeException("设置列表类型字段异常");
        }
    }
}