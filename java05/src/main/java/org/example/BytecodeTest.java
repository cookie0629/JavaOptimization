package org.example;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

// 辅助类：用于演示方法调用和字段修改
class TargetObject {
    public int targetField = 0;
    public String getMessage() {
        return "Hello from TargetObject";
    }
}

// 任务 1：包含三个特定方法的类
class Operations {
    // 1. 获取字符串长度
    public int getStringLength(String s) {
        return s.length();
    }

    // 2. 调用对象方法并返回值
    public String callMethod(TargetObject obj) {
        return obj.getMessage();
    }

    // 3. 修改对象的 Java 字段
    public void changeField(TargetObject obj) {
        obj.targetField = 42;
    }
}

// 任务 2：包含 value 字段的类
class Item {
    public int value;

    public Item(int value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }
}

// 主类：包含手动排序和 main 方法
public class BytecodeTest {

    // 任务 2：手动对 Item 对象按 value 升序排序 (使用冒泡排序)
    public static void manualSort(List<Item> list) {
        int n = list.size();
        for (int i = 0; i < n - 1; i++) {
            for (int j = 0; j < n - i - 1; j++) {
                // 如果前一个对象的 value 大于后一个对象的 value，则交换
                if (list.get(j).value > list.get(j + 1).value) {
                    Item temp = list.get(j);
                    list.set(j, list.get(j + 1));
                    list.set(j + 1, temp);
                }
            }
        }
    }

    public static void main(String[] args) {
        List<Item> list = new ArrayList<>();
        Random rand = new Random();

        // 填充随机对象
        for (int i = 0; i < 5; i++) {
            list.add(new Item(rand.nextInt(100)));
        }

        System.out.println("排序前: " + list);
        manualSort(list);
        System.out.println("排序后: " + list);
    }
}

//javac BytecodeTest.java
//javap -cp ../.. -c -v org.example.BytecodeTest > BytecodeTest_bytecode.txt
//javap -cp ../.. -c -v org.example.Operations > Operations_bytecode.txt
//java -cp ../..  org.example.BytecodeTest

class EmptyClass1 {
    public void methodA1() {}
    public void methodA2() {}
    public void methodA3() {}
    public void methodA4() {}
    public void methodA5() {}
}
class EmptyClass2 {
    public void methodB1() {}
    public void methodB2() {}
    public void methodB3() {}
    public void methodB4() {}
    public void methodB5() {}
}
class EmptyClass3 {
    public void methodC1() {}
    public void methodC2() {}
    public void methodC3() {}
    public void methodC4() {}
    public void methodC5() {}
}
class EmptyClass4 {
    public void methodD1() {}
    public void methodD2() {}
    public void methodD3() {}
    public void methodD4() {}
    public void methodD5() {}
}
class EmptyClass5 {
    public void methodE1() {}
    public void methodE2() {}
    public void methodE3() {}
    public void methodE4() {}
    public void methodE5() {}
}

//./gradlew clean build
// mkdir obfuscated_out
// cd obfuscated_out
// jar xf ..\build\libs\*-obfuscated.jar
// cd ..
// javap -c obfuscated_out/org/example/EmptyClass1.class
//rm  obfuscated_out/
//javap -c obfuscated_out/org/example/Operations.class