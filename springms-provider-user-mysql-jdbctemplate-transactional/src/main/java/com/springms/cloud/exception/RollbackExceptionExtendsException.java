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
public class RollbackExceptionExtendsException extends Exception {

    public RollbackExceptionExtendsException() {
    }

    public RollbackExceptionExtendsException(String message) {
        super(message);
    }

    public RollbackExceptionExtendsException(String message, Throwable cause) {
        super(message, cause);
    }

    public RollbackExceptionExtendsException(Throwable cause) {
        super(cause);
    }

    public RollbackExceptionExtendsException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
