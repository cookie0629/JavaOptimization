//gcc -shared -I"$env:JAVA_HOME\include" -I"$env:JAVA_HOME\include\win32" -O0 matrix_jni.c -o matrix_jni.dll
//gcc -shared -I"$env:JAVA_HOME\include" -I"$env:JAVA_HOME\include\win32" -O2 matrix_jni.c -o matrix_jni.dll
//gcc -shared -I"$env:JAVA_HOME\include" -I"$env:JAVA_HOME\include\win32" -O3 -march=native matrix_jni.c -o matrix_jni.dll
#include <jni.h>
#include "org_example_MatrixBenchmark.h"

JNIEXPORT void JNICALL Java_org_example_MatrixBenchmark_multiplyC
  (JNIEnv *env, jobject obj, jdoubleArray matA, jdoubleArray matB, jdoubleArray matC, jint size) {

    // 获取指向 Java 数组的 C 指针
    jdouble *A = (*env)->GetDoubleArrayElements(env, matA, NULL);
    jdouble *B = (*env)->GetDoubleArrayElements(env, matB, NULL);
    jdouble *C = (*env)->GetDoubleArrayElements(env, matC, NULL);

    // 标准矩阵乘法 O(n^3)
    for (int i = 0; i < size; i++) {
        for (int j = 0; j < size; j++) {
            double sum = 0;
            for (int k = 0; k < size; k++) {
                sum += A[i * size + k] * B[k * size + j];
            }
            C[i * size + j] = sum;
        }
    }

    // 释放数组。JNI_ABORT 表示 A 和 B 只是读取，无需将更改复制回 Java；
    // C 数组需要将结果写回，所以传递 0。
    (*env)->ReleaseDoubleArrayElements(env, matA, A, JNI_ABORT);
    (*env)->ReleaseDoubleArrayElements(env, matB, B, JNI_ABORT);
    (*env)->ReleaseDoubleArrayElements(env, matC, C, 0);
}