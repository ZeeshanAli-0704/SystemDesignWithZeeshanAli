# 🧮 What is a Counting Bloom Filter?

A **Counting Bloom Filter (CBF)** replaces the **bit array** in a standard Bloom Filter with an **array of small counters**.

* Standard Bloom Filter: Each position in the array is just a `0` or `1`.
* Counting Bloom Filter: Each position is a **small integer counter** (e.g., 4 bits, 8 bits, or 16 bits).

👉 This allows **incrementing when inserting** and **decrementing when deleting**, which means **deletions are supported** safely.

---

# ⚙️ How it Works

### 1. **Insertion**

* Compute `k` hash values for the item.
* For each hash value → increment the counter at that position.
* Example: inserting `"apple"` sets counters at positions `[4, 17, 29]` → incremented by `+1`.

---

### 2. **Lookup**

* Compute `k` hash values for the item.
* Check if **all counters > 0**.
* If yes → *item may exist*.
* If any counter is `0` → *item definitely doesn’t exist*.

---

### 3. **Deletion**

* Compute `k` hash values.
* For each → decrement the counter by `-1`.
* If a counter goes back to `0`, that slot is free again.
* Unlike standard Bloom Filter, this does **not break other items’ membership** (as long as you don’t decrement below 0).

---

# 🧾 Example Walkthrough

Say we have a **CBF with 10 counters** (all start at `0`) and **3 hash functions**:

### Insert `"cat"` → hashes to \[2, 4, 7]

* counters\[2]++, counters\[4]++, counters\[7]++
* Array: `[0,0,1,0,1,0,1,0,0,0]`

### Insert `"dog"` → hashes to \[4, 5, 9]

* counters\[4]++, counters\[5]++, counters\[9]++
* Array: `[0,0,1,0,2,1,1,0,0,1]`

### Delete `"cat"` → hashes \[2, 4, 7]

* counters\[2]--, counters\[4]--, counters\[7]--
* Array: `[0,0,0,0,1,1,0,0,0,1]`
* `"dog"` still exists safely!

---

# 📊 Pros & Cons

### ✅ Advantages

1. **Supports Deletion** — main benefit.
2. Same **lookup speed** as Bloom Filter (O(k)).
3. Can prevent memory bloat in dynamic datasets.

### ❌ Disadvantages

1. **Higher Memory Usage** — counters need more space than single bits.

   * Example: if counters are 4 bits each → 4x memory cost.
2. **Counter Overflow**

   * If too many items hash to the same position, counters may overflow.
   * Fix: use larger counters (but increases memory usage).
3. **Still Has False Positives**

   * Like Bloom Filters, cannot avoid false positives.
   * Deletions also increase false positive risk if counters drop unevenly.

---

# 🚀 Real-World Use Cases

* **Databases & Caches**:

  * e.g., Redis, Cassandra, and HBase use Counting Bloom Filters to track keys and allow safe removals.
* **Networking**:

  * Routers use them to track active flows that expire over time.
* **Security / Spam Detection**:

  * Track whether URLs, IPs, or messages are seen/unseen and remove them when outdated.

---

# 🔍 Comparison with Standard Bloom Filter

| Feature         | Standard Bloom Filter  | Counting Bloom Filter                |
| --------------- | ---------------------- | ------------------------------------ |
| Memory Usage    | Low (1 bit per slot)   | Higher (c-bit counters per slot)     |
| Deletion        | ❌ Not possible         | ✅ Supported                          |
| False Positives | ✅                      | ✅                                    |
| False Negatives | ❌ (unless buggy)       | ❌ (unless buggy)                     |
| Resizing        | ❌ Hard                 | ❌ Still hard                         |
| Use Case        | Mostly static datasets | Dynamic datasets (inserts + deletes) |

---

✅ **Summary**:
A **Counting Bloom Filter** is like a Bloom Filter on steroids — it trades **extra memory** for the ability to **delete elements**. It’s widely used in **dynamic systems (databases, caches, networks)** where entries need to expire or be removed.




More Details:

Get all articles related to system design 
Hastag: SystemDesignWithZeeshanAli

Git: https://github.com/ZeeshanAli-0704/SystemDesignWithZeeshanAli