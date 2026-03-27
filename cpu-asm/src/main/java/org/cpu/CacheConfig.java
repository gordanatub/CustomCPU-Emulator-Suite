package org.cpu;
 

public class CacheConfig {
 public final int size;  
 public final int associativity;  
 public final int lineSize;  
 public final String replacementPolicy;  
 public final int level;  
 
 public CacheConfig(int size, int associativity, int lineSize, String replacementPolicy, int level) {
     this.size = size;
     this.associativity = associativity;
     this.lineSize = lineSize;
     this.replacementPolicy = replacementPolicy.toUpperCase();
     this.level = level;
     
     validate();
 }
 
 private void validate() {
     if (size <= 0) throw new IllegalArgumentException("Cache size must be positive");
     if (associativity <= 0) throw new IllegalArgumentException("Associativity must be positive");
     if (lineSize <= 0) throw new IllegalArgumentException("Line size must be positive");
     if (!replacementPolicy.equals("LRU") && !replacementPolicy.equals("OPTIMAL")) {
         throw new IllegalArgumentException("Replacement policy must be LRU or OPTIMAL");
     }
     if (level < 1 || level > 3) throw new IllegalArgumentException("Cache level must be 1-3");
 }
 
 public int getNumSets() {
     return size / (associativity * lineSize);
 }
 
 @Override
 public String toString() {
     return String.format("L%d Cache: %dB, %d-way, %dB line, %s", 
         level, size, associativity, lineSize, replacementPolicy);
 }
}