package com.wpp;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * @author wpp
 * @desc
 * @see
 * @since 2022/12/12
 */
public class Base {

    public static void main(String[] args) {
        System.setProperty("sun.misc.ProxyGenerator.saveGeneratedFiles", "true");
        Object proxy = Proxy.newProxyInstance(Base.class.getClassLoader(),
                new Class[]{IA.class},
                new InvocationHandler() {
                    @Override
                    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                        return null;
                    }
                }
        );

    }

}
