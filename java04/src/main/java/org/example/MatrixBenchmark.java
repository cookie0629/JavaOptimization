package org.example;

import java.util.Random;

public class MatrixBenchmark {
    // 加载 C 语言编译的动态链接库
    static {
        System.loadLibrary("matrix_jni");
    }

    // 声明 JNI 原生方法
    public native void multiplyC(double[] A, double[] B, double[] C, int size);

    // 纯 Java 实现的矩阵乘法
    public void multiplyJava(double[] A, double[] B, double[] C, int size) {
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                double sum = 0;
                for (int k = 0; k < size; k++) {
                    sum += A[i * size + k] * B[k * size + j];
                }
                C[i * size + j] = sum;
            }
        }
    }

    // 辅助方法：填充随机数
    private static void fillRandom(double[] matrix) {
        Random rand = new Random();
        for (int i = 0; i < matrix.length; i++) {
            matrix[i] = rand.nextDouble();
        }
    }

    public static void main(String[] args) {
        MatrixBenchmark benchmark = new MatrixBenchmark();
        int[] sizes = {128, 1024}; // 要求的测试尺寸

        for (int size : sizes) {
            System.out.println("========== 测试矩阵大小: " + size + "x" + size + " ==========");
            double[] A = new double[size * size];
            double[] B = new double[size * size];
            double[] C_java = new double[size * size];
            double[] C_c = new double[size * size];

            fillRandom(A);
            fillRandom(B);

            // 预热 (JVM JIT 编译优化需要预热才能测得准确时间)
            benchmark.multiplyJava(A, B, C_java, 128);

            // 1. 测试 Java 性能
            long startJava = System.nanoTime();
            benchmark.multiplyJava(A, B, C_java, size);
            long endJava = System.nanoTime();
            System.out.printf("Java 耗时: %.2f 毫秒\n", (endJava - startJava) / 1_000_000.0);

            // 2. 测试 C (JNI) 性能
            long startC = System.nanoTime();
            benchmark.multiplyC(A, B, C_c, size);
            long endC = System.nanoTime();
            System.out.printf("C (JNI) 耗时: %.2f 毫秒\n", (endC - startC) / 1_000_000.0);
            System.out.println();
        }
    }
}


//javac -h . MatrixBenchmark.java
//java -cp ..\.. "-Djava.library.path=." org.example.MatrixBenchmark