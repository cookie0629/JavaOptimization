package org.example;

import java.util.ArrayList;
import java.util.List;

// 1. 单例类
class MySingleton {
    private static final MySingleton INSTANCE = new MySingleton();

    private MySingleton() {
        System.out.println("Singleton 初始化完成");
    }

    public static MySingleton getInstance() {
        return INSTANCE;
    }
}

// 2. Bean 类：包含各种字段
class ComplexBean {
    String name;
    byte[] data;
    ArrayList<String> list;
    int primitiveInt;
    double primitiveDouble;
    ComplexBean selfRef; // 指向自己或用于构建环

    public ComplexBean(String name) {
        this.name = name;
        this.data = new byte[1024]; // 占用 1KB
        this.list = new ArrayList<>();
        this.primitiveInt = 42;
        this.primitiveDouble = 3.14159;
    }
}

public class MemoryDemo {

    public static void main(String[] args) {
        // 检查命令行参数，决定是否启动“有害”线程
        boolean runOOM = args.length > 0 && args[0].equals("oom");

        System.out.println("程序启动 PID: " + ProcessHandle.current().pid());

        // 3. 构建 Bean 环 (主线程中演示)
        ComplexBean b1 = new ComplexBean("Bean-1");
        ComplexBean b2 = new ComplexBean("Bean-2");
        ComplexBean b3 = new ComplexBean("Bean-3");

        // 闭环引用：b1 -> b2 -> b3 -> b1
        b1.selfRef = b2;
        b2.selfRef = b3;
        b3.selfRef = b1;

        // 4. 启动多个休眠线程
        for (int i = 0; i < 5; i++) {
            new Thread(new WorkerTask(), "Sleeping-Worker-" + i).start();
        }

        // 5. 有害线程逻辑
        if (runOOM) {
            System.out.println("警告：启动有害线程，即将发生 OOM...");
            new Thread(new OOMTask(), "Bad-Thread").start();
        } else {
            System.out.println("正常模式运行中。请使用 jmap/jstack/jconsole 连接...");
            // 保持主线程存活，防止程序退出
            try {
                Thread.sleep(Long.MAX_VALUE);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    // 正常工作线程任务
    static class WorkerTask implements Runnable {
        @Override
        public void run() {
            // 每个线程持有单例引用
            MySingleton singleton = MySingleton.getInstance();
            // 每个线程创建自己的 Bean
            ComplexBean myBean = new ComplexBean(Thread.currentThread().getName() + "-Bean");
            // 引用自己
            myBean.selfRef = myBean;

            try {
                // 无限休眠
                Thread.sleep(Long.MAX_VALUE);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            // 防止变量被 JIT 优化掉
            System.out.println(singleton + " " + myBean);
        }
    }

    // 有害线程任务：制造 OOM
    static class OOMTask implements Runnable {
        @Override
        public void run() {
            List<byte[]> garbage = new ArrayList<>();
            while (true) {
                // 每次分配 5MB
                garbage.add(new byte[5 * 1024 * 1024]);
                try {
                    Thread.sleep(50); // 稍微给一点时间让工具能连上
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}

//javac MemoryDemo.java
//java MemoryDemo.java
//jmap -dump:live,format=b,file=heap.bin ID
//jstack ID > threads.txt

//jcmd 12345 help 列出当前这个 Java 进程（PID 12345）所支持的所有 jcmd 诊断命令列表
//jcmd 12345 VM.version 打印目标 Java 进程所运行的 JVM 的详细版本信息
//jcmd 12345 GC.heap_info 打印当前堆内存的详细分配和使用信息
//        Thread.print：等同于 jstack。
//        GC.heap_dump：等同于 jmap dump。
//        VM.flags：查看 JVM 启动参数。
//        PerfCounter.print：查看性能计数器。
//jconsole
//java -Xmx256m -XX:+HeapDumpOnOutOfMemoryError MemoryDemo oom