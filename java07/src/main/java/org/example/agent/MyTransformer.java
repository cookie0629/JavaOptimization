package org.example.agent;

import javassist.*;
import java.lang.instrument.ClassFileTransformer;
import java.security.ProtectionDomain;

public class MyTransformer implements ClassFileTransformer {

    @Override
    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined,
                            ProtectionDomain protectionDomain, byte[] classfileBuffer) {

        if (className == null || !className.startsWith("org/example/app")) {
            return null;
        }

        try {
            ClassPool pool = new ClassPool(true);
            if (loader != null) {
                pool.appendClassPath(new LoaderClassPath(loader));
            }

            CtClass ctClass = pool.makeClass(new java.io.ByteArrayInputStream(classfileBuffer));
            boolean modified = false;

            for (CtMethod method : ctClass.getDeclaredMethods()) {
                if (method.hasAnnotation("org.example.agent.RetryAndLog")) {
                    String methodName = method.getName();

                    // 1. 注入一个局部变量用于记录开始时间
                    method.addLocalVariable("agentStartTime", CtClass.longType);

                    // 2. 在方法开头插入逻辑
                    method.insertBefore(
                            "agentStartTime = System.currentTimeMillis();\n" +
                                    "System.out.println(\"[Agent] => Dynamic interception: " + methodName + ", parameter: \" + java.util.Arrays.toString($args));"
                    );

                    // 3. 在方法正常返回前插入逻辑 (如果抛出异常则不会走到这里)
                    method.insertAfter(
                            "long cost = System.currentTimeMillis() - agentStartTime;\n" +
                                    "System.out.println(\"[Agent] <= The method ended successfully: " + methodName + ", Time-consuming: \" + cost + \"ms\\n\");"
                    );

                    // 4. 捕获异常并抛出 (代替原来的重试机制)
                    CtClass exceptionType = pool.get("java.lang.Exception");
                    method.addCatch(
                            "{ System.err.println(\"[Agent] An exception occurred in the method and was caught: \" + $e.getMessage()); throw $e; }",
                            exceptionType
                    );

                    modified = true;
                }
            }

            if (modified) {
                return ctClass.toBytecode();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}