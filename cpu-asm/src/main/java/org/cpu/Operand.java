package org.cpu;

public class Operand {
	public enum Type {
		REGISTER, IMMEDIATE, MEMORY_DIRECT, MEMORY_INDIRECT, MEMORY_EXPRESSION, LABEL
	}

	public final Type type;
	public final long value;
	public final long baseAddress;
	public final String label;

	private Operand(Type type, long value, long baseAddress, String label) {
		this.type = type;
		this.value = value;
		this.baseAddress = baseAddress;
		this.label = label;
	}

	public static Operand register(int regIndex) {
		return new Operand(Type.REGISTER, regIndex, 0, null);
	}

	public static Operand immediate(long value) {
		return new Operand(Type.IMMEDIATE, value, 0, null);
	}

	public static Operand memoryDirect(long address) {
		return new Operand(Type.MEMORY_DIRECT, address, 0, null);
	}

	public static Operand memoryIndirect(int regIndex) {
		return new Operand(Type.MEMORY_INDIRECT, regIndex, 0, null);
	}

	public static Operand memoryExpression(long baseAddress, int regIndex) {
		return new Operand(Type.MEMORY_EXPRESSION, regIndex, baseAddress, null);
	}

	public static Operand label(String label) {
		return new Operand(Type.LABEL, 0, 0, label);
	}

	@Override
	public String toString() {
		switch (type) {
		case REGISTER:
			return "R" + value;
		case IMMEDIATE:
			return String.format("0x%X", value);
		case MEMORY_DIRECT:
			return String.format("[0x%X]", value);
		case MEMORY_INDIRECT:
			return "[R" + value + "]";
		case MEMORY_EXPRESSION:
			return String.format("[0x%X + R%d]", baseAddress, value);
		case LABEL:
			return label;
		default:
			return "UNKNOWN";
		}
	}
}