package org.cpu;

public class RegisterFile {
	private final long[] registers;
	public static final int NUM_REGISTERS = 4;

	public RegisterFile() {
		registers = new long[NUM_REGISTERS];
	}

	public void set(int regIndex, long value) {
		if (regIndex < 0 || regIndex >= NUM_REGISTERS) {
			throw new IllegalArgumentException("Invalid register index: " + regIndex);
		}
		registers[regIndex] = value;
	}

	public long get(int regIndex) {
		if (regIndex < 0 || regIndex >= NUM_REGISTERS) {
			throw new IllegalArgumentException("Invalid register index: " + regIndex);
		}
		return registers[regIndex];
	}

	public void reset() {
		for (int i = 0; i < NUM_REGISTERS; i++) {
			registers[i] = 0;
		}
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < NUM_REGISTERS; i++) {
			sb.append(String.format("R%d: 0x%016X (%d)\n", i, registers[i], registers[i]));
		}
		return sb.toString();
	}
}