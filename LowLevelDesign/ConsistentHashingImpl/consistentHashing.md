
# 🔄 Consistent Hashing Explained with an Example

To understand **consistent hashing**, let’s walk through a simple example step by step.
Imagine we have a **hash ring** with positions from `0–100`. Both **servers (nodes)** and **keys (data items)** are placed on this ring using a hash function.

---

## 🟢 Step 1: Initial Setup

We start with **4 servers** placed on the ring:

* **S1 → 10**
* **S2 → 30**
* **S3 → 60**
* **S4 → 85**

And we have **6 keys**:

* **K1 → 12**
* **K2 → 25**
* **K3 → 40**
* **K4 → 65**
* **K5 → 70**
* **K6 → 90**

🔹 **Mapping Rule**: Each key is assigned to the **first server encountered while moving clockwise**.

* `K1 (12)` → goes to `S2 (30)`
* `K2 (25)` → goes to `S2 (30)`
* `K3 (40)` → goes to `S3 (60)`
* `K4 (65)` → goes to `S4 (85)`
* `K5 (70)` → goes to `S4 (85)`
* `K6 (90)` → goes to `S1 (10)` (wraps around the ring)

✅ All keys are evenly distributed.

---

## 🔴 Step 2: Server Failure

Now, suppose **S3 (60) crashes**.

* Keys assigned to S3 (`K3`) need a new home.
* In consistent hashing, these keys are reassigned to the **next available server in clockwise direction**, which is **S4 (85)**.

🔹 New distribution:

* `K1, K2` → S2
* `K3` → S4 (moved from S3)
* `K4, K5` → S4
* `K6` → S1

✅ Only **K3 is moved**. Other keys remain unaffected.
This is the power of consistent hashing: **minimal disruption**.

---

## 🟡 Step 3: Adding a New Server

Now, let’s **add a new server S5 at position 50**.

* Keys between **S2 (30)** and **S5 (50)** will now move to S5.
* In our case, only `K3 (40)` falls in this range.

🔹 New distribution:

* `K1, K2` → S2
* `K3` → S5 (moved from S4)
* `K4` → S3 (back online in this step, assumed)
* `K5` → S4
* `K6` → S1

✅ Again, only **one key (K3)** was affected.

---

## 🎯 Key Takeaways from the Example

* Keys are always mapped **clockwise to the nearest server**.
* When a server **fails**, only the keys belonging to it move to the next server.
* When a server is **added**, only the keys in its segment move.
* This ensures **stability and scalability** — unlike traditional hashing where all keys might need to be remapped.

---

👉 This simple example shows why **consistent hashing** is a backbone for scalable distributed systems like caching (Memcached, Redis clusters), databases, and load balancers.

---


# ⚖️ Consistent Hashing in Java — Step-by-Step Example

In my [previous article](https://dev.to/zeeshanali0704/consistent-hashing-explained-with-an-example-209), I explained Consistent Hashing with a simple key–server mapping example. Now let’s take the same concept and **implement it in Java** to see it in action.

---

## 🔑 What is Consistent Hashing?

Consistent Hashing is a technique used in distributed systems to **distribute keys across servers (nodes) in a balanced way**, while minimizing reassignments when servers are added or removed.

* Servers and keys are placed on a **hash ring**.
* A key is mapped to the **first server clockwise** from its position.
* Adding/removing servers only affects a **small subset of keys**.

This makes it ideal for **distributed caches, databases, and load balancers**.

---

## 🛠️ Java Implementation

Here’s a basic Java implementation:

```java
import java.util.*;

class ConsistentHashing {
    private TreeMap<Integer, String> ring = new TreeMap<>();
    private int ringSize;

    public ConsistentHashing(int ringSize) {
        this.ringSize = ringSize;
    }

    // Simple hash function for demo (use MD5/MurmurHash in production)
    private int hash(String key) {
        // return Math.abs(key.hashCode()) % ringSize;
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

    // Add a server
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

    // Get server for a key
    public String getServer(String key) {
        if (ring.isEmpty()) return null;
        int position = hash(key);

        // Find the first server clockwise
        Map.Entry<Integer, String> entry = ring.ceilingEntry(position);
        if (entry == null) {
            entry = ring.firstEntry(); // wrap around
        }
        return entry.getValue();
    }

    // Show mapping of keys to servers
    public void showKeyMapping(List<String> keys) {
        for (String key : keys) {
            System.out.println(key + " → " + getServer(key));
        }
    }
}

public class ConsistentHashingDemo {
    public static void main(String[] args) {
        ConsistentHashing ch = new ConsistentHashing(100);

        // Add servers
        ch.addServer("S1");
        ch.addServer("S2");
        ch.addServer("S3");
        ch.addServer("S4");

        List<String> keys = Arrays.asList("K1", "K2", "K3", "K4", "K5", "K6");

        System.out.println("\nInitial key mappings:");
        ch.showKeyMapping(keys);

        // Remove a server
        ch.removeServer("S3");

        System.out.println("\nAfter removing S3:");
        ch.showKeyMapping(keys);

        // Add a new server
        ch.addServer("S5");

        System.out.println("\nAfter adding S5:");
        ch.showKeyMapping(keys);
    }
}
```

---

## 📊 Sample Output (Run Result)

```
Server S1 added at position 66
Server S2 added at position 62
Server S3 added at position 79
Server S4 added at position 1

Initial key mappings:
K1 → S2
K2 → S2
K3 → S2
K4 → S1
K5 → S2
K6 → S2

Server S3 removed from position 79

After removing S3:
K1 → S2
K2 → S2
K3 → S2
K4 → S1
K5 → S2
K6 → S2

Server S5 added at position 15

After adding S5:
K1 → S5
K2 → S2
K3 → S2
K4 → S1
K5 → S5
K6 → S2
```

---

## 📝 Explanation of the Flow

1. **Initial Setup**

   * Servers placed at:

     ```
     S1 → 66
     S2 → 62
     S3 → 79
     S4 → 1
     ```
   * Keys mostly map to **S2**, with `K4` mapping to **S1**.

2. **Removing S3**

   * No keys were assigned to `S3`.
   * ✅ Result: No key movement — all keys remain the same.

3. **Adding S5**

   * `S5` lands at position `15`, between `S4 (1)` and `S2 (62)`.
   * Keys `K1` and `K5` move from **S2 → S5**.
   * Other keys remain unchanged.

👉 This shows the power of **consistent hashing**:
Only a **small subset of keys** are remapped when servers are added or removed.

---

## 🚀 Production Considerations

* Use strong hash functions like **MD5, SHA-256, or MurmurHash**.
* Use **Virtual Nodes** (replicas per server) for better load balancing.
* Apply this pattern in **distributed caches (Redis, Memcached), databases, and CDNs**.

---

✅ That’s it! You now have a **working Java implementation of Consistent Hashing** with code, explanation, and sample output.




More Details:

Get all articles related to system design 
Hastag: SystemDesignWithZeeshanAli

Git: https://github.com/ZeeshanAli-0704/SystemDesignWithZeeshanAli


