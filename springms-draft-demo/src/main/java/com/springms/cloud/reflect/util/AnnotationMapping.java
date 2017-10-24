package com.springms.cloud.reflect.util;

import com.springms.cloud.reflect.util.mapping.IKeyConstant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 注解映射类。
 *
 * @author hmilyylimh
 *
 * @version 0.0.1
 *
 * @date 2017/10/24
 *
 */
public class AnnotationMapping {

    private static final Logger Logger = LoggerFactory.getLogger(AnnotationMapping.class);

    /**
     * Key：通用对象中有功能意思的属性名称；<br/>
     * Value：数组类型，{ 通用对象字段名称, 通用对象字段名称所属处上级对象Class类型 }。
     */
    private static Map<String, Object[]> sGeneralFieldMapping = new HashMap<String, Object[]>();

    /**
     * 特殊接口解析法则，需要重新处理，目前 value 值还没想好填啥，先放个 Map 再说。
     */
    private static Map<String, String> sSpecialInterfaceMap = new HashMap<String, String>();

    static {
        initAllKeyConsts();
    }

    /**
     * 获取银行对象中注解里面的customFieldName值与通用对象字段名称映射集合。<br/>
     * Key：通用对象中有功能意思的属性名称；<br/>
     * Value：数组类型，{ 通用对象字段名称, 通用对象字段名称所属处上级对象Class类型 }。
     *
     * @return
     */
    public static Map<String, Object[]> getGeneralFieldMapping() {
        return sGeneralFieldMapping;
    }

    /**
     * 获取特殊接口集合。
     *
     * @return
     */
    public static Map<String, String> getSpecialInterfaceMap() {
        return sSpecialInterfaceMap;
    }

    /**
     * 初始化所有常量类。
     */
    private static void initAllKeyConsts() {
        List<Class<?>> allImplClasses = ReflectionUtil.getAllImplClasses(IKeyConstant.class);
        if (allImplClasses == null || allImplClasses.isEmpty()) {
            return;
        }

        Map<String, Object[]> map = getGeneralFieldMapping();
        try {
            for (Class<?> clazz : allImplClasses) {
                Object constructor = clazz.newInstance();
                Method method = clazz.getMethod("init", Map.class);
                method.invoke(constructor, new Object[]{map});
            }
        } catch (Exception e) {
            Logger.error("============    初始化所有常量类异常    ============", e);
        }
    }

    /**
     * 通用对象中的字段名称。
     *
     * @author hmilyylimh
     *
     * @version 0.0.1
     *
     * @date 2017/10/24
     *
     */
    public static final class BaseFieldName {

        /** 客户端编号，用于判断队列属于谁的任务 */
        public static final String GENERAL_FIELD_NAME_CLIENT_ID = "clientId";
        /** 用户编号，Beos服务器分配给客户端的用户 */
        public static final String GENERAL_FIELD_NAME_USER_ID = "userId";
        /** 企业Id */
        public static final String GENERAL_FIELD_NAME_ENT_ID = "entId";
        /** 请求日期 */
        public static final String GENERAL_FIELD_NAME_REQ_DATE = "reqDate";
        /** 业务类型 */
        public static final String GENERAL_FIELD_NAME_BUSINESS_TYPE = "businessType";
        /** 法人编码 */
        public static final String GENERAL_FIELD_NAME_INCORPORATOR_ID = "incorporatorId";
        /** 交易序列号头ID */
        public static final String GENERAL_FIELD_NAME_TRADE_PACKAGE_ID = "tradePackageId";
        /** 银行包头ID */
        public static final String GENERAL_FIELD_NAME_BANK_PACKAGE_ID = "bankPackageId";
        /** 返回信息 */
        public static final String GENERAL_FIELD_NAME_RESULT_MESSAGE = "resultMessage";
        /** 返回代码 */
        public static final String GENERAL_FIELD_NAME_RESULT_CODE = "resultCode";
        /** 交易状态 */
        public static final String GENERAL_FIELD_NAME_STATUS = "status";
        /** 农行读取文件标识 */
        public static final String GENERAL_FIELD_NAME_FLOW_FILE_FLAG = "fileFlag";
        /** 农行流水文件名 */
        public static final String GENERAL_FIELD_NAME_FLOW_FILE_NAME = "flowFileName";
        /** 头对象HeadObj */
        public static final String GENERAL_FIELD_NAME_MEANINGLESS_OBJECT = "meaninglessObject";
        /** 成功总笔数 */
        public static final String GENERAL_FIELD_NAME_SUCCESS_CTS = "successCts";
        /** 成功总金额 */
        public static final String GENERAL_FIELD_NAME_SUCCESS_AMT = "successAmt";
        /** 失败总笔数 */
        public static final String GENERAL_FIELD_NAME_FAILD_CTS = "faildCts";
        /** 失败总金额 */
        public static final String GENERAL_FIELD_NAME_FAILD_AMT = "faildAmt";
    }
}