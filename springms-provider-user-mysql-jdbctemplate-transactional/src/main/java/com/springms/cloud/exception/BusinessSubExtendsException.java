package com.springms.cloud.exception;

/**
 *
 *
 * @author hmilyylimh
 *
 * @version 0.0.1
 *
 * @date 17/10/18
 *
 */
public class BusinessSubExtendsException extends BusinessExtendsException {

    public BusinessSubExtendsException() {
    }

    public BusinessSubExtendsException(String message) {
        super(message);
    }

    public BusinessSubExtendsException(String message, Throwable cause) {
        super(message, cause);
    }

    public BusinessSubExtendsException(Throwable cause) {
        super(cause);
    }

    public BusinessSubExtendsException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
