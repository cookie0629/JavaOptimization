package org.example.app;

import org.example.agent.RetryAndLog;

public class TargetService {
    private int count = 0;

    @RetryAndLog
    public String doTask(String input) throws Exception {
        count++;
        // 模拟前两次调用抛出异常，第三次成功
        if (count % 3 != 0) {
            // 稍作延迟以展示执行时间
            Thread.sleep(100);
            throw new RuntimeException("The network request has timed out!");
        }
        Thread.sleep(200);
        return "Processed successfully: " + input;
    }
}