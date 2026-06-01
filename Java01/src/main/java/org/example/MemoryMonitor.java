package org.example;

import java.util.ArrayList;
import java.util.List;

// 1. 定义包含数组和字符串的对象
class ComplexObject {
    private String name;
    private int[] data;

    public ComplexObject(int index) {
        // String 占用内存
        this.name = "StringObject-" + index;
        // Array 占用内存 (分配 1024 个整数，约 4KB)
        this.data = new int[1024];
    }
}

public class MemoryMonitor {
    public static void main(String[] args) throws InterruptedException {
        // 用于持有对象引用，防止被立即 GC (模拟内存泄漏或高负载)
        List<ComplexObject> objectList = new ArrayList<>();

        Runtime runtime = Runtime.getRuntime();
        long counter = 0;

        System.out.println("Time(ms),Max(MB),Total(MB),Free(MB),Used(MB)");

        long startTime = System.currentTimeMillis();

        while (true) {
            // 2. 分配对象
            // 每次循环分配 100 个对象，避免输出过快
            for (int i = 0; i < 100; i++) {
                objectList.add(new ComplexObject((int) counter++));
            }

            //Thread.sleep(10);

            // 每隔一定次数由于 List 扩容或内存压力，可能会触发 GC
            // 为了防止 OOM 导致程序过快崩溃，我们在达到一定数量后清理一半数据（模拟 GC 后的波谷）
            if (objectList.size() % 1000 == 0) {
//                objectList.subList(0, 25000).clear();
//                // 建议显式调用 GC 以便在图表中观察明显的下降（生产环境不建议）
                System.gc();
            }

            // 3. 获取内存指标
            long maxMemory = runtime.maxMemory();
            long totalMemory = runtime.totalMemory();
            long freeMemory = runtime.freeMemory();
            // 4. 计算已占用内存
            long usedMemory = totalMemory - freeMemory;

            // 转换为 MB 输出
            int toMB = 1;
            System.out.printf("%d,%d,%d,%d,%d,%n",
                    (System.currentTimeMillis() - startTime),
                    maxMemory / toMB,
                    totalMemory / toMB,
                    freeMemory / toMB,
                    usedMemory / toMB
            );
        }
    }
}

// java -Xmx256m  org.example.MemoryMonitor > data.csv