package org.cpu;

import java.util.List;
import java.util.Scanner;

public class Processor {
	private final RegisterFile registers;
	private final Flags flags;
	private final Memory memory;
	private final CacheHierarchy cache;
	private long pc;
	private boolean halted;
	private Scanner inputScanner;
	private boolean verbose;

	public Processor() {
		registers = new RegisterFile();
		flags = new Flags();
		memory = new Memory();
		cache = new CacheHierarchy(memory);
		inputScanner = new Scanner(System.in);
		reset();
	}

	public void reset() {
		registers.reset();
		flags.reset();
		memory.clear();
		pc = 0;
		halted = false;
	}

	public void loadProgram(List<Instruction> program) {
		reset();

	}

	public void execute(List<Instruction> program, Assembler assembler) {
		int instructionCount = 0;
		halted = false;

		while (!halted && pc < program.size() * 4) {
			int instructionIndex = (int) (pc / 4);
			if (instructionIndex >= program.size()) {
				break;
			}

			Instruction instruction = program.get(instructionIndex);
			executeInstruction(instruction, assembler);
			instructionCount++;

			if (verbose) {
				System.out.printf("PC: 0x%04X | %s | %s\n", pc, instruction, flags);
				System.out.println(registers);
			}

			if (instructionCount > 100000) {
				throw new ProcessorException("Execution limit exceeded - possible infinite loop");
			}
		}

		if (verbose) {
			System.out.println("Execution completed. Instructions executed: " + instructionCount);
		}
	}

	public void executeInstruction(Instruction instruction, Assembler assembler) {
		try {
			switch (instruction.opcode) {
			case ADD:
				executeADD(instruction.operands);
				break;
			case SUB:
				executeSUB(instruction.operands);
				break;
			case MUL:
				executeMUL(instruction.operands);
				break;
			case DIV:
				executeDIV(instruction.operands);
				break;
			case AND:
				executeAND(instruction.operands);
				break;
			case OR:
				executeOR(instruction.operands);
				break;
			case XOR:
				executeXOR(instruction.operands);
				break;
			case NOT:
				executeNOT(instruction.operands);
				break;
			case MOV:
				executeMOV(instruction.operands);
				break;
			case CMP:
				executeCMP(instruction.operands);
				break;
			case JMP:
				executeJMP(instruction.operands, assembler);
				break;
			case JE:
				executeJE(instruction.operands, assembler);
				break;
			case JNE:
				executeJNE(instruction.operands, assembler);
				break;
			case JGE:
				executeJGE(instruction.operands, assembler);
				break;
			case JL:
				executeJL(instruction.operands, assembler);
				break;
			case IN:
				executeIN(instruction.operands);
				break;
			case OUT:
				executeOUT(instruction.operands);
				break;
			case HALT:
				executeHALT();
				break;
			default:
				throw new ProcessorException("Unknown instruction: " + instruction.opcode);
			}

			if (!isJumpInstruction(instruction.opcode)) {
				pc += instruction.size;
			}

		} catch (Exception e) {
			throw new ProcessorException(
					String.format("Error executing instruction at PC 0x%04X: %s - %s", pc, instruction, e.getMessage()),
					e);
		}
	}

	private boolean isJumpInstruction(Instruction.Opcode opcode) {
		return opcode == Instruction.Opcode.JMP || opcode == Instruction.Opcode.JE || opcode == Instruction.Opcode.JNE
				|| opcode == Instruction.Opcode.JGE || opcode == Instruction.Opcode.JL;
	}

	private void executeADD(Operand[] operands) {
		long value1 = getRegisterValue(operands[0]);
		long value2 = getOperandValue(operands[1]);
		long result = value1 + value2;
		setRegisterValue(operands[0], result);
		flags.setForArithmetic(result, value1, value2, "ADD");
	}

	private void executeSUB(Operand[] operands) {
		long value1 = getRegisterValue(operands[0]);
		long value2 = getOperandValue(operands[1]);
		long result = value1 - value2;
		setRegisterValue(operands[0], result);
		flags.setForArithmetic(result, value1, value2, "SUB");
	}

	private void executeMUL(Operand[] operands) {
		long value1 = getRegisterValue(operands[0]);
		long value2 = getOperandValue(operands[1]);
		long result = value1 * value2;
		setRegisterValue(operands[0], result);
		flags.setForArithmetic(result, value1, value2, "MUL");
	}

	private void executeDIV(Operand[] operands) {
		long value1 = getRegisterValue(operands[0]);
		long value2 = getOperandValue(operands[1]);

		if (value2 == 0) {
			throw new ProcessorException("Division by zero");
		}

		long result = value1 / value2;
		setRegisterValue(operands[0], result);
		flags.setForArithmetic(result, value1, value2, "DIV");
	}

	private void executeAND(Operand[] operands) {
		long value1 = getRegisterValue(operands[0]);
		long value2 = getOperandValue(operands[1]);
		long result = value1 & value2;
		setRegisterValue(operands[0], result);
		flags.setForArithmetic(result, value1, value2, "AND");
	}

	private void executeOR(Operand[] operands) {
		long value1 = getRegisterValue(operands[0]);
		long value2 = getOperandValue(operands[1]);
		long result = value1 | value2;
		setRegisterValue(operands[0], result);
		flags.setForArithmetic(result, value1, value2, "OR");
	}

