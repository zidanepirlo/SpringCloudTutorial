package com.springms.cloud.exception;

/**
 * 回滚异常，测试语句：@Transactional(propagation = Propagation.REQUIRED, isolation= Isolation.DEFAULT, rollbackFor = Exception.class) 是否生效。
 *
 * 结果：含有该属性的 rollbackFor = Exception.class，只要抛出的异常属于 Exception 的子类的话，就可以正常回滚数据；
 *
 * @author hmilyylimh
 *
 * @version 0.0.1
 *
 * @date 17/10/18
 *
 */
public class RollbackExceptionExtendsRuntimeException extends RuntimeException {

    public RollbackExceptionExtendsRuntimeException() {
    }

    public RollbackExceptionExtendsRuntimeException(String message) {
        super(message);
    }

    public RollbackExceptionExtendsRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }

    public RollbackExceptionExtendsRuntimeException(Throwable cause) {
        super(cause);
    }

    public RollbackExceptionExtendsRuntimeException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
