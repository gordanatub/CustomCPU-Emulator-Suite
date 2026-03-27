package org.cpu;

import java.util.*;
import java.util.regex.*;

public class Assembler {
	private static final Pattern INSTRUCTION_PATTERN = Pattern.compile("^(\\w+)\\s*(.*)$", Pattern.CASE_INSENSITIVE);

	private static final Pattern OPERAND_SPLIT_PATTERN = Pattern.compile("\\s*,\\s*");

	private static final Pattern HEX_PATTERN = Pattern.compile("^0x([0-9a-f]+)$", Pattern.CASE_INSENSITIVE);
	private static final Pattern REGISTER_PATTERN = Pattern.compile("^R([0-3])$", Pattern.CASE_INSENSITIVE);

	private static final Pattern MEMORY_INDIRECT_PATTERN = Pattern.compile("^\\[\\s*R([0-3])\\s*\\]$",
			Pattern.CASE_INSENSITIVE);
	private static final Pattern MEMORY_DIRECT_PATTERN = Pattern.compile("^\\[\\s*(0x[0-9a-f]+|[0-9]+)\\s*\\]$",
			Pattern.CASE_INSENSITIVE);
	private static final Pattern MEMORY_EXPRESSION_PATTERN = Pattern
			.compile("^\\[\\s*((?:0x[0-9a-f]+|[0-9]+)\\s*\\+\\s*R([0-3]))\\s*\\]$", Pattern.CASE_INSENSITIVE);
	private static final Pattern LABEL_PATTERN = Pattern.compile("^[a-z_][a-z0-9_]*$", Pattern.CASE_INSENSITIVE);

	private Map<String, Long> labels;
	private List<Instruction> instructions;
	private long currentAddress;
	private boolean debug;

	public Assembler() {
		labels = new HashMap<>();
		instructions = new ArrayList<>();
		currentAddress = 0;
		debug = false;
	}

	public List<Instruction> assemble(List<String> sourceLines) {
		labels.clear();
		instructions.clear();
		currentAddress = 0;

		for (String line : sourceLines) {
			processLineFirstPass(line.trim());
		}

		currentAddress = 0;
		for (String line : sourceLines) {
			processLineSecondPass(line.trim());
		}

		return new ArrayList<>(instructions);
	}

	private void processLineFirstPass(String line) {
		if (line.isEmpty() || line.startsWith(";")) {
			return;
		}

		if (line.endsWith(":")) {
			String label = line.substring(0, line.length() - 1).trim();
			labels.put(label.toUpperCase(), currentAddress);
			if (debug)
				System.out.println("Found label: " + label + " -> 0x" + Long.toHexString(currentAddress));
			return;
		}

		currentAddress += 4;
	}

	private void processLineSecondPass(String line) {
		if (line.isEmpty() || line.startsWith(";") || line.endsWith(":")) {
			return;
		}

		Matcher matcher = INSTRUCTION_PATTERN.matcher(line);
		if (!matcher.find()) {
			throw new AssemblyException("Invalid instruction: " + line);
		}

		String mnemonic = matcher.group(1).toUpperCase();
		String operandsStr = matcher.group(2).trim();

		if (debug) {
			System.out.println("Processing: '" + line + "'");
			System.out.println("  Mnemonic: '" + mnemonic + "'");
			System.out.println("  Operands string: '" + operandsStr + "'");
		}

		try {
			Instruction.Opcode opcode = Instruction.Opcode.valueOf(mnemonic);
			Operand[] operands = parseOperands(operandsStr);

			if (debug) {
				System.out.println("  Parsed " + operands.length + " operands:");
				for (Operand op : operands) {
					System.out.println("    - " + op + " (type: " + op.type + ")");
				}
			}

			validateOperandCount(opcode, operands);

			Instruction instruction = new Instruction(opcode, operands, 4, line);
			instructions.add(instruction);
			currentAddress += 4;

		} catch (IllegalArgumentException e) {
			throw new AssemblyException("Unknown instruction: " + mnemonic);
		}
	}

	private Operand[] parseOperands(String operandsStr) {
		if (operandsStr == null || operandsStr.trim().isEmpty()) {
			return new Operand[0];
		}

		List<String> operandStrs = splitOperandsRespectingBrackets(operandsStr);
		List<Operand> operands = new ArrayList<>();

		for (String operandStr : operandStrs) {
			if (operandStr.trim().isEmpty())
				continue;

			Operand operand = parseSingleOperand(operandStr.trim());
			operands.add(operand);
		}

		return operands.toArray(new Operand[0]);
	}

	private List<String> splitOperandsRespectingBrackets(String operandsStr) {
		List<String> result = new ArrayList<>();
		StringBuilder current = new StringBuilder();
		int bracketDepth = 0;

		for (int i = 0; i < operandsStr.length(); i++) {
			char c = operandsStr.charAt(i);

			if (c == '[') {
				bracketDepth++;
			} else if (c == ']') {
				bracketDepth--;
			}

			if (c == ',' && bracketDepth == 0) {

				result.add(current.toString().trim());
				current.setLength(0);
			} else {
				current.append(c);
			}
		}

		if (current.length() > 0) {
			result.add(current.toString().trim());
		}

		if (debug) {
			System.out.println("  Split operands: " + result);
		}

		return result;
	}

