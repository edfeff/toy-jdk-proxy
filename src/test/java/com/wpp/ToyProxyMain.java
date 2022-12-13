package com.wpp;

import java.lang.reflect.Method;

/**
 * @author wpp
 * @desc
 * @see
 * @since 2022/12/12
 */
public class ToyProxyMain {
    public static void main(String[] args) {
        IA ia = ToyProxy.newProxyInstance(ToyProxyMain.class.getClassLoader(), new Class[]{IA.class}, new InvocationHandler() {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) {
                System.out.println("拦截调用: " + method.getName());
                if ("toString".equals(method.getName())) {
                    return "proxy toString";
                } else if ("hashCode".equals(method.getName())) {
                    return "proxy toString".hashCode();
                } else if ("equals".equals(method.getName())) {
                    return false;
                } else {
                    if (args != null) {
                        System.out.println("args.length=" + args.length);
                    } else {
                        System.out.println("args.length=" + 0);
                    }
                }
                return null;
            }
        });

        ia.voidNoArg();
        ia.voidArg("1");
        ia.stringNoArg();
        ia.stringArg("2");
        System.out.println(ia.toString());
        System.out.println(ia.hashCode());
        System.out.println(ia.equals(ia));
    }

}
