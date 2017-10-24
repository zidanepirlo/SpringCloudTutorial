package com.springms.cloud.reflect.util.mapping.impl;

import com.springms.cloud.reflect.util.AnnotationMapping;
import com.springms.cloud.reflect.util.mapping.IKeyConstant;

import java.util.Map;

/**
 * 普通公用常量类Key。
 *
 * @author hmilyylimh
 *
 * @version 0.0.1
 *
 * @date 2017/10/24
 *
 */
public class GeneralConsts implements IKeyConstant {

    /**
     * 初始化平安银行字段。通用的对象父类则可以参考 BeosAdapterRequest 类。
     *
     * @param map
     */
    @Override
    public void init(Map<String, Object[]> map) {
        map.put(GENERAL_KEY_CLIENT_ID, new Object[]{AnnotationMapping.BaseFieldName.GENERAL_FIELD_NAME_CLIENT_ID});
        map.put(GENERAL_KEY_TRADE_PACKAGE_ID, new Object[]{AnnotationMapping.BaseFieldName.GENERAL_FIELD_NAME_TRADE_PACKAGE_ID});
        map.put(GENERAL_KEY_USER_ID, new Object[]{AnnotationMapping.BaseFieldName.GENERAL_FIELD_NAME_USER_ID});
        map.put(GENERAL_KEY_ENT_ID, new Object[]{AnnotationMapping.BaseFieldName.GENERAL_FIELD_NAME_ENT_ID});
        map.put(GENERAL_KEY_REQ_DATE, new Object[]{AnnotationMapping.BaseFieldName.GENERAL_FIELD_NAME_REQ_DATE});
        map.put(GENERAL_KEY_BUSINESS_TYPE, new Object[]{AnnotationMapping.BaseFieldName.GENERAL_FIELD_NAME_BUSINESS_TYPE});
        map.put(GENERAL_KEY_INCORPORATOR_ID, new Object[]{AnnotationMapping.BaseFieldName.GENERAL_FIELD_NAME_INCORPORATOR_ID});
        map.put(GENERAL_KEY_MEANINGLESS_OBJECT, new Object[]{AnnotationMapping.BaseFieldName.GENERAL_FIELD_NAME_MEANINGLESS_OBJECT});

        map.put(GENERAL_KEY_BANK_PACKAGE_ID, new Object[]{AnnotationMapping.BaseFieldName.GENERAL_FIELD_NAME_BANK_PACKAGE_ID});
        map.put(GENERAL_KEY_RESULT_CODE, new Object[]{AnnotationMapping.BaseFieldName.GENERAL_FIELD_NAME_RESULT_CODE});
        map.put(GENERAL_KEY_RESULT_MESSAGE, new Object[]{AnnotationMapping.BaseFieldName.GENERAL_FIELD_NAME_RESULT_MESSAGE});
        map.put(GENERAL_KEY_STATUS, new Object[]{AnnotationMapping.BaseFieldName.GENERAL_FIELD_NAME_STATUS});
        map.put(GENERAL_KEY_FLOW_FILE_FLAG, new Object[]{AnnotationMapping.BaseFieldName.GENERAL_FIELD_NAME_FLOW_FILE_FLAG});
        map.put(GENERAL_KEY_FLOW_FILE_NAME, new Object[]{AnnotationMapping.BaseFieldName.GENERAL_FIELD_NAME_FLOW_FILE_NAME});
        map.put(GENERAL_KEY_SUCCESS_CTS, new Object[]{AnnotationMapping.BaseFieldName.GENERAL_FIELD_NAME_SUCCESS_CTS});
        map.put(GENERAL_KEY_SUCCESS_AMT, new Object[]{AnnotationMapping.BaseFieldName.GENERAL_FIELD_NAME_SUCCESS_AMT});
        map.put(GENERAL_KEY_FAILD_CTS, new Object[]{AnnotationMapping.BaseFieldName.GENERAL_FIELD_NAME_FAILD_CTS});
        map.put(GENERAL_KEY_FAILD_AMT, new Object[]{AnnotationMapping.BaseFieldName.GENERAL_FIELD_NAME_FAILD_AMT});
    }

    /** 客户端编号，用于判断队列属于谁的任务 */
    public static final String GENERAL_KEY_CLIENT_ID = "GENERAL_KEY_CLIENT_ID";
    /** 用户编号，Beos服务器分配给客户端的用户 */
    public static final String GENERAL_KEY_USER_ID = "GENERAL_KEY_USER_ID";
    /** 企业Id，用于读取数据库信息 add by ex-chenjiarong001 20141202 */
    public static final String GENERAL_KEY_ENT_ID = "GENERAL_KEY_ENT_ID";
    /** 请求日期 */
    public static final String GENERAL_KEY_REQ_DATE = "GENERAL_KEY_REQ_DATE";
    /** 业务类型 */
    public static final String GENERAL_KEY_BUSINESS_TYPE = "GENERAL_KEY_BUSINESS_TYPE";
    /** 法人编码 */
    public static final String GENERAL_KEY_INCORPORATOR_ID = "GENERAL_KEY_INCORPORATOR_ID";
    /** 交易序列号头ID */
    public static final String GENERAL_KEY_TRADE_PACKAGE_ID = "GENERAL_KEY_TRADE_PACKAGE_ID";
    /** 交易序列号明细ID */
    public static final String GENERAL_KEY_TRADE_RECORD_ID = "GENERAL_KEY_TRADE_RECORD_ID";
    /** 银行包头ID */
    public static final String GENERAL_KEY_BANK_PACKAGE_ID = "GENERAL_KEY_BANK_PACKAGE_ID";
    /** 银行包明细ID */
    public static final String GENERAL_KEY_BANK_RECORD_ID = "GENERAL_KEY_BANK_RECORD_ID";
    /** 返回信息 */
    public static final String GENERAL_KEY_RESULT_MESSAGE = "GENERAL_KEY_RESULT_MESSAGE";
    /** 返回代码 */
    public static final String GENERAL_KEY_RESULT_CODE = "GENERAL_KEY_RESULT_CODE";
    /** 交易状态 */
    public static final String GENERAL_KEY_STATUS = "GENERAL_KEY_STATUS";
    /** 农行读取文件标识 */
    public static final String GENERAL_KEY_FLOW_FILE_FLAG = "GENERAL_KEY_FLOW_FILE_FLAG";
    /** 农行流水文件名 */
    public static final String GENERAL_KEY_FLOW_FILE_NAME = "GENERAL_KEY_FLOW_FILE_NAME";
    /** 成功总笔数 */
    public static final String GENERAL_KEY_SUCCESS_CTS = "GENERAL_KEY_SUCCESS_CTS";
    /** 成功总金额 */
    public static final String GENERAL_KEY_SUCCESS_AMT = "GENERAL_KEY_SUCCESS_AMT";
    /** 失败总笔数 */
    public static final String GENERAL_KEY_FAILD_CTS = "GENERAL_KEY_FAILD_CTS";
    /** 失败总金额 */
    public static final String GENERAL_KEY_FAILD_AMT = "GENERAL_KEY_FAILD_AMT";
    /** 头对象HeadObj */
    public static final String GENERAL_KEY_MEANINGLESS_OBJECT = "GENERAL_KEY_MEANINGLESS_OBJECT";
}