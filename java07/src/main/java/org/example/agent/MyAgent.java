package org.example.agent;

import java.lang.instrument.Instrumentation;

public class MyAgent {

    // JVM 启动时加载 (java -javaagent)
    public static void premain(String agentArgs, Instrumentation inst) {
        System.out.println("[Agent] premain Activated, registered Transformer。");
        inst.addTransformer(new MyTransformer(), true);
    }

    // JVM 运行中附加 (Dynamic Attach)
    public static void agentmain(String agentArgs, Instrumentation inst) {
        System.out.println("[Agent] agentmain Started and has been dynamically attached.");
        inst.addTransformer(new MyTransformer(), true);

        try {
            // 对已经加载到内存中的目标类进行重新转换
            for (Class<?> clazz : inst.getAllLoadedClasses()) {
                if (clazz.getName().startsWith("org.example.app.TargetService")) {
                    System.out.println("[Agent] Re-convert the loaded class: " + clazz.getName());
                    inst.retransformClasses(clazz);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}