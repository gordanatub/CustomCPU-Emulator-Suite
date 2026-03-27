package org.cpu;

import java.util.HashMap;
import java.util.Map;

public class Memory {
	private final Map<Long, Byte> storage;
	private static final byte DEFAULT_VALUE = 0;

	public Memory() {
		storage = new HashMap<>();
	}

	public byte readByte(long address) {
		return storage.getOrDefault(address, DEFAULT_VALUE);
	}

	public void writeByte(long address, byte value) {
		storage.put(address, value);
	}

	public long readLong(long address) {
		long result = 0;
		for (int i = 0; i < 8; i++) {
			long byteValue = readByte(address + i) & 0xFFL;
			result |= (byteValue << (i * 8));
		}
		return result;
	}

	public void writeLong(long address, long value) {
		for (int i = 0; i < 8; i++) {
			byte byteValue = (byte) ((value >> (i * 8)) & 0xFF);
			writeByte(address + i, byteValue);
		}
	}

	public void clear() {
		storage.clear();
	}

	public int getUsedMemorySize() {
		return storage.size();
	}

	@Override
	public String toString() {
		return String.format("Memory: %d bytes allocated", storage.size());
	}
}