package com.springms.cloud.reflect.util.mapping.impl;

import com.springms.cloud.reflect.ResultDTO;
import com.springms.cloud.reflect.util.AnnotationMapping;
import com.springms.cloud.reflect.util.mapping.IKeyConstant;

import java.util.Map;

/**
 * 历史余额常量类Key。
 *
 * @author hmilyylimh
 *
 * @version 0.0.1
 *
 * @date 2017/10/24
 *
 */
public class HistoryBalanceConsts implements IKeyConstant {

    @Override
    public void init(Map<String, Object[]> map) {

        map.put(HISTORY_BALANCE_KEY_RECORD_BALANCE, new Object[]{"balance", ResultDTO.Record.class});
        map.put(HISTORY_BALANCE_KEY_RECORD_ACCT_NO, new Object[]{"acctNo", ResultDTO.Record.class});
        map.put(HISTORY_BALANCE_KEY_RECORD_ACCT_NAME, new Object[]{"acctName", ResultDTO.Record.class});
        map.put(HISTORY_BALANCE_KEY_RECORD_SUB_TOT_BALANCE, new Object[]{"subTotBalance", ResultDTO.Record.class});

        map.put(HISTORY_BALANCE_KEY_HEAD, new Object[]{AnnotationMapping.BaseFieldName.GENERAL_FIELD_NAME_MEANINGLESS_OBJECT});
        map.put(HISTORY_BALANCE_KEY_BODY, new Object[]{"record"});

        map.put(HISTORY_BALANCE_KEY_TRANS_CODE, new Object[]{"clientId"});
        map.put(HISTORY_BALANCE_KEY_SIGN_FLAG, new Object[]{"fileFlag"});
        map.put(HISTORY_BALANCE_KEY_PACKET_ID, new Object[]{"tradePackageId"});
        map.put(HISTORY_BALANCE_KEY_RETURN_CODE, new Object[]{"resultCode"});
    }

    public static final String HISTORY_BALANCE_KEY_HEAD = "HISTORY_BALANCE_KEY_HEAD";
    public static final String HISTORY_BALANCE_KEY_BODY = "HISTORY_BALANCE_KEY_BODY";

    public static final String HISTORY_BALANCE_KEY_TRANS_CODE = "HISTORY_BALANCE_KEY_TRANS_CODE";
    public static final String HISTORY_BALANCE_KEY_SIGN_FLAG = "HISTORY_BALANCE_KEY_SIGN_FLAG";
    public static final String HISTORY_BALANCE_KEY_PACKET_ID = "HISTORY_BALANCE_KEY_PACKET_ID";
    public static final String HISTORY_BALANCE_KEY_RETURN_CODE = "HISTORY_BALANCE_KEY_RETURN_CODE";

    public static final String HISTORY_BALANCE_KEY_RECORD_BALANCE = "HISTORY_BALANCE_KEY_RECORD_BALANCE";
    public static final String HISTORY_BALANCE_KEY_RECORD_ACCT_NO = "HISTORY_BALANCE_KEY_RECORD_ACCT_NO";
    public static final String HISTORY_BALANCE_KEY_RECORD_ACCT_NAME = "HISTORY_BALANCE_KEY_RECORD_ACCT_NAME";
    public static final String HISTORY_BALANCE_KEY_RECORD_SUB_TOT_BALANCE = "HISTORY_BALANCE_KEY_RECORD_SUB_TOT_BALANCE";

}