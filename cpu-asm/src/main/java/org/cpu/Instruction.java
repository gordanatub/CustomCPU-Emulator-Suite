package org.cpu;

public class Instruction {
	public enum Opcode {

		ADD, SUB, MUL, DIV,

		AND, OR, XOR, NOT,

		MOV,

		CMP, JMP, JE, JNE, JGE, JL,

		IN, OUT,

		HALT
	}

	public final Opcode opcode;
	public final Operand[] operands;
	public final int size;
	public final String originalLine;

	public Instruction(Opcode opcode, Operand[] operands, int size, String originalLine) {
		this.opcode = opcode;
		this.operands = operands;
		this.size = size;
		this.originalLine = originalLine;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(opcode.toString());
		if (operands != null && operands.length > 0) {
			sb.append(" ");
			for (int i = 0; i < operands.length; i++) {
				if (i > 0)
					sb.append(", ");
				sb.append(operands[i]);
			}
		}
		return sb.toString();
	}
}