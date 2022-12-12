package com.wpp;

import java.lang.reflect.Method;

/**
 * @author wpp
 * @desc
 * @see
 * @since 2022/12/12
 */
public interface InvocationHandler {
    /**
     * 不允许抛出检查异常
     *
     * @param proxy
     * @param method
     * @param args
     * @return
     */
    public Object invoke(Object proxy, Method method, Object[] args);
}
