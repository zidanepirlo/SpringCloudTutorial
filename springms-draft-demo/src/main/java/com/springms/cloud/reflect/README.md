# Reflect 通过反射获取自定义注解值给另外一个对象赋值
-

## 一、大致介绍

``` 
1、今天刚完成这么一个功能模块，需求场景是这样的，我们需要对接许多银行的接口，我们解析银行XML报文后，根据每个银行每个接口我们会解析得到很多BankDTO;
2、然后我们需要在BankDTO挑出一些必要的字段放到另外一个 ResultDTO 中去，然后将 ResultDTO 的数据入库处理；
3、而且最关键的是，每个银行的字段五花八门，我们根本没办法统一字段，最初的办法我们是对每个 BankDTO 写了一个转换类转成 ResultDTO；
4、但是随着接入的银行越来越多了，开发效率也就慢慢的降下来了，然而我就在思考如何优化这个字段转换来转换去的笨重方法；

5、经过辗转反侧的思考，最终自己定义一个注解类，然后将这些注解安插在BankDTO上，而我们需要做的事情就是反射获取注解值然后给ResultDTO赋值即可；
6、原理就是这么简单，这样写好之后，银行一多，开发人员不够，我们找些不会开发的人员只要告诉他们如何写 BankDTO 对象即可，如何映射字段值即可，最后提交代码就搞定了；
7、而我在这里主要将一些类贴出来仅供大家参考，如果这种思路在大家工作中用得着的话，相信稍微复用我这思路，功能很快就能水到渠成；
```

## 二、实现步骤

