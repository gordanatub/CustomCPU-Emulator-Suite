package org.cpu;

import java.io.*;
import java.util.*;

public class Emulator {
	/**
	args[0]  <putanja do asm fajla sa asemblerskim kodom> 
	args[1] <nivo_Kesa:velicina:asocijatvnost:velicina_linije:policy(LRU | OPTIMAL)> 
	args[2] <nivo_Kesa:velicina:asocijatvnost:velicina_linije:policy(LRU | OPTIMAL)>

	*/
	
	
	
	private final Processor processor;
	private final Assembler assembler;
	private boolean verbose;

	public Emulator() {
		processor = new Processor();
		assembler = new Assembler();
		verbose = false;
	}

	public void run(String filename, List<CacheConfig> cacheConfigs) {
		try {

			processor.getCache().configure(cacheConfigs);

			List<String> sourceLines = readSourceFile(filename);
			List<Instruction> program = assembler.assemble(sourceLines);

			System.out.println("Assembled program:");
			for (int i = 0; i < program.size(); i++) {
				System.out.printf("0x%04X: %s\n", i * 4, program.get(i));
			}
			System.out.println();

			Map<String, Long> labels = assembler.getLabels();
			if (!labels.isEmpty()) {
				System.out.println("Labels:");
				for (Map.Entry<String, Long> entry : labels.entrySet()) {
					System.out.printf("  %s: 0x%04X\n", entry.getKey(), entry.getValue());
				}
				System.out.println();
			}

			processor.setVerbose(false); //Prikaz
			processor.execute(program, assembler);

			printExecutionResults();

		} catch (Exception e) {
			System.err.println("Error: " + e.getMessage());
			e.printStackTrace();
		} finally {
			processor.close();
		}
	}

	private List<String> readSourceFile(String filename) throws IOException {
		List<String> lines = new ArrayList<>();
		try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
			String line;
			while ((line = reader.readLine()) != null) {

				int commentIndex = line.indexOf(';');
				if (commentIndex != -1) {
					line = line.substring(0, commentIndex);
				}
				lines.add(line.trim());
			}
		}
		return lines;
	}

	private void printExecutionResults() {
		System.out.println("\n=== Execution Results ===");
		System.out.println("Final register state:");
		System.out.println(processor.getRegisters());
		System.out.println("Final flags: " + processor.getFlags());
		System.out.println("Final PC: 0x" + Long.toHexString(processor.getPC()));
		System.out.println("Memory used: " + processor.getMemory().getUsedMemorySize() + " bytes");

		System.out.println("\n=== Cache Statistics ===");
		System.out.println(processor.getCache());

		if (!processor.getCache().getStatistics().isEmpty()) {
			System.out.println("\n=== Optimal Replacement Comparison ===");
			processor.getCache().simulateOptimalComparison();
		}
	}

	public void setVerbose(boolean verbose) {
		this.verbose = verbose;
	}

	public static void main(String[] args) {
		if (args.length < 1) {
			System.out.println("Cache config format: level:size:associativity:line_size:policy");
			System.out.println("Example: 1:1024:4:64:LRU 2:8192:8:64:LRU");
			return;
		}
		String filename = args[0];
		List<CacheConfig> cacheConfigs = new ArrayList<>();

		for (int i = 1; i < args.length; i++) {
			try {
				String[] parts = args[i].split(":");
				int level = Integer.parseInt(parts[0]);
				int size = Integer.parseInt(parts[1]);
				int associativity = Integer.parseInt(parts[2]);
				int lineSize = Integer.parseInt(parts[3]);
				String policy = parts[4];

				cacheConfigs.add(new CacheConfig(size, associativity, lineSize, policy, level));
			} catch (Exception e) {
				System.err.println("Invalid cache config: " + args[i]);
				return;
			}
		}

		Emulator emulator = new Emulator();
		emulator.setVerbose(true);
		emulator.run(filename, cacheConfigs);
	}
}