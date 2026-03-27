package org.cpu;
 

import java.util.ArrayList;
import java.util.List;

public class CacheHierarchy {
 private final List<CacheLevel> levels;
 private final Memory memory;
 private boolean enabled;
 
 public CacheHierarchy(Memory memory) {
     this.levels = new ArrayList<>();
     this.memory = memory;
     this.enabled = false;
 }
 
 public void configure(List<CacheConfig> configs) {
     levels.clear();
     for (CacheConfig config : configs) {
         levels.add(new CacheLevel(config));
     }
     enabled = !configs.isEmpty();
 }
 
 public byte readByte(long address) {
     if (!enabled) {
         return memory.readByte(address);
     }
     
     byte[] data = new byte[1];
     accessMemory(address, false, data);
     return data[0];
 }
 
 public void writeByte(long address, byte value) {
     if (!enabled) {
         memory.writeByte(address, value);
         return;
     }
     
     byte[] data = {value};
     accessMemory(address, true, data);
 }
 
 public long readLong(long address) {
     if (!enabled) {
         return memory.readLong(address);
     }
     
     long result = 0;
     for (int i = 0; i < 8; i++) {
         byte b = readByte(address + i);
         result |= ((long) (b & 0xFF)) << (i * 8);
     }
     return result;
 }
 
 public void writeLong(long address, long value) {
     if (!enabled) {
         memory.writeLong(address, value);
         return;
     }
     
     for (int i = 0; i < 8; i++) {
         byte b = (byte) ((value >> (i * 8)) & 0xFF);
         writeByte(address + i, b);
     }
 }
 
 /*private void accessMemory(long address, boolean isWrite, byte[] data) {
      
     for (CacheLevel level : levels) {
         CacheAccessResult result = level.access(address, isWrite, data);
         if (result.hit) {
             return;
         }
     }
     
      
     if (isWrite) {
         memory.writeByte(address, data[0]);
     } else {
         data[0] = memory.readByte(address);
     }
 }*/
 
private void accessMemory(long address, boolean isWrite, byte[] data) {
   
  for (CacheLevel level : levels) {
      CacheAccessResult result = level.access(address, isWrite, data);
      if (result.hit) {
          return;
      }
  }
  
   
   
  if (isWrite) {
      memory.writeByte(address, data[0]);
  } else {
      data[0] = memory.readByte(address);
  }
}
 
 public void simulateOptimalComparison() {
     if (levels.isEmpty()) return;
     
      
     List<Long> fullTrace = levels.get(0).getAccessTrace();
     
     for (CacheLevel level : levels) {
         System.out.println("Original " + level);
         level.simulateOptimal(fullTrace);
         System.out.println("Optimal Simulation " + level);
     }
 }
 
 public List<CacheStatistics> getStatistics() {
     List<CacheStatistics> statistics = new ArrayList<>();
     for (CacheLevel level : levels) {
         statistics.add(level.getStatistics());
     }
     return statistics;
 }
 
 @Override
 public String toString() {
     if (!enabled) return "Cache disabled";
     
     StringBuilder sb = new StringBuilder();
     sb.append("Cache Hierarchy:\n");
     for (int i = 0; i < levels.size(); i++) {
         sb.append("L").append(i + 1).append(": ").append(levels.get(i)).append("\n");
     }
     return sb.toString();
 }
}