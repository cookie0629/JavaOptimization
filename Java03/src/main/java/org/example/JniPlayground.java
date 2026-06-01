package org.example;

public class JniPlayground {

    // 加载动态库 (Windows下是 jnidemo.dll, Linux/Mac下是 libjnidemo.so)
    static {
        System.loadLibrary("jnidemo");
    }

    // --- Native 方法定义 ---

    // 1. 立即崩溃的方法
    public native void crashNow();

    // 2. 分配 1KB 内存 (为了在 Runtime 中可见，我们在 C 中创建 Java 对象)
    public native void allocate1KbMemory();

    // 3. 递归/深层调用后崩溃
    public native void deepStackCrash();

    // 4. 获取字符串长度
    public native int getStringLength(String s);

    // 5. 调用对象的方法
    public native void callBeanMethod(TestBean bean);

    // 6. 修改对象的字段
    public native void modifyBeanField(TestBean bean);

    // 7. 结构体操作：分配并返回指针 (使用 long 保存地址)
    public native long allocNativeStruct();

    // 8. 结构体操作：读取指针指向的数据
    public native int readNativeStruct(long ptr);

    // 9. 结构体操作：释放内存
    public native void freeNativeStruct(long ptr);


    public static void main(String[] args) {
        JniPlayground app = new JniPlayground();

        System.out.println("=== JNI 测试开始 ===");

        // --- 基础操作测试 ---
        String testStr = "Hello JNI";
        System.out.println("字符串 '" + testStr + "' 长度: " + app.getStringLength(testStr));

        TestBean bean = new TestBean();

        // 调用方法
        System.out.println("\n--- 测试 C 调用 Java 方法 ---");
        app.callBeanMethod(bean);

        // 修改字段
        System.out.println("\n--- 测试 C 修改 Java 字段 ---");
        System.out.println("修改前: " + bean.intValue);
        app.modifyBeanField(bean);
        System.out.println("修改后: " + bean.intValue);

        // --- 内存与 Runtime 测试 ---
        System.out.println("\n--- 测试 1KB 内存分配 ---");
        printMemory("分配前");
        app.allocate1KbMemory();
        printMemory("分配后");
        // 注意：GC 可能会迅速回收，或者 TLAB 缓冲导致变化不直观，但原理是 C 层请求了 Java 堆内存

        // --- 结构体指针测试 ---
        System.out.println("\n--- 测试 Native 结构体指针管理 ---");
        long structPtr = app.allocNativeStruct();
        System.out.println("结构体指针地址: 0x" + Long.toHexString(structPtr));

        int value = app.readNativeStruct(structPtr);
        System.out.println("从结构体读取的值: " + value);

        app.freeNativeStruct(structPtr);
        System.out.println("结构体内存已释放");

        // --- 崩溃测试 (取消注释以运行) ---
        // 警告：这将导致 JVM 退出

//         System.out.println("\n--- 准备进行深层堆栈崩溃测试 ---");
//         app.deepStackCrash();
//
         System.out.println("\n--- 准备进行直接崩溃测试 ---");
         app.crashNow();
    }

    private static void printMemory(String label) {
        Runtime rt = Runtime.getRuntime();
        long total = rt.totalMemory();
        long free = rt.freeMemory();
        long used = total - free;
        System.out.printf("[%s] Used Memory: %d bytes%n", label, used);
    }
}

//javac -h . JniPlayground.java TestBean.java

//java JniPlayground.java
//java -cp ..\..\ org.example.JniPlayground