package com.wpp;

import org.objectweb.asm.util.ASMifier;

import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.lang.reflect.Method;

/**
 * @author wpp
 * @desc
 * @see
 * @since 2022/12/12
 */
public class DebugMain {
    public static void main(String[] args) throws Exception {
        final FileOutputStream out = new FileOutputStream("/Users/wpp/test/toy-jdk-proxy/target/$Proxy0Dump.java");
        PrintWriter printWriter = new PrintWriter(out, true);
        Method main = ASMifier.class.getDeclaredMethod("main", String[].class, PrintWriter.class, PrintWriter.class);
        main.setAccessible(true);
        main.invoke(null, new String[]{"com.wpp.$Proxy0"}, printWriter, printWriter);

    }

}
