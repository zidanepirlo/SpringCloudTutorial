package com.springms.cloud.reflect;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 结果对象DTO。
 *
 * @author hmilyylimh
 *
 * @version 0.0.1
 *
 * @date 2017/10/24
 *
 */
public class ResultDTO {

    private String clientId;
    protected String tradePackageId;
    private String userId;
    private String entId;
    protected String bankPackageId;
    protected String resultCode;
    protected String resultMessage;
    protected int status;
    private String fileFlag;
    private String flowFileName;
    private String tradeRecordId;
    private Record record;

    protected MeaninglessObject meaninglessObject;

    public static class Record implements Serializable {

        private String currencyCode;
        private String balance;
        private String acctNo;
        private String acctName;
        private String subTotBalance;
        private BigDecimal availableBalance;
        private BigDecimal freezeBalance;
        private BigDecimal overdraftBalance;

        public String getCurrencyCode() {
            return currencyCode;
        }

        public void setCurrencyCode(String currencyCode) {
            this.currencyCode = currencyCode;
        }

        public String getBalance() {
            return balance;
        }

        public void setBalance(String balance) {
            this.balance = balance;
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

        public String getSubTotBalance() {
            return subTotBalance;
        }

        public void setSubTotBalance(String subTotBalance) {
            this.subTotBalance = subTotBalance;
        }

        public BigDecimal getAvailableBalance() {
            return availableBalance;
        }

        public void setAvailableBalance(BigDecimal availableBalance) {
            this.availableBalance = availableBalance;
        }

        public BigDecimal getFreezeBalance() {
            return freezeBalance;
        }

        public void setFreezeBalance(BigDecimal freezeBalance) {
            this.freezeBalance = freezeBalance;
        }

        public BigDecimal getOverdraftBalance() {
            return overdraftBalance;
        }

        public void setOverdraftBalance(BigDecimal overdraftBalance) {
            this.overdraftBalance = overdraftBalance;
        }
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getTradePackageId() {
        return tradePackageId;
    }

    public void setTradePackageId(String tradePackageId) {
        this.tradePackageId = tradePackageId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getEntId() {
        return entId;
    }

    public void setEntId(String entId) {
        this.entId = entId;
    }

    public String getBankPackageId() {
        return bankPackageId;
    }

    public void setBankPackageId(String bankPackageId) {
        this.bankPackageId = bankPackageId;
    }

    public String getResultCode() {
        return resultCode;
    }

    public void setResultCode(String resultCode) {
        this.resultCode = resultCode;
    }

    public String getResultMessage() {
        return resultMessage;
    }

    public void setResultMessage(String resultMessage) {
        this.resultMessage = resultMessage;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getFileFlag() {
        return fileFlag;
    }

    public void setFileFlag(String fileFlag) {
        this.fileFlag = fileFlag;
    }

    public String getFlowFileName() {
        return flowFileName;
    }

    public void setFlowFileName(String flowFileName) {
        this.flowFileName = flowFileName;
    }

    public String getTradeRecordId() {
        return tradeRecordId;
    }

    public void setTradeRecordId(String tradeRecordId) {
        this.tradeRecordId = tradeRecordId;
    }

    public Record getRecord() {
        return record;
    }

    public void setRecord(Record record) {
        this.record = record;
    }

    /**
     * 无意思的对象，仅仅只是为了对应值而用的。
     *
     * @author hmilyylimh
     *
     * @version 0.0.1
     *
     * @date 2017/10/24
     *
     */
	public static class MeaninglessObject implements Serializable {
	}

	public MeaninglessObject getMeaninglessObject() {
		return meaninglessObject;
	}

	public void setMeaninglessObject(MeaninglessObject meaninglessObject) {
		this.meaninglessObject = meaninglessObject;
	}
}
