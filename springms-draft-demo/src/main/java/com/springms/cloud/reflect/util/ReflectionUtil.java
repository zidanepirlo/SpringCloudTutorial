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