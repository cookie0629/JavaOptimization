package org.example.app;

import java.lang.management.ManagementFactory;
import java.util.Scanner;

public class MainApp {
    public static void main(String[] args) {
        // 打印当前进程的 PID，供动态 Attach 使用
        String name = ManagementFactory.getRuntimeMXBean().getName();
        System.out.println("====== Action ======");
        System.out.println("Current process PID: " + name.split("@")[0]);

        TargetService service = new TargetService();
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("Press enter to execute the doTask method, or enter 'exit' to exit...");
            if (!scanner.hasNextLine()) {
                System.out.println("检测到输入流结束，程序退出。");
                break;
            }
            String line = scanner.nextLine();
            if ("exit".equalsIgnoreCase(line)) break;

            try {
                String result = service.doTask("HelloAgent");
                System.out.println("Final result: " + result);
            } catch (Exception e) {
                System.err.println("The mission was a complete failure: " + e.getMessage());
            }
        }
    }
}


//gradle clean build
//java -javaagent:build/libs/agent-demo-1.0-SNAPSHOT.jar -cp build/libs/agent-demo-1.0-SNAPSHOT.jar org.example.app.MainApp

// java -cp build/libs/agent-demo-1.0-SNAPSHOT.jar org.example.app.MainApp
// java -cp build/libs/agent-demo-1.0-SNAPSHOT.jar org.example.app.Attacher 25140 build/libs/agent-demo-1.0-SNAPSHOT.jar