	private void executeXOR(Operand[] operands) {
		long value1 = getRegisterValue(operands[0]);
		long value2 = getOperandValue(operands[1]);
		long result = value1 ^ value2;
		setRegisterValue(operands[0], result);
		flags.setForArithmetic(result, value1, value2, "XOR");
	}

	private void executeNOT(Operand[] operands) {
		long value = getRegisterValue(operands[0]);
		long result = ~value;
		setRegisterValue(operands[0], result);
		flags.setForArithmetic(result, value, 0, "NOT");
	}

	private void executeMOV(Operand[] operands) {
		long value = getOperandValue(operands[1]);
		setOperandValue(operands[0], value);
	}

	private void executeCMP(Operand[] operands) {
		long value1 = getOperandValue(operands[0]);
		long value2 = getOperandValue(operands[1]);
		flags.setForComparison(value1, value2);
	}

	private void executeJMP(Operand[] operands, Assembler assembler) {
		pc = resolveOperandAddress(operands[0], assembler);
	}

	private void executeJE(Operand[] operands, Assembler assembler) {
		if (flags.isZero()) {
			pc = resolveOperandAddress(operands[0], assembler);
		} else {
			pc += 4;
		}
	}

	private void executeJNE(Operand[] operands, Assembler assembler) {
		if (!flags.isZero()) {
			pc = resolveOperandAddress(operands[0], assembler);
		} else {
			pc += 4;
		}
	}

	private void executeJGE(Operand[] operands, Assembler assembler) {
		if (!flags.isSign() || flags.isZero()) {
			pc = resolveOperandAddress(operands[0], assembler);
		} else {
			pc += 4;
		}
	}

	private void executeJL(Operand[] operands, Assembler assembler) {
		if (flags.isSign()) {
			pc = resolveOperandAddress(operands[0], assembler);
		} else {
			pc += 4;
		}
	}

	private void executeIN(Operand[] operands) {
		System.out.print("Input character: ");
		String input = inputScanner.nextLine();
		if (input.length() > 0) {
			char ch = input.charAt(0);
			setRegisterValue(operands[0], (byte) ch);
		} else {
			setRegisterValue(operands[0], 0);
		}
	}

	private void executeOUT(Operand[] operands) {
		long value = getOperandValue(operands[0]);
		char ch = (char) (value & 0xFF);
		System.out.print(ch);
	}

	private void executeHALT() {
		halted = true;
	}

	private long getOperandValue(Operand operand) {
		switch (operand.type) {
		case REGISTER:
			return registers.get((int) operand.value);
		case IMMEDIATE:
			return operand.value;
		case MEMORY_DIRECT:

			return cache.readByte(operand.value) & 0xFFL;
		case MEMORY_INDIRECT:
			long address1 = registers.get((int) operand.value);
			return cache.readByte(address1) & 0xFFL;
		case MEMORY_EXPRESSION:
			long base = operand.baseAddress;
			long offset = registers.get((int) operand.value);
			return cache.readByte(base + offset) & 0xFFL;
		default:
			throw new ProcessorException("Unsupported operand type for reading: " + operand.type);
		}
	}

	private void setOperandValue(Operand operand, long value) {
		switch (operand.type) {
		case REGISTER:
			registers.set((int) operand.value, value);
			break;
		case MEMORY_DIRECT:

			cache.writeByte(operand.value, (byte) (value & 0xFF));
			break;
		case MEMORY_INDIRECT:
			long address = registers.get((int) operand.value);

			cache.writeByte(address, (byte) (value & 0xFF));
			break;
		case MEMORY_EXPRESSION:
			long base = operand.baseAddress;
			long offset = registers.get((int) operand.value);
			cache.writeByte(base + offset, (byte) (value & 0xFF));
			break;
		default:
			throw new ProcessorException("Unsupported operand type for writing: " + operand.type);
		}
	}

	private long resolveOperandAddress(Operand operand, Assembler assembler) {
		switch (operand.type) {
		case IMMEDIATE:
			return operand.value;
		case MEMORY_DIRECT:
			return operand.value;
		case MEMORY_INDIRECT:
			return registers.get((int) operand.value);
		case MEMORY_EXPRESSION:
			return operand.baseAddress + registers.get((int) operand.value);
		case LABEL:
			return assembler.resolveLabel(operand.label);
		default:
			throw new ProcessorException("Unsupported operand type for address: " + operand.type);
		}
	}

	private long getRegisterValue(Operand operand) {
		if (operand.type != Operand.Type.REGISTER) {
			throw new ProcessorException("Expected register operand");
		}
		return registers.get((int) operand.value);
	}

	private void setRegisterValue(Operand operand, long value) {
		if (operand.type != Operand.Type.REGISTER) {
			throw new ProcessorException("Expected register operand");
		}
		registers.set((int) operand.value, value);
	}

	public RegisterFile getRegisters() {
		return registers;
	}

	public Flags getFlags() {
		return flags;
	}

	public Memory getMemory() {
		return memory;
	}

	public CacheHierarchy getCache() {
		return cache;
	}

	public long getPC() {
		return pc;
	}

	public boolean isHalted() {
		return halted;
	}

	public void setVerbose(boolean verbose) {
		this.verbose = verbose;
	}

	public void close() {
		if (inputScanner != null) {
			inputScanner.close();
		}
	}

}