package com.springms.cloud.reflect;

import com.springms.cloud.reflect.util.xml.AdapterXmlBean;
import com.springms.cloud.reflect.util.CustomFieldAnnotation;
import com.springms.cloud.reflect.util.mapping.impl.HistoryBalanceConsts;
import com.springms.cloud.reflect.util.xml.MappingField;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 银行相应对象DTO.
 *
 * @author hmilyylimh
 *
 * @version 0.0.1
 *
 * @date 2017/10/24
 *
 */
public class BankDTO implements AdapterXmlBean {

    private static final org.slf4j.Logger Logger = LoggerFactory.getLogger(BankDTO.class);

    @CustomFieldAnnotation(customFieldName = HistoryBalanceConsts.HISTORY_BALANCE_KEY_HEAD, customFieldType = CustomFieldAnnotation.CustomFieldType.CLASS)
    private Head head;

    @CustomFieldAnnotation(customFieldName = HistoryBalanceConsts.HISTORY_BALANCE_KEY_BODY, customFieldType = CustomFieldAnnotation.CustomFieldType.CLASS)
    private Body body;

    public static class Head implements AdapterXmlBean {

        @CustomFieldAnnotation(customFieldName = HistoryBalanceConsts.HISTORY_BALANCE_KEY_TRANS_CODE)
        private String transCode;
        @CustomFieldAnnotation(customFieldName = HistoryBalanceConsts.HISTORY_BALANCE_KEY_SIGN_FLAG)
        private String signFlag;
        @CustomFieldAnnotation(customFieldName = HistoryBalanceConsts.HISTORY_BALANCE_KEY_PACKET_ID)
        private String packetID;
        private String timeStamp;
        @CustomFieldAnnotation(customFieldName = HistoryBalanceConsts.HISTORY_BALANCE_KEY_RETURN_CODE)
        private String returnCode;

        @Override
        public List getMapping() {
            List<MappingField> mappingList = new ArrayList<MappingField>();
            mappingList.add(new MappingField("transCode", "transCode"));
            mappingList.add(new MappingField("signFlag", "signFlag"));
            mappingList.add(new MappingField("packetID", "packetID"));
            mappingList.add(new MappingField("timeStamp", "timeStamp"));
            mappingList.add(new MappingField("returnCode", "returnCode"));
            return mappingList;
        }

        @Override
        public String getRootClassName() {
            return "head";
        }

        public String getTransCode() {
            return transCode;
        }

        public void setTransCode(String transCode) {
            this.transCode = transCode;
        }

        public String getSignFlag() {
            return signFlag;
        }

        public void setSignFlag(String signFlag) {
            this.signFlag = signFlag;
        }

        public String getPacketID() {
            return packetID;
        }

        public void setPacketID(String packetID) {
            this.packetID = packetID;
        }

        public String getTimeStamp() {
            return timeStamp;
        }

        public void setTimeStamp(String timeStamp) {
            this.timeStamp = timeStamp;
        }

        public String getReturnCode() {
            return returnCode;
        }

        public void setReturnCode(String returnCode) {
            this.returnCode = returnCode;
        }

        @Override
        public String toString() {
            return "Head{" +
                    "transCode='" + transCode + '\'' +
                    ", signFlag='" + signFlag + '\'' +
                    ", packetID='" + packetID + '\'' +
                    ", timeStamp='" + timeStamp + '\'' +
                    ", returnCode='" + returnCode + '\'' +
                    '}';
        }
    }

    public static class Body implements AdapterXmlBean {

        @CustomFieldAnnotation(customFieldName = HistoryBalanceConsts.HISTORY_BALANCE_KEY_RECORD_ACCT_NO)
        private String acctNo;
        @CustomFieldAnnotation(customFieldName = HistoryBalanceConsts.HISTORY_BALANCE_KEY_RECORD_ACCT_NAME)
        private String acctName;
        @CustomFieldAnnotation(customFieldName = HistoryBalanceConsts.HISTORY_BALANCE_KEY_RECORD_SUB_TOT_BALANCE)
        private String subTotBalance;
        private String acctBalance;
        private ListWrapper lists;

        /****************************************************************/
        /** 自定义和通用对象对应的字段 Start */
        /****************************************************************/
        @CustomFieldAnnotation(customFieldName = HistoryBalanceConsts.HISTORY_BALANCE_KEY_RECORD_BALANCE)
        private BigDecimal recordBalance;
        /****************************************************************/
        /** 自定义和通用对象对应的字段 End */
        /****************************************************************/