### 2.1 反射工具类，参考网上代码做了稍微调整，整理成符合自己业务逻辑的公用工具类
``` 
package com.springms.cloud.reflect.util;

import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;

/**
 * 反射工具类。
 *
 * @author hmilyylimh
 *
 * @version 0.0.1
 *
 * @date 2017/10/24
 */
public class ReflectionUtil {

    private static final org.slf4j.Logger Logger = LoggerFactory.getLogger(ReflectionUtil.class);

    /**
     * 循环向上转型, 获取对象的 DeclaredField。
     *
     * @param object    : 子类对象，也就是实现类对象；
     * @param fieldName : 父类中的属性名；
     * @return 父类中的属性对象
     */
    public static Field getDeclaredField(Object object, String fieldName) {
        Field field = null;

        Class<?> clazz = object.getClass();
        for (; clazz != Object.class; clazz = clazz.getSuperclass()) {
            try {
                field = clazz.getDeclaredField(fieldName);
                return field;
            } catch (Exception e) {
                // 这里甚么都不要做！并且这里的异常必须这样写，不能抛出去。
                // 如果这里的异常打印或者往外抛，则就不会执行clazz = clazz.getSuperclass(),最后就不会进入到父类中了
                // Logger.error("循环向上转型, 获取对象的 DeclaredField 异常, fieldName: {}, object: {}, \n\ne: {}", fieldName, object, CommonUtil.getExceptionStackTrace(e));
            }
        }

        return null;
    }

    /**
     * 循环向上转型, 获取当前对象以及父类所有对象的属性 Field 字段。
     *
     * @param objectClass
     * @return
     */
    public static List<Field> getDeclaredSuperFields(Class<?> objectClass) {
        List<Field> declaredFieldList = new ArrayList<Field>();

        Class<?> tempClass = objectClass;
        try {
            while(true){
                if(tempClass == Object.class){
                    break;
                }

                declaredFieldList.addAll(Arrays.asList(tempClass.getDeclaredFields()));
                tempClass = tempClass.getSuperclass();
            }
        } catch (Exception e) {
            // 这里甚么都不要做！并且这里的异常必须这样写，不能抛出去。
            // 如果这里的异常打印或者往外抛，则就不会执行clazz = clazz.getSuperclass(),最后就不会进入到父类中了
            Logger.error("循环向上转型, 获取当前对象以及父类所有对象的属性 Field 字段异常, objectClass: {}, \n\ne: {}", objectClass, e);
        }

        return declaredFieldList;
    }

    /**
     * 循环向上转型, 获取对象的 DeclaredMethod。
     *
     * @param object         : 子类对象，也就是实现类对象；
     * @param methodName     : 父类中的方法名；
     * @param parameterTypes : 父类中的方法参数类型；
     * @return 父类中的方法对象
     */
    public static Method getDeclaredMethod(Object object, String methodName, Class<?>... parameterTypes) {
        Method method = null;

        for (Class<?> clazz = object.getClass(); clazz != Object.class; clazz = clazz.getSuperclass()) {
            try {
                method = clazz.getDeclaredMethod(methodName, parameterTypes);
                return method;
            } catch (Exception e) {
                // 这里甚么都不要做！并且这里的异常必须这样写，不能抛出去。
                // 如果这里的异常打印或者往外抛，则就不会执行clazz = clazz.getSuperclass(),最后就不会进入到父类中了
                // Logger.error("循环向上转型, 获取对象的 DeclaredMethod 异常, methodName: {}, object: {}, parameterTypes: {}, \n\ne: {}", methodName, object, parameterTypes, CommonUtil.getExceptionStackTrace(e));
            }
        }

        return null;
    }

    /**
     * 获取 Field 字段的值。
     *
     * @param field
     * @param fieldParentObj
     * @return
     */
    public static Object getFieldValue(Field field, Object fieldParentObj) {
        Object value = null;
        try {
            field.setAccessible(true);
            value = field.get(fieldParentObj);
        } catch (Exception e) {
            Logger.error("获取 Field 字段的值异常, field: {}, fieldParentObj: {}, \n\ne: {}", field, fieldParentObj, e);
        }
        return value;
    }

    /**
     * 设置 Field 字段的值。
     *
     * @param field
     * @param fieldParentObj
     * @param newValueObj
     */
    public static void setFieldValue(Field field, Object fieldParentObj, Object newValueObj) {
        try {
            field.setAccessible(true);
            field.set(fieldParentObj, newValueObj);
        } catch (Exception e) {
            Logger.error("设置 Field 字段的值异常, field: {}, fieldParentObj: {}, newValueObj: {}, \n\ne: {}", field,
                    fieldParentObj,
                    newValueObj, e);
        }
    }

    /**
     * 获取当前对象中子对象的属性。
     *
     * @param parentObj：当前对象，需要搜索查询字段所属的父类对象；
     * @param searchFieldParentClass：查询字段所属字段的父类对象Class类型；
     * @param searchFieldName：查询字段名称；
     * @return new Object[] { searchFieldParentObject, searchField, searchFieldValue }
     */
    public static Object[] getChildAttr(Object parentObj, Class<?> searchFieldParentClass, String searchFieldName) {
        if (parentObj == null) {
            return null;
        }

        Class<?> parentObjClass = parentObj.getClass();
        Field foundedField = null;
        Object foundedFieldValue = null;
        Object[] result = null;
        try {
            foundedField = parentObjClass.getDeclaredField(searchFieldName);
            foundedField.setAccessible(true);
            foundedFieldValue = foundedField.get(parentObj);

            return new Object[]{parentObj, foundedField, foundedFieldValue};
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            // 此处异常捕获为：找不到属性名异常。
            // 注意在此处我们要手工去帮它找到field应该对象到哪个对象里的值，因为我们不知道它们之间的关系，所以需要手工指定关系，找哪个对象去关联
            result = getChildObjAttr(parentObj, parentObjClass, searchFieldParentClass, searchFieldName);
        } catch (IllegalArgumentException e) {
            Logger.error("获取当前对象中子对象的属性异常, searchFieldParentClass: {}, searchFieldName: {}, parentObj: {}, \n\ne: " +
                            "{}", searchFieldParentClass, searchFieldName,
                    parentObj, e);
        } catch (IllegalAccessException e) {
            Logger.error("获取当前对象中子对象的属性异常, searchFieldParentClass: {}, searchFieldName: {}, parentObj: {}, \n\ne: " +
                            "{}", searchFieldParentClass, searchFieldName,
                    parentObj, e);
        }
        return result;
    }

    /**
     * 获取 parentObj 对象中子类对象的属性。
     *
     * @param parentObj：当前对象，需要搜索查询字段所属的父类对象；
     * @param parentObjClass：当前对象类名称类型，需要搜索查询字段所属的父类类名称类型；
     * @param searchFieldParentClass：查询字段所属字段的父类对象Class类型；
     * @param searchFieldName：查询字段名称；
     * @return new Object[] { searchFieldParentObject, searchField, searchFieldValue }
     */
    private static Object[] getChildObjAttr(Object parentObj, Class<?> parentObjClass, Class<?>
            searchFieldParentClass, String
                                                    searchFieldName) {
        Field[] childFields = parentObjClass.getDeclaredFields();
        Field childField = null;
        Class<?> childFieldType = null;
        for (int i = 0; i < childFields.length; i++) {
            childField = childFields[i];
            childFieldType = childField.getType();

            if (!childFieldType.isMemberClass()) {
                if (childFieldType.equals(searchFieldParentClass)) {
                    return getChildObjAttrDetail(parentObj, childField, searchFieldName);
                }
            } else {
                return getChildAttr(getFieldValue(childField, parentObj), searchFieldParentClass, searchFieldName);
            }
        }
        return null;
    }

    /**
     * 获取 parentObj 对象中子类对象的明细属性。
     *
     * @param parentObj：当前对象，需要搜索查询字段所属的父类对象；
     * @param parentObjChildField：当前对象子对象，需要搜索查询字段所属的父类对象的子对象；
     * @param searchFieldName：查询字段名称；
     * @return new Object[] { searchFieldParentObject, searchField, searchFieldValue }
     */
    private static Object[] getChildObjAttrDetail(Object parentObj, Field parentObjChildField, String searchFieldName) {
        parentObjChildField.setAccessible(true);
        Object searchFieldParentObject = null;
        Class<?> childClass = null;
        Field searchField = null;
        Object searchFieldValue = null;
        try {
            searchFieldParentObject = parentObjChildField.get(parentObj);
            childClass = searchFieldParentObject.getClass();
            searchField = childClass.getDeclaredField(searchFieldName);

            searchField.setAccessible(true);
            searchFieldValue = searchField.get(searchFieldParentObject);

            return new Object[]{searchFieldParentObject, searchField, searchFieldValue};
        } catch (IllegalArgumentException e) {
            Logger.error("获取 parentObj 对象中子类对象的明细属性异常, searchFieldName: {}, parentObj: {}, parentObjChildField: {}, " +
                    "\n\ne: " +
                    "{}", searchFieldName, parentObj, parentObjChildField, e);
        } catch (SecurityException e) {
            Logger.error("获取 parentObj 对象中子类对象的明细属性异常, searchFieldName: {}, parentObj: {}, parentObjChildField: {}, " +
                    "\n\ne: " +
                    "{}", searchFieldName, parentObj, parentObjChildField, e);
        } catch (IllegalAccessException e) {
            Logger.error("获取 parentObj 对象中子类对象的明细属性异常, searchFieldName: {}, parentObj: {}, parentObjChildField: {}, " +
                    "\n\ne: " +
                    "{}", searchFieldName, parentObj, parentObjChildField, e);
        } catch (NoSuchFieldException e) {
            Logger.error("获取 parentObj 对象中子类对象的明细属性异常, searchFieldName: {}, parentObj: {}, parentObjChildField: {}, " +
                    "\n\ne: " +
                    "{}", searchFieldName, parentObj, parentObjChildField, e);
        }

        return null;
    }

    /**
     * 获取接口中所有实现类。
     *
     * @param interfaceClass
     * @return
     */
    public static List<Class<?>> getAllImplClasses(Class<?> interfaceClass) {
        if (!interfaceClass.isInterface()) {
            return null;
        }

        try {
            List<Class<?>> resultClassList = new ArrayList<Class<?>>();

            // 获得接口所在的当前包名
            String packageName = interfaceClass.getPackage().getName();

            // 获取接口所在处的包名下的所有实现类
            List<Class<?>> allClass = getClassesByPackageName(packageName);
            for (int i = 0; i < allClass.size(); i++) {
                if (interfaceClass.isAssignableFrom(allClass.get(i))) {
                    if (!interfaceClass.equals(allClass.get(i))) {// 本身加不进去
                        resultClassList.add(allClass.get(i));
                    }
                }
            }

            return resultClassList;
        } catch (Exception e) {
            Logger.error("获取接口中所有实现类异常, interfaceClass: {}, \n\ne: {}", interfaceClass, e);
            return null;
        }
    }

    /**
     * 通过包名获取当前包名下所有的类。
     *
     * @param packageName
     * @return
     * @throws IOException
     * @throws ClassNotFoundException
     */
    private static List<Class<?>> getClassesByPackageName(String packageName) throws IOException,
            ClassNotFoundException {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        String path = packageName.replace('.', '/');
        Enumeration<URL> resources = classLoader.getResources(path);
        List<File> dirs = new ArrayList<File>();
        while (resources.hasMoreElements()) {
            URL resource = resources.nextElement();
            dirs.add(new File(resource.getFile()));
        }
        List<Class<?>> resultClassList = new ArrayList<Class<?>>();
        for (File directory : dirs) {
            resultClassList.addAll(findClasses(directory, packageName));
        }
        return resultClassList;
    }

    /**
     * 通过路径以及包名，获取所有类。
     *
     * @param directory
     * @param packageName
     * @return
     * @throws ClassNotFoundException
     */
    private static List<Class<?>> findClasses(File directory, String packageName) throws ClassNotFoundException {
        List<Class<?>> resultClassList = new ArrayList<Class<?>>();
        if (!directory.exists()) {
            return resultClassList;
        }

        File[] files = directory.listFiles();
        for (File file : files) {
            if (file.isDirectory()) {
                assert !file.getName().contains(".");

                resultClassList.addAll(findClasses(file, packageName + '.' + file.getName()));
            } else if (file.getName().endsWith(".class")) {
                resultClassList.add(Class.forName(packageName + "." + file.getName().substring(0, file.getName()
                        .length() - 6)));
            }
        }
        return resultClassList;
    }
}
```


