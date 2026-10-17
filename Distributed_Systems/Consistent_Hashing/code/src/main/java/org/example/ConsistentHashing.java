package org.example;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

class ConsistentHashing {
    private final TreeMap<Integer, String> ring = new TreeMap<>();
    private final int HASH_SPACE = 100; // 0–100 like our example

    // Hash function (simple mod for demo, use MD5 in production)
    private int hash(String key) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] digest = md.digest(key.getBytes());
            // Convert first 4 bytes to an int
            int hash = ((digest[0] & 0xFF) << 24) |
                    ((digest[1] & 0xFF) << 16) |
                    ((digest[2] & 0xFF) << 8)  |
                    (digest[3] & 0xFF);
            return Math.abs(hash) % HASH_SPACE; // keep within ring
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    // Add a server to the ring
    public void addServer(String server) {
        int position = hash(server);
        ring.put(position, server);
        System.out.println("Server " + server + " added at position " + position);
    }

    // Remove a server
    public void removeServer(String server) {
        int position = hash(server);
        ring.remove(position);
        System.out.println("Server " + server + " removed from position " + position);
    }

    // Find the server for a given key
    public String getServer(String key) {
        int keyHash = hash(key);
        Map.Entry<Integer, String> entry = ring.ceilingEntry(keyHash);

        if (entry == null) {
            entry = ring.firstEntry(); // wrap around
        }

        return entry.getValue();
    }

    // Show key mappings
    public void printKeyMappings(List<String> keys) {
        for (String key : keys) {
            System.out.println(key + " → " + getServer(key));
        }
    }
}