	private Operand parseSingleOperand(String operandStr) {

		operandStr = operandStr.trim();

		if (debug)
			System.out.println("  Parsing operand: '" + operandStr + "'");

		Matcher regMatcher = REGISTER_PATTERN.matcher(operandStr);
		if (regMatcher.matches()) {
			int regIndex = Integer.parseInt(regMatcher.group(1));
			if (debug)
				System.out.println("    -> Register R" + regIndex);
			return Operand.register(regIndex);
		}

		if (operandStr.matches("^R[4-9].*$")) {
			throw new AssemblyException("Invalid register: " + operandStr + ". Only R0-R3 are available.");
		}

		Matcher memExprMatcher = MEMORY_EXPRESSION_PATTERN.matcher(operandStr);
		if (memExprMatcher.matches()) {
			String expression = memExprMatcher.group(1);
			int regIndex = Integer.parseInt(memExprMatcher.group(2));

			String basePart = expression.replaceAll("\\s*\\+\\s*R[0-3]", "").trim();
			long baseAddress = parseNumber(basePart);

			if (debug)
				System.out.println(
						"    -> Memory expression [0x" + Long.toHexString(baseAddress) + " + R" + regIndex + "]");
			return Operand.memoryExpression(baseAddress, regIndex);
		}

		Matcher memIndirectMatcher = MEMORY_INDIRECT_PATTERN.matcher(operandStr);
		if (memIndirectMatcher.matches()) {
			int regIndex = Integer.parseInt(memIndirectMatcher.group(1));
			if (debug)
				System.out.println("    -> Memory indirect [R" + regIndex + "]");
			return Operand.memoryIndirect(regIndex);
		}

		Matcher memDirectMatcher = MEMORY_DIRECT_PATTERN.matcher(operandStr);
		if (memDirectMatcher.matches()) {
			String addressStr = memDirectMatcher.group(1);
			long address = parseNumber(addressStr);
			if (debug)
				System.out.println("    -> Memory direct [0x" + Long.toHexString(address) + "]");
			return Operand.memoryDirect(address);
		}

		Matcher hexMatcher = HEX_PATTERN.matcher(operandStr);
		if (hexMatcher.matches()) {
			String hexValue = hexMatcher.group(1);

			long value;
			if (hexValue.length() <= 16) {
				value = Long.parseUnsignedLong(hexValue, 16);
			} else {

				value = Long.parseUnsignedLong(hexValue.substring(hexValue.length() - 16), 16);
			}
			if (debug)
				System.out.println("    -> Immediate 0x" + Long.toHexString(value) + " (decimal: " + value + ")");
			return Operand.immediate(value);
		}

		try {
			long value = Long.parseLong(operandStr);
			if (debug)
				System.out.println("    -> Immediate " + value + " (hex: 0x" + Long.toHexString(value) + ")");
			return Operand.immediate(value);
		} catch (NumberFormatException e) {

			if (LABEL_PATTERN.matcher(operandStr).matches()) {
				if (debug)
					System.out.println("    -> Label " + operandStr);
				return Operand.label(operandStr.toUpperCase());
			} else {
				throw new AssemblyException("Invalid operand: " + operandStr);
			}
		}
	}

	private long parseNumber(String numberStr) {
		if (numberStr.toLowerCase().startsWith("0x")) {
			return Long.parseUnsignedLong(numberStr.substring(2), 16);
		} else {
			return Long.parseLong(numberStr);
		}
	}

	private void validateOperandCount(Instruction.Opcode opcode, Operand[] operands) {
		int expected = getExpectedOperandCount(opcode);
		if (operands.length != expected) {
			throw new AssemblyException(String.format("Instruction %s expects %d operands, got %d. Operands: %s",
					opcode, expected, operands.length, Arrays.toString(operands)));
		}
	}

	private int getExpectedOperandCount(Instruction.Opcode opcode) {
		switch (opcode) {
		case ADD:
		case SUB:
		case MUL:
		case DIV:
		case AND:
		case OR:
		case XOR:
		case MOV:
		case CMP:
			return 2;
		case NOT:
			return 1;
		case JMP:
		case JE:
		case JNE:
		case JGE:
		case JL:
		case IN:
		case OUT:
			return 1;
		case HALT:
			return 0;
		default:
			return 0;
		}
	}

	public long resolveLabel(String label) {
		Long address = labels.get(label.toUpperCase());
		if (address == null) {
			throw new AssemblyException("Undefined label: " + label);
		}
		return address;
	}

	public Map<String, Long> getLabels() {
		return new HashMap<>(labels);
	}

	public void setDebug(boolean debug) {
		this.debug = debug;
	}
}

class AssemblyException extends RuntimeException {
	public AssemblyException() {
	}

	public AssemblyException(String s) {
		super(s);

	}

}