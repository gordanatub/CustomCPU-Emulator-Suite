 
package org.cpu;

import java.util.*;

class CacheLine {
    long tag;
    byte[] data;
    boolean valid;
    boolean dirty;
    long lastAccessTime;
    long futureAccessTime;  
    
    CacheLine(int lineSize) {
        this.data = new byte[lineSize];
        this.valid = false;
        this.dirty = false;
        this.lastAccessTime = -1;
        this.futureAccessTime = Long.MAX_VALUE;
    }
}

public class CacheLevel {
    private final CacheConfig config;
    private final CacheStatistics stats;
    private final List<List<CacheLine>> sets;
    private long accessTimeCounter;
    private List<Long> accessTrace;  
    
    public CacheLevel(CacheConfig config) {
        this.config = config;
        this.stats = new CacheStatistics();
        this.accessTimeCounter = 0;
        this.accessTrace = new ArrayList<>();
        
        int numSets = config.getNumSets();
        sets = new ArrayList<>(numSets);
        
        for (int i = 0; i < numSets; i++) {
            List<CacheLine> set = new ArrayList<>(config.associativity);
            for (int j = 0; j < config.associativity; j++) {
                set.add(new CacheLine(config.lineSize));
            }
            sets.add(set);
        }
    }
    
    public CacheAccessResult access(long address, boolean isWrite, byte[] data) {
        accessTimeCounter++;
        accessTrace.add(address);
        
        long lineAddress = address & ~(config.lineSize - 1L);
        int setIndex = (int) ((address / config.lineSize) % config.getNumSets());
        long tag = lineAddress / config.lineSize;
        
        List<CacheLine> set = sets.get(setIndex);
        
         
        for (CacheLine line : set) {
            if (line.valid && line.tag == tag) {
                line.lastAccessTime = accessTimeCounter;
                stats.recordHit();
                
                if (isWrite) {
                    System.arraycopy(data, 0, line.data, (int)(address - lineAddress), data.length);
                    line.dirty = true;
                }
                
                return new CacheAccessResult(true, false, config.level);
            }
        }
        
         
        stats.recordMiss();
        boolean evictionRequired = true;
        CacheLine victim = null;
        
         
        for (CacheLine line : set) {
            if (!line.valid) {
                victim = line;
                evictionRequired = false;
                break;
            }
        }
        
         
        if (victim == null) {
            victim = selectVictim(set, tag);
            stats.recordConflictMiss();  
        } else {
            stats.recordCompulsoryMiss();
        }
        
         
        victim.tag = tag;
        victim.valid = true;
        victim.lastAccessTime = accessTimeCounter;
        
        if (isWrite) {
            System.arraycopy(data, 0, victim.data, (int)(address - lineAddress), data.length);
            victim.dirty = true;
        } else {
             
             
            Arrays.fill(victim.data, (byte)0);
            victim.dirty = false;
        }
        
        return new CacheAccessResult(false, evictionRequired && victim.dirty, config.level);
    }
    
    private CacheLine selectVictim(List<CacheLine> set, long newTag) {
        if (config.replacementPolicy.equals("OPTIMAL")) {
            return selectOptimalVictim(set, newTag);
        } else {
            return selectLRUVictim(set);
        }
    }
    
    private CacheLine selectLRUVictim(List<CacheLine> set) {
        CacheLine lru = set.get(0);
        for (CacheLine line : set) {
            if (line.lastAccessTime < lru.lastAccessTime) {
                lru = line;
            }
        }
        return lru;
    }
    
    private CacheLine selectOptimalVictim(List<CacheLine> set, long newTag) {
         
        for (CacheLine line : set) {
            line.futureAccessTime = calculateNextAccessTime(line.tag, accessTimeCounter);
        }
        
         
        CacheLine optimal = set.get(0);
        for (CacheLine line : set) {
            if (line.futureAccessTime > optimal.futureAccessTime) {
                optimal = line;
            }
        }
        return optimal;
    }
    
    private long calculateNextAccessTime(long tag, long currentTime) {
        for (long i = currentTime; i < accessTrace.size(); i++) {
            long accessAddress = accessTrace.get((int)i);
            long accessTag = (accessAddress & ~(config.lineSize - 1L)) / config.lineSize;
            if (accessTag == tag) {
                return i;
            }
        }
        return Long.MAX_VALUE;
    }
    
    public void simulateOptimal(List<Long> fullTrace) {
         
        accessTrace = new ArrayList<>(fullTrace);
        accessTimeCounter = 0;
        stats.reset();
        
         
        for (List<CacheLine> set : sets) {
            for (CacheLine line : set) {
                line.valid = false;
            }
        }
        
         
        byte[] dummyData = new byte[1];
        for (long address : fullTrace) {
            access(address, false, dummyData);
        }
    }
    
    public CacheStatistics getStatistics() {
        return stats;
    }
    
    public List<Long> getAccessTrace() {
        return new ArrayList<>(accessTrace);
    }
    
    @Override
    public String toString() {
        return config.toString() + " - " + stats.toString();
    }
}

class CacheAccessResult {
    public final boolean hit;
    public final boolean writeBackRequired;
    public final int level;
    
    public CacheAccessResult(boolean hit, boolean writeBackRequired, int level) {
        this.hit = hit;
        this.writeBackRequired = writeBackRequired;
        this.level = level;
    }
}