        @Override
        public List getMapping() {
            List<MappingField> mappingList = new ArrayList<MappingField>();
            mappingList.add(new MappingField("lists", "lists"));
            mappingList.add(new MappingField("acctNo", "acctNo"));
            mappingList.add(new MappingField("acctName", "acctName"));
            mappingList.add(new MappingField("acctBalance", "acctBalance"));
            mappingList.add(new MappingField("subTotBalance", "subTotBalance"));
            return mappingList;
        }

        @Override
        public String getRootClassName() {
            return "body";
        }

        public static class ListWrapper implements AdapterXmlBean {

            private ListItem[] list;//账号

            public String getRootClassName() {
                return "lists";
            }

            public List getMapping() {
                List mappingList = new ArrayList();
                mappingList.add(new MappingField("list","list"));
                mappingList.add(new MappingField("ATTRIBUTE","ATTRIBUTE"));
                return mappingList;
            }

            public ListItem[] getList() {
                return list;
            }


            public void setList(ListItem[] list) {
                this.list = list;
            }

            public static class ListItem implements AdapterXmlBean {

                public String subAcctNo;
                public String subAcctBalance;
                public String subAcctName;

                public List getMapping() {
                    List<MappingField> mappingList = new ArrayList<MappingField>();
                    mappingList.add(new MappingField("subAcctNo", "subAcctNo"));
                    mappingList.add(new MappingField("subAcctBalance", "subAcctBalance"));
                    mappingList.add(new MappingField("subAcctName", "subAcctName"));
                    return mappingList;
                }

                @Override
                public String getRootClassName() {
                    return "list";
                }

                public String getSubAcctNo() {
                    return subAcctNo;
                }

                public void setSubAcctNo(String subAcctNo) {
                    this.subAcctNo = subAcctNo;
                }

                public String getSubAcctBalance() {
                    return subAcctBalance;
                }

                public void setSubAcctBalance(String subAcctBalance) {
                    this.subAcctBalance = subAcctBalance;
                }

                public String getSubAcctName() {
                    return subAcctName;
                }

                public void setSubAcctName(String subAcctName) {
                    this.subAcctName = subAcctName;
                }

                @Override
                public String toString() {
                    return "ListItem{" +
                            "subAcctNo='" + subAcctNo + '\'' +
                            ", subAcctBalance='" + subAcctBalance + '\'' +
                            ", subAcctName='" + subAcctName + '\'' +
                            '}';
                }
            }

            @Override
            public String toString() {
                return "ListWrapper{" +
                        "list=" + Arrays.toString(list) +
                        '}';
            }
        }

        public String getAcctNo() {
            return acctNo;
        }

        public void setAcctNo(String acctNo) {
            this.acctNo = acctNo;
        }

        public String getAcctName() {
            return acctName;
        }

        public void setAcctName(String acctName) {
            this.acctName = acctName;
        }

        public String getAcctBalance() {
            return acctBalance;
        }

        public void setAcctBalance(String acctBalance) {
            this.acctBalance = acctBalance;
        }

        public String getSubTotBalance() {
            return subTotBalance;
        }

        public void setSubTotBalance(String subTotBalance) {
            this.subTotBalance = subTotBalance;
        }

        public ListWrapper getLists() {
            return lists;
        }

        public void setLists(ListWrapper lists) {
            this.lists = lists;
        }

        public BigDecimal getRecordBalance() {
            return recordBalance;
        }

        public void setRecordBalance(BigDecimal recordBalance) {
            this.recordBalance = recordBalance;
        }

        @Override
        public String toString() {
            return "Body{" +
                    "acctNo='" + acctNo + '\'' +
                    ", acctName='" + acctName + '\'' +
                    ", acctBalance='" + acctBalance + '\'' +
                    ", subTotBalance='" + subTotBalance + '\'' +
                    ", lists=" + lists +
                    ", recordBalance=" + recordBalance +
                    '}';
        }
    }

    @Override
    public List getMapping() {
        List mappingList = new ArrayList();

        mappingList.add(new MappingField("body", "body"));
        mappingList.add(new MappingField("head", "head"));

        return mappingList;
    }

    @Override
    public String getRootClassName() {
        return "packet";
    }

    public Body getBody() {
        return body;
    }

    public void setBody(Body body) {
        this.body = body;
    }

    public Head getHead() {
        return head;
    }

    public void setHead(Head head) {
        this.head = head;
    }
}