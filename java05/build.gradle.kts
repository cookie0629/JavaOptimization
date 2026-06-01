import proguard.gradle.ProGuardTask

// 1. buildscript 必须放在最前面（用于引入 ProGuard 插件本身）
buildscript {
    repositories {
        mavenCentral()
    }
    dependencies {
        classpath("com.guardsquare:proguard-gradle:7.3.2")
    }
}

// 2. 全局只能有一个 plugins 块，紧跟在 buildscript 后面
plugins {
    java
    // 如果你创建项目时还有自带的插件（比如 kotlin("jvm") 等），请在这里保留它们
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    // 你的业务代码依赖写在这里（目前为空）
}

// 3. 配置普通的 Jar 任务，指定 Main-Class
tasks.withType<Jar> {
    manifest {
        attributes["Main-Class"] = "org.example.BytecodeTest"
    }
}

// 4. 定义 ProGuard 任务 (Kotlin DSL 写法)
val proguardTask = tasks.register<ProGuardTask>("proguard") {
    // 依赖于普通的 jar 打包任务
    dependsOn(tasks.jar)

    // 注入 Java 核心类库 (Java 9+ 使用 jmods，如果是 Java 8 请换成 "$javaHome/lib/rt.jar")
    val javaHome = System.getProperty("java.home")
    libraryjars("$javaHome/jmods/java.base.jmod")

    // 输入：普通的 jar 包
    injars(tasks.jar.flatMap { it.archiveFile })

    // 输出：混淆后的 jar 包
    outjars(layout.buildDirectory.file("libs/${project.name}-${version}-obfuscated.jar"))

    // 指定 ProGuard 规则文件
    configuration("proguard-rules.pro")
}

// 5. 将 proguard 挂载到 build 任务上，确保 ./gradlew build 时自动执行
tasks.build {
    dependsOn(proguardTask)
}