package org.cpu;

public class CacheStatistics {
	private int hits;
	private int misses;
	private int compulsoryMisses;
	private int capacityMisses;
	private int conflictMisses;

	public CacheStatistics() {
		reset();
	}

	public void reset() {
		hits = 0;
		misses = 0;
		compulsoryMisses = 0;
		capacityMisses = 0;
		conflictMisses = 0;
	}

	public void recordHit() {
		hits++;
	}

	public void recordMiss() {
		misses++;
	}

	public void recordCompulsoryMiss() {
		compulsoryMisses++;
	}

	public void recordCapacityMiss() {
		capacityMisses++;
	}

	public void recordConflictMiss() {
		conflictMisses++;
	}

	public int getTotalAccesses() {
		return hits + misses;
	}

	public double getHitRate() {
		return getTotalAccesses() == 0 ? 0 : (double) hits / getTotalAccesses();
	}

	public double getMissRate() {
		return getTotalAccesses() == 0 ? 0 : (double) misses / getTotalAccesses();
	}

	public int getHits() {
		return hits;
	}

	public int getMisses() {
		return misses;
	}

	public int getCompulsoryMisses() {
		return compulsoryMisses;
	}

	public int getCapacityMisses() {
		return capacityMisses;
	}

	public int getConflictMisses() {
		return conflictMisses;
	}

	@Override
	public String toString() {
		return String.format("Hits: %d, Misses: %d, Hit Rate: %.2f%%, Miss Rate: %.2f%%", hits, misses,
				getHitRate() * 100, getMissRate() * 100);
	}
}