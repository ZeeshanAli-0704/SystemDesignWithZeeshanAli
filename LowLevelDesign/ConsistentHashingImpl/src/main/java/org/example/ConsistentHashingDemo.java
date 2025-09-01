package org.example;

import java.util.Arrays;
import java.util.List;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class ConsistentHashingDemo {
    public static void main(String[] args) {
        ConsistentHashing ch = new ConsistentHashing();

        // Step 1: Add servers
        ch.addServer("S1");
        ch.addServer("S2");
        ch.addServer("S3");
        ch.addServer("S4");

        List<String> keys = Arrays.asList("K1", "K2", "K3", "K4", "K5", "K6");
        System.out.println("\nInitial key mappings:");
        ch.printKeyMappings(keys);

        // Step 2: Remove a server
        ch.removeServer("S3");
        System.out.println("\nAfter removing S3:");
        ch.printKeyMappings(keys);

        // Step 3: Add a new server
        ch.addServer("S5");
        System.out.println("\nAfter adding S5:");
        ch.printKeyMappings(keys);
    }
}