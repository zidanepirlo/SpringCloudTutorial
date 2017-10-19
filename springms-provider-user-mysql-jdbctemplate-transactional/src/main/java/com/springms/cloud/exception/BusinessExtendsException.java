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
public class BusinessExtendsException extends Exception {

    public BusinessExtendsException() {
    }

    public BusinessExtendsException(String message) {
        super(message);
    }

    public BusinessExtendsException(String message, Throwable cause) {
        super(message, cause);
    }

    public BusinessExtendsException(Throwable cause) {
        super(cause);
    }

    public BusinessExtendsException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
