package org.example;

import java.util.BitSet;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class BloomFilter {

    private BitSet bitset;
    private int bitSetSize;
    private int hashCount;

    public BloomFilter(int size, int hashCount) {
        this.bitSetSize = size;
        this.hashCount = hashCount;
        this.bitset = new BitSet(size);
    }

    private int getHash(String item, int seed) {
        int hash = 0;
        for (char c : (item + seed).toCharArray()) {
            hash = (hash << 5) - hash + c;
            hash |= 0; // 32-bit int
        }
        return Math.abs(hash) % bitSetSize;
    }

    public void add(String item) {
        for (int i = 0; i < hashCount; i++) {
            bitset.set(getHash(item, i));
        }
    }

    public boolean mightContain(String item) {
        for (int i = 0; i < hashCount; i++) {
            if (!bitset.get(getHash(item, i))) {
                return false;
            }
        }
        return true;
    }

    public static void main(String[] args) {
        BloomFilter bloom = new BloomFilter(100, 3);

        String[] words = {"developer", "engineer", "java", "python"};
        for (String word : words) bloom.add(word);

        String[] testWords = {"developer", "golang", "java", "ruby"};
        for (String word : testWords) {
            System.out.println("'" + word + "' is in Bloom Filter: " + bloom.mightContain(word));
        }
    }
}