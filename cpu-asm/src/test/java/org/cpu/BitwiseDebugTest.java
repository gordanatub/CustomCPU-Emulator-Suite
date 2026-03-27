package org.cpu;

import java.util.*;

public class BitwiseDebugTest {
 public static void main(String[] args) {
     Processor processor = new Processor();
     Assembler assembler = new Assembler();
     assembler.setDebug(true);
     
     
     String[][] testSteps = {
         {"MOV R0, 0xFF", "R0 should be 0xFF (255)"},
         {"MOV R1, 0x0F", "R1 should be 0x0F (15)"},
         {"AND R0, R1", "R0 should be 0xFF & 0x0F = 0x0F (15)"},
         {"OR R0, 0xF0", "R0 should be 0x0F | 0xF0 = 0xFF (255)"},
         {"XOR R0, 0xFF", "R0 should be 0xFF ^ 0xFF = 0x00 (0)"},
         {"NOT R0", "R0 should be ~0x00 = 0xFFFFFFFFFFFFFFFF (-1)"}
     };
     
     for (String[] step : testSteps) {
         System.out.println("\n=== " + step[1] + " ===");
         System.out.println("Executing: " + step[0]);
         
         try {
             List<Instruction> instructions = assembler.assemble(Arrays.asList(step[0]));
             processor.execute(instructions, assembler);
             
             System.out.println("Register State:");
             for (int i = 0; i < 4; i++) {
                 long value = processor.getRegisters().get(i);
                 System.out.printf("  R%d: 0x%016X (%d)\n", i, value, value);
             }
             
         } catch (Exception e) {
             System.out.println("ERROR: " + e.getMessage());
             e.printStackTrace();
         }
     }
     
     processor.close();
 }
}