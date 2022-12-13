package com.wpp;


import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static org.objectweb.asm.Opcodes.*;

/**
 * @author wpp
 * @desc
 * @see
 * @since 2022/12/12
 */
public class ToyProxy {
    protected InvocationHandler h;

    private ToyProxy() {
    }

    protected ToyProxy(InvocationHandler h) {
        this.h = h;
    }

    public static <T> T newProxyInstance(ClassLoader loader, Class<?>[] interfaces, InvocationHandler h) {
        return new ToyProxy(h).getProxy(loader, interfaces);
    }


    public static <T> T newProxyInstance(ClassLoader loader, Class<?> interfaces, InvocationHandler h) {
        return new ToyProxy(h).getProxy(loader, interfaces);
    }

    private <T> T getProxy(ClassLoader loader, Class<?> interfaces) {
        return getProxy(loader, new Class[]{interfaces});
    }

    static class MethodInfo {
        Class owner;
        Method method;

        String methodFieldName;

        MethodInfo(Class owner, Method method, String methodFieldName) {
            this.owner = owner;
            this.method = method;
            this.methodFieldName = methodFieldName;
        }
    }

    private <T> T getProxy(ClassLoader loader, Class<?>[] interfaces) {
        try {

            Method hashCode = Object.class.getMethod("hashCode");
            Method equals = Object.class.getMethod("equals", Object.class);
            Method toString = Object.class.getMethod("toString");
            int base = 3;
            List<MethodInfo> methodInfos = new ArrayList<>();
            for (Class<?> _interface : interfaces) {
                Method[] ms = _interface.getMethods();
                for (Method m : ms) {
                    //TODO 判断default方法 判断重复方法
                    methodInfos.add(new MethodInfo(_interface, m, "m" + base));
                    base++;
                }
            }

            ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_FRAMES);

            String classname = genClassname();
            String classInter = classname.replace(".", "/");
            String superClassname = ToyProxy.class.getName();
            String superClassInter = Type.getInternalName(ToyProxy.class);


            //接口名字
            String[] interfacesInterNames = new String[interfaces.length];
            for (int i = 0; i < interfaces.length; i++) {
                interfacesInterNames[i] = Type.getInternalName(interfaces[i]);
            }
            classWriter.visit(V1_8, ACC_PUBLIC | ACC_SUPER, classInter, null, superClassInter, interfacesInterNames);
            classWriter.visitSource("<genByToy>", null);
            //接口方法 生成字段
            for (int i = 0; i < methodInfos.size() + 3; i++) {
                FieldVisitor fv = classWriter.visitField(ACC_PRIVATE | ACC_STATIC, "m" + i, "Ljava/lang/reflect/Method;", null, null);
                fv.visitEnd();
            }
            //静态方法
            {
                MethodVisitor methodVisitor = classWriter.visitMethod(ACC_STATIC, "<clinit>", "()V", null, null);
                methodVisitor.visitCode();

                staticAddObjectMethods(methodVisitor, classInter);

                for (MethodInfo methodInfo : methodInfos) {
                    staticAddInterfacesMethods(methodVisitor, methodInfo, classInter);
                }

                methodVisitor.visitInsn(RETURN);
                methodVisitor.visitMaxs(6, 1);
                methodVisitor.visitEnd();
            }
            //
            //构造函数
            {
                MethodVisitor constructor = classWriter.visitMethod(ACC_PUBLIC, "<init>", "(Lcom/wpp/InvocationHandler;)V", null, null);
                constructor.visitCode();
                constructor.visitVarInsn(ALOAD, 0);
                constructor.visitVarInsn(ALOAD, 1);
                constructor.visitMethodInsn(INVOKESPECIAL, "com/wpp/ToyProxy", "<init>", "(Lcom/wpp/InvocationHandler;)V", false);
                constructor.visitInsn(RETURN);
                constructor.visitMaxs(2, 2);
                constructor.visitEnd();
            }

            // m0 hashcode
            // m1 equasl
            // m2 toString
            // m...
            // 生成方法
            //object方法
            {
                MethodVisitor mv = classWriter.visitMethod(ACC_PUBLIC, "hashCode", "()I", null, null);
                mv.visitCode();
                mv.visitVarInsn(ALOAD, 0);
                mv.visitFieldInsn(GETFIELD, "com/wpp/ToyProxy", "h", "Lcom/wpp/InvocationHandler;");
                mv.visitVarInsn(ALOAD, 0);
                mv.visitFieldInsn(GETSTATIC, classInter, "m0", "Ljava/lang/reflect/Method;");
                mv.visitInsn(ACONST_NULL);
                mv.visitTypeInsn(CHECKCAST, "[Ljava/lang/Object;");
                mv.visitMethodInsn(INVOKEINTERFACE, "com/wpp/InvocationHandler", "invoke", "(Ljava/lang/Object;Ljava/lang/reflect/Method;[Ljava/lang/Object;)Ljava/lang/Object;", true);
                mv.visitTypeInsn(CHECKCAST, "java/lang/Integer");
                mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Integer", "intValue", "()I", false);
                mv.visitInsn(IRETURN);
                mv.visitMaxs(4, 1);
                mv.visitEnd();
            }
            {
                MethodVisitor mv = classWriter.visitMethod(ACC_PUBLIC, "equals", "(Ljava/lang/Object;)Z", null, null);
                mv.visitCode();
                mv.visitVarInsn(ALOAD, 0);
                mv.visitFieldInsn(GETFIELD, "com/wpp/ToyProxy", "h", "Lcom/wpp/InvocationHandler;");
                mv.visitVarInsn(ALOAD, 0);
                mv.visitFieldInsn(GETSTATIC, classInter, "m1", "Ljava/lang/reflect/Method;");
                mv.visitInsn(ICONST_1);
                mv.visitTypeInsn(ANEWARRAY, "java/lang/Object");
                mv.visitInsn(DUP);
                mv.visitInsn(ICONST_0);
                mv.visitVarInsn(ALOAD, 1);
                mv.visitInsn(AASTORE);
                mv.visitMethodInsn(INVOKEINTERFACE, "com/wpp/InvocationHandler", "invoke", "(Ljava/lang/Object;Ljava/lang/reflect/Method;[Ljava/lang/Object;)Ljava/lang/Object;", true);
                mv.visitTypeInsn(CHECKCAST, "java/lang/Boolean");
                mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Boolean", "booleanValue", "()Z", false);
                mv.visitInsn(IRETURN);
                mv.visitMaxs(7, 2);
                mv.visitEnd();
            }
            {
                MethodVisitor mv = classWriter.visitMethod(ACC_PUBLIC, "toString", "()Ljava/lang/String;", null, null);
                mv.visitCode();
                mv.visitVarInsn(ALOAD, 0);
                mv.visitFieldInsn(GETFIELD, "com/wpp/ToyProxy", "h", "Lcom/wpp/InvocationHandler;");
                mv.visitVarInsn(ALOAD, 0);
                mv.visitFieldInsn(GETSTATIC, classInter, "m2", "Ljava/lang/reflect/Method;");
                mv.visitInsn(ACONST_NULL);
                mv.visitTypeInsn(CHECKCAST, "[Ljava/lang/Object;");
                mv.visitMethodInsn(INVOKEINTERFACE, "com/wpp/InvocationHandler", "invoke", "(Ljava/lang/Object;Ljava/lang/reflect/Method;[Ljava/lang/Object;)Ljava/lang/Object;", true);
                mv.visitTypeInsn(CHECKCAST, "java/lang/String");
                mv.visitInsn(ARETURN);
                mv.visitMaxs(4, 1);
                mv.visitEnd();
            }

            //接口方法
            for (int i = 0; i < methodInfos.size(); i++) {
                MethodInfo methodInfo = methodInfos.get(i);
                Method method = methodInfo.method;
                Class owner = methodInfo.owner;
                Class<?> methodReturnType = method.getReturnType();
                Class<?>[] parameterTypes = method.getParameterTypes();
                //
                MethodVisitor methodVisitor = classWriter.visitMethod(ACC_PUBLIC, method.getName(), Type.getMethodDescriptor(method), null, null);
                methodVisitor.visitCode();
                methodVisitor.visitVarInsn(ALOAD, 0);
                methodVisitor.visitFieldInsn(GETFIELD, "com/wpp/ToyProxy", "h", "Lcom/wpp/InvocationHandler;");
                methodVisitor.visitVarInsn(ALOAD, 0);
                methodVisitor.visitFieldInsn(GETSTATIC, classInter, methodInfo.methodFieldName, "Ljava/lang/reflect/Method;");

                if (parameterTypes.length == 0) {
                    methodVisitor.visitInsn(ACONST_NULL);
                    methodVisitor.visitTypeInsn(CHECKCAST, "[Ljava/lang/Object;");
                    methodVisitor.visitMethodInsn(INVOKEINTERFACE, "com/wpp/InvocationHandler", "invoke", "(Ljava/lang/Object;Ljava/lang/reflect/Method;[Ljava/lang/Object;)Ljava/lang/Object;", true);
                } else {
                    if (parameterTypes.length <= 5) {
                        methodVisitor.visitInsn(parameterTypes.length + 3);
                    } else {
                        methodVisitor.visitIntInsn(BIPUSH, parameterTypes.length);
                    }
                    methodVisitor.visitTypeInsn(ANEWARRAY, "java/lang/Object");
                    for (int j = 0; j < parameterTypes.length; j++) {
                        methodVisitor.visitInsn(DUP);
                        if (parameterTypes.length <= 5) {
                            methodVisitor.visitInsn(j + 3);
                        } else {
                            methodVisitor.visitIntInsn(BIPUSH, j);
                        }
                        methodVisitor.visitVarInsn(ALOAD, j + 1);
                        methodVisitor.visitInsn(AASTORE);
                    }
                    methodVisitor.visitMethodInsn(INVOKEINTERFACE, "com/wpp/InvocationHandler", "invoke", "(Ljava/lang/Object;Ljava/lang/reflect/Method;[Ljava/lang/Object;)Ljava/lang/Object;", true);
                }
                handleMethodReturn(methodVisitor, methodReturnType);
                methodVisitor.visitMaxs(7, parameterTypes.length + 1);
                methodVisitor.visitEnd();
            }
            classWriter.visitEnd();
            //
            byte[] bytes = classWriter.toByteArray();
            bytes = postProcesse(bytes);
            Class<?> clazz = defineClass(loader, classname, bytes);
            Constructor<?> constructor = clazz.getConstructor(new Class[]{InvocationHandler.class});
            return (T) constructor.newInstance(this.h);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private Class<?> defineClass(ClassLoader loader, String className, byte[] bytes) {
        try {
            Method defineClass = ClassLoader.class.getDeclaredMethod("defineClass", String.class, byte[].class, int.class, int.class);
            if (!defineClass.isAccessible()) {
                defineClass.setAccessible(true);
            }
            return (Class) defineClass.invoke(loader, className, bytes, 0, bytes.length);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private byte[] postProcesse(byte[] bytes) {
        FileUtil.saveBytesToFile(bytes, "/Users/wpp/test/toy-jdk-proxy/target/$Proxy0.class");
        return bytes;
    }

    private void staticAddInterfacesMethods(MethodVisitor methodVisitor, MethodInfo methodInfo, String classDesc) {
        Method method = methodInfo.method;
        Class owner = methodInfo.owner;
        Class<?> methodReturnType = method.getReturnType();
        Class<?>[] parameterTypes = method.getParameterTypes();

        methodVisitor.visitLdcInsn(owner.getName());
        methodVisitor.visitMethodInsn(INVOKESTATIC, "java/lang/Class", "forName", "(Ljava/lang/String;)Ljava/lang/Class;", false);
        methodVisitor.visitLdcInsn(method.getName());
        if (parameterTypes.length == 0) {
            methodVisitor.visitInsn(ICONST_0);
            methodVisitor.visitTypeInsn(ANEWARRAY, "java/lang/Class");
            methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Class", "getMethod", "(Ljava/lang/String;[Ljava/lang/Class;)Ljava/lang/reflect/Method;", false);
            methodVisitor.visitFieldInsn(PUTSTATIC, classDesc, methodInfo.methodFieldName, "Ljava/lang/reflect/Method;");
        } else {
            if (parameterTypes.length <= 5) {
                methodVisitor.visitInsn(parameterTypes.length + 3);
            } else {
                methodVisitor.visitIntInsn(BIPUSH, parameterTypes.length);
            }
            methodVisitor.visitTypeInsn(ANEWARRAY, "java/lang/Class");

            for (int i = 0; i < parameterTypes.length; i++) {
                methodVisitor.visitInsn(DUP);
                methodVisitor.visitIntInsn(BIPUSH, i);
                methodVisitor.visitLdcInsn(Type.getType(parameterTypes[i]));
                methodVisitor.visitInsn(AASTORE);
            }
            methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Class", "getMethod", "(Ljava/lang/String;[Ljava/lang/Class;)Ljava/lang/reflect/Method;", false);
            methodVisitor.visitFieldInsn(PUTSTATIC, classDesc, methodInfo.methodFieldName, "Ljava/lang/reflect/Method;");
        }


    }

    private void staticAddObjectMethods(MethodVisitor methodVisitor, String classDesc) {
        methodVisitor.visitLdcInsn("java.lang.Object");
        methodVisitor.visitMethodInsn(INVOKESTATIC, "java/lang/Class", "forName", "(Ljava/lang/String;)Ljava/lang/Class;", false);
        methodVisitor.visitLdcInsn("hashCode");
        methodVisitor.visitInsn(ICONST_0);
        methodVisitor.visitTypeInsn(ANEWARRAY, "java/lang/Class");
        methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Class", "getMethod", "(Ljava/lang/String;[Ljava/lang/Class;)Ljava/lang/reflect/Method;", false);
        methodVisitor.visitFieldInsn(PUTSTATIC, classDesc, "m0", "Ljava/lang/reflect/Method;");

        methodVisitor.visitLdcInsn("java.lang.Object");
        methodVisitor.visitMethodInsn(INVOKESTATIC, "java/lang/Class", "forName", "(Ljava/lang/String;)Ljava/lang/Class;", false);
        methodVisitor.visitLdcInsn("equals");
        methodVisitor.visitInsn(ICONST_1);
        methodVisitor.visitTypeInsn(ANEWARRAY, "java/lang/Class");
        methodVisitor.visitInsn(DUP);
        methodVisitor.visitInsn(ICONST_0);
        methodVisitor.visitLdcInsn("java.lang.Object");
        methodVisitor.visitMethodInsn(INVOKESTATIC, "java/lang/Class", "forName", "(Ljava/lang/String;)Ljava/lang/Class;", false);
        methodVisitor.visitInsn(AASTORE);
        methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Class", "getMethod", "(Ljava/lang/String;[Ljava/lang/Class;)Ljava/lang/reflect/Method;", false);
        methodVisitor.visitFieldInsn(PUTSTATIC, classDesc, "m1", "Ljava/lang/reflect/Method;");

        methodVisitor.visitLdcInsn("java.lang.Object");
        methodVisitor.visitMethodInsn(INVOKESTATIC, "java/lang/Class", "forName", "(Ljava/lang/String;)Ljava/lang/Class;", false);
        methodVisitor.visitLdcInsn("toString");
        methodVisitor.visitInsn(ICONST_0);
        methodVisitor.visitTypeInsn(ANEWARRAY, "java/lang/Class");
        methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Class", "getMethod", "(Ljava/lang/String;[Ljava/lang/Class;)Ljava/lang/reflect/Method;", false);
        methodVisitor.visitFieldInsn(PUTSTATIC, classDesc, "m2", "Ljava/lang/reflect/Method;");
    }

    private String genClassname() {
        return getPackageName() + ".$TOY$" + counter.getAndIncrement();
    }

    private String getPackageName() {
        return "com.wpp";
    }

    private final static AtomicInteger counter = new AtomicInteger(1);

    private boolean isVoid(Class<?> methodReturnType) {
        return methodReturnType.isAssignableFrom(Void.class) || methodReturnType.isAssignableFrom(void.class);
    }

    private void handleMethodReturn(MethodVisitor mv, Class methodReturnType) {
        if (isVoid(methodReturnType)) {
            mv.visitInsn(RETURN);
        } else {
            mv.visitTypeInsn(CHECKCAST, Type.getInternalName(methodReturnType)); //类型转换
            mv.visitInsn(ARETURN);
        }
    }

}
