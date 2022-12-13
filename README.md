# Toy-JDK-Proxy

使用asm来模拟实现jdk的动态代理，不可用于生产环境

- 仅模拟实现了字节码生成代理类
- 没有处理异常
- 没有代码生成优化
- 没有类数据缓存，性能较低

# 示例
```java
    ToyProxy.newProxyInstance(ToyProxyMain.class.getClassLoader(), new Class[]{IA.class}, new InvocationHandler() {
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
```