//gcc -g -shared -I"$env:JAVA_HOME\include" -I"$env:JAVA_HOME\include\win32" native_lib.c -o jnidemo.dll

#include <jni.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include "org_example_JniPlayground.h"

// 定义一个简单的 Native 结构体
typedef struct {
    int id;
    int data;
} MyNativeStruct;

// 1. 崩溃方法：空指针解引用
JNIEXPORT void JNICALL Java_org_example_JniPlayground_crashNow(JNIEnv *env, jobject obj) {
    printf("[C] 准备执行非法内存访问...\n");
    int *ptr = NULL;
    *ptr = 10; // 这里会触发 Segmentation Fault (Linux) 或 Access Violation (Windows)
}

// 2. 分配 1KB 内存并使其在 Java Runtime 可见
// 说明：如果在 C 中使用 malloc，这是 Native Heap，Java Runtime.totalMemory 是看不到的。
// 要让 Java Runtime 看到，我们需要通过 JNI 在 Java Heap 上分配对象（如 byte 数组）。
JNIEXPORT void JNICALL Java_org_example_JniPlayground_allocate1KbMemory(JNIEnv *env, jobject obj) {
    int size = 1024; // 1KB
    // NewByteArray 在 Java 堆上分配内存
    jbyteArray arr = (*env)->NewByteArray(env, size);

    // 为了防止被立即 GC (如果是在局部引用帧中)，在实际场景中可能需要 GlobalRef。
    // 但为了演示 "alloc"，这样已经发生了堆内存申请。

    // 如果我们想演示 malloc (C Heap)，那是看不见的：
    // void* c_mem = malloc(1024); // Java Runtime 监控不到这个

    printf("[C] 已在 Java 堆上分配了 1KB 的 byte 数组。\n");
}

// 辅助函数，用于深层调用
void recursiveFunction(int depth) {
    if (depth == 0) {
        printf("[C] 递归到达底部，准备崩溃...\n");
        int *ptr = (int*)0x1234; // 随机的非法地址
        *ptr = 999; // Crash
        return;
    }
    // 可以在这里打印日志方便追踪
    // printf("[C] Stack depth: %d\n", depth);
    recursiveFunction(depth - 1);
}

// 3. 深层调用崩溃
JNIEXPORT void JNICALL Java_org_example_JniPlayground_deepStackCrash(JNIEnv *env, jobject obj) {
    printf("[C] 开始深层调用链...\n");
    recursiveFunction(10); // 调用 10 层后崩溃
}

// 4. 获取字符串长度
JNIEXPORT jint JNICALL Java_org_example_JniPlayground_getStringLength(JNIEnv *env, jobject obj, jstring str) {
    if (str == NULL) return 0;
    return (*env)->GetStringLength(env, str);
}

// 5. 调用对象方法
JNIEXPORT void JNICALL Java_org_example_JniPlayground_callBeanMethod(JNIEnv *env, jobject obj, jobject bean) {
    // 1. 获取类 Class
    jclass beanClass = (*env)->GetObjectClass(env, bean);

    // 2. 获取方法 ID (名称: "javaMethod", 签名: "()V")
    // 签名可以通过 `javap -s TestBean` 查看
    jmethodID mid = (*env)->GetMethodID(env, beanClass, "javaMethod", "()V");

    if (mid == NULL) return; // 方法未找到

    // 3. 调用方法
    printf("[C] Calling Java method...\n");
    (*env)->CallVoidMethod(env, bean, mid);
}

// 6. 修改 Java 字段
JNIEXPORT void JNICALL Java_org_example_JniPlayground_modifyBeanField(JNIEnv *env, jobject obj, jobject bean) {
    jclass beanClass = (*env)->GetObjectClass(env, bean);

    // 获取 intValue 字段 ID
    jfieldID fid = (*env)->GetFieldID(env, beanClass, "intValue", "I");

    if (fid == NULL) return;

    // 修改值为 999
    printf("[C] Setting intValue to 999...\n");
    (*env)->SetIntField(env, bean, fid, 999);
}

// 7. 结构体操作：分配
JNIEXPORT jlong JNICALL Java_org_example_JniPlayground_allocNativeStruct(JNIEnv *env, jobject obj) {
    MyNativeStruct *ptr = (MyNativeStruct*)malloc(sizeof(MyNativeStruct));
    ptr->id = 1;
    ptr->data = 123456;
    printf("[C] Allocated struct at %p\n", ptr);
    return (jlong)ptr; // 将指针转换为 Java long 返回
}

// 8. 结构体操作：读取
JNIEXPORT jint JNICALL Java_org_example_JniPlayground_readNativeStruct(JNIEnv *env, jobject obj, jlong ptrAddr) {
    MyNativeStruct *ptr = (MyNativeStruct*)ptrAddr;
    printf("[C] Reading struct from %p\n", ptr);
    return ptr->data;
}

// 9. 必须提供释放入口，防止 Native 内存泄漏
JNIEXPORT void JNICALL Java_org_example_JniPlayground_freeNativeStruct(JNIEnv *env, jobject obj, jlong ptrAddr) {
    MyNativeStruct *ptr = (MyNativeStruct*)ptrAddr;
    printf("[C] Freeing struct at %p\n", ptr);
    free(ptr);
}