package org.example;

public class TestBean {
    // 基本类型
    public int intValue;
    public double doubleValue;

    // 字符串
    public String stringValue;

    // 引用类型 (递归引用)
    public TestBean innerBean;

    // 数组
    public int[] primitiveArray;
    public Object[] objectArray;

    public TestBean() {
        this.intValue = 100;
        this.stringValue = "Initial Value";
    }

    // 供 Native 代码调用的方法
    public void javaMethod() {
        System.out.println("[Java] TestBean.javaMethod() 被 C 代码调用了！");
        System.out.println("[Java] 当前 intValue = " + this.intValue);
    }
}