### 2.2 自定义注解类，主要用来安插在银行响应类BankDTO身上的；
``` 
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
```


### 2.3 反射获取注解值并给 ResultDTO 赋值的解析器类，非常非常重要的类
``` 
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
```


### 2.4 测试代码，如何调用起我们写的这套功能
``` 
package com.springms.cloud.reflect;

import com.springms.cloud.reflect.util.AnnotationReflectParser;
import com.springms.cloud.reflect.util.xml.BeanXml;

/**
 * 测试类。
 *
 * @author hmilyylimh
 *
 * @version 0.0.1
 *
 * @date 2017/10/24
 *
 */
public class TestReflectDemo {

    public static void main(String[] args) {

        try {
            String xmlData = getXml();
            Class<?> beanClass = getBeanClassPath();

            Object respBankDTO = BeanXml.xml2Bean(xmlData, beanClass);

            ResultDTO resultObject = new ResultDTO();
            ResultDTO.Record record = new ResultDTO.Record();
            resultObject.setRecord(record);

            boolean finished = AnnotationReflectParser.start(respBankDTO, resultObject);
            System.out.println("finished: " + finished);
            System.out.println("=====================================");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 这里可以通过路径反射得到 Class 类，如果你的类有规律的话，那完成可以在这个地方通过设定规则出来得到类名路径。
     *
     * 那么我这里呢，就直接拿个例子来试试而已。
     *
     * @return
     */
    private static Class<?> getBeanClassPath() {
        String className = "com.springms.cloud.reflect.BankDTO";
        return getRespBeanClass(className);
    }

    private static String getXml() {
        String recvContent = "<?xml version='1.0' encoding='GB2312'?>\n" +
                "<packet>\n" +
                "<head>\n" +
                "<transCode>4469</transCode>  \n" +
                "<signFlag>0</signFlag>   \n" +
                "<packetID>1234567890</packetID>      \n" +
                "<timeStamp>2004-07-28 16:14:29</timeStamp> \n" +
                "<returnCode>AAAAAAA</returnCode>  \n" +
                "</head>\n" +
                "<body>\n" +
                "<acctNo>246333388999</acctNo>\n" +
                "<acctName>张三</acctName>\n" +
                "<acctBalance>199098777.97</acctBalance>\n" +
                "<subTotBalance>199098777.97</subTotBalance>\n" +
                "<lists name=\"LoopResult\">\n" +
                "<list>\n" +
                "<subAcctNo>1234567890000000</subAcctNo>\n" +
                "<subAcctBalance>234.56</subAcctBalance>\n" +
                "<subAcctName>账户名称甲</subAcctName>\n" +
                "</list>\n" +
                "</lists>\n" +
                "</body>\n" +
                "</packet>";

        return recvContent;
    }

    /**
     * 获取响应类名的 Class 对象。
     *
     * @return
     */
    private static Class<?> getRespBeanClass(String className) {
        Class<?> respClass = null;
        try {
            respClass = Class.forName(className);
            return respClass;
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(className + " 该响应类路径不存在", e);
        }
    }
}

```

### 2.5 总结
``` 
1、虽然这样写可以偷懒了，也可以招非开发人员直接上手撸代码直接开发功能模块，但是不方便的地方就是得发版升级；
2、后期想法，我们不是有 "Java运行时动态加载类" 这么一说么？后期准备将这一套代码放在某个目录上传，或者直接放到数据库存储，然后动态加载执行对应功能；
3、想法虽然不错，路漫漫其修远兮，慢慢努力吧，顺便祝各位猿猿们节日快乐；
```


## 三、下载地址

[https://git.oschina.net/ylimhhmily/SpringCloudTutorial.git](https://git.oschina.net/ylimhhmily/SpringCloudTutorial.git)

SpringCloudTutorial交流QQ群: 235322432

SpringCloudTutorial交流微信群: [微信沟通群二维码图片链接](https://gitee.com/ylimhhmily/SpringCloudTutorial/blob/master/doc/qrcode/SpringCloudWeixinQrcode.png)

欢迎关注，您的肯定是对我最大的支持!!!





























