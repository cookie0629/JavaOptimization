package org.example.app;

import com.sun.tools.attach.VirtualMachine;

public class Attacher {
    public static void main(String[] args) throws Exception {
        if (args.length < 2) {
            System.out.println("用法: java org.example.app.Attacher <PID> <Agent-Jar-Path>");
            return;
        }
        String pid = args[0];
        String agentJar = args[1];

        System.out.println("Try to attach to PID: " + pid + "...");
        VirtualMachine vm = VirtualMachine.attach(pid);
        vm.loadAgent(agentJar);
        vm.detach();
        System.out.println("Agent Successfully attached!");
    }
}