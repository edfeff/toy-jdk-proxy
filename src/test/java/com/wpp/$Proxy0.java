package com.wpp;

import java.lang.reflect.Method;

/**
 * @author wpp
 * @desc
 * @see
 * @since 2022/12/12
 */

public class $Proxy0 extends ToyProxy implements IA {
    private static Method m1;
    private static Method m4;
    private static Method m2;
    private static Method m3;
    private static Method m6;
    private static Method m0;
    private static Method m5;

    private static Method m7;

    public $Proxy0(InvocationHandler var1) {
        super(var1);
    }

    public boolean equals(Object var1) {
        return (Boolean) super.h.invoke(this, m1, new Object[]{var1});
    }

    public void voidNoArg() {
        super.h.invoke(this, m4, (Object[]) null);
    }

    public String toString() {
        return (String) super.h.invoke(this, m2, (Object[]) null);
    }

    public int hashCode() {
        return (Integer) super.h.invoke(this, m0, (Object[]) null);
    }

    public void voidArg(String var1) {
        super.h.invoke(this, m3, new Object[]{var1});
    }

    public String stringNoArg() {
        return (String) super.h.invoke(this, m6, (Object[]) null);
    }

    public String stringArg(String var1) {
        return (String) super.h.invoke(this, m5, new Object[]{var1});
    }

    @Override
    public String stringArg6(String arg1, String arg2, String arg3, String arg4, String arg5, String arg6, String arg7) {
        return (String) super.h.invoke(this, m7, new Object[]{arg1, arg2, arg3, arg4, arg5, arg6, arg7});
    }

    static {
        try {
            m0 = Class.forName("java.lang.Object").getMethod("hashCode");
            m1 = Class.forName("java.lang.Object").getMethod("equals", Class.forName("java.lang.Object"));
            m2 = Class.forName("java.lang.Object").getMethod("toString");

            m3 = Class.forName("com.wpp.IA").getMethod("voidArg", Class.forName("java.lang.String"));
            m4 = Class.forName("com.wpp.IA").getMethod("voidNoArg");
            m5 = Class.forName("com.wpp.IA").getMethod("stringArg", Class.forName("java.lang.String"));
            m6 = Class.forName("com.wpp.IA").getMethod("stringNoArg");
            m7 = Class.forName("com.wpp.IA").getMethod("stringArg6", String.class, String.class, String.class, String.class, String.class, String.class, String.class);

        } catch (Exception var3) {
            throw new RuntimeException(var3);
        }
    }
}
