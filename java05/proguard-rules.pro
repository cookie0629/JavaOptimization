# 场景 1：什么都不发生 (如果想测试这个，取消下面三行的注释)
# -dontshrink
# -dontoptimize
# -dontobfuscate

# 场景 2：做所有能做的事（极致裁剪与混淆），并保留特定目标
# 基础必须项：保留程序入口，否则连 main 方法都会被裁掉
-keepclasseswithmembers public class * {
    public static void main(java.lang.String[]);
}

# 要求 A：不碰某些类 (例如保留 EmptyClass1 及其所有内容不被裁剪、不被改名)
-keep class org.example.EmptyClass1 {
    *;
}

# 要求 B：不碰某些方法 (按名称)
# 例如保留所有叫 methodB1 的方法。因为 EmptyClass2 里的 methodB1 被保留，EmptyClass2 也会免于被彻底裁剪。
-keepclassmembers class * {
    void methodB1();
}

# 要求 C：不碰某些方法 (按签名)
# 例如保留 Operations 类中，接收 TargetObject 返回 String 的 callMethod 方法
#-keepclassmembers class org.example.Operations {
#    java.lang.String callMethod(org.example.TargetObject);
#}

# 将原来的 -keepclassmembers 改为 -keep
-keep class org.example.Operations {
    java.lang.String callMethod(org.example.TargetObject);
}