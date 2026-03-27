package org.cpu;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.*;

import org.junit.jupiter.api.*;

class ProcessorTest {
    private Processor processor;
    private Assembler assembler;
    
    @BeforeEach
    void setUp() {
        processor = new Processor();
        assembler = new Assembler();
    }
    
    @AfterEach
    void tearDown() {
        processor.close();
    }
    
    @Test
    void testArithmeticOperations() {
        List<String> program = Arrays.asList(
            "MOV R0, 10",
            "MOV R1, 5",  //R1=5
            "ADD R0, R1",
            "SUB R0, 3",
            "MUL R0, 2",
            "MOV R2, 4",  //R2=4
            "DIV R0, R2", //R0 = 24/4=6
            "HALT"
        );
        
        List<Instruction> instructions = assembler.assemble(program);
        processor.execute(instructions, assembler);
        
        assertEquals(6, processor.getRegisters().get(0));
        assertEquals(5, processor.getRegisters().get(1));
        assertEquals(4, processor.getRegisters().get(2));
    }
    
 
    @Test
    void testBitwiseOperations() {
        List<String> program = Arrays.asList(
            "MOV R0, 0xFF",
            "MOV R1, 0x0F", 
            "AND R0, R1",    // R0 = 0xFF and 0x0F = 0x0F
            "OR R0, 0xF0",   // R0 = 0x0F or 0xF0 = 0xFF  
            "XOR R0, 0xFF",  // R0 = 0xFF xor 0xFF = 0x00
            "NOT R0",        // R0 = not 0x00 = 0xFFFFFFFFFFFFFFFF
            "HALT"
        );
        
        List<Instruction> instructions = assembler.assemble(program);
        processor.execute(instructions, assembler);
        
        long result = processor.getRegisters().get(0);
        assertEquals(0xFFFFFFFFFFFFFFFFL, result);
    }

    @Test
    void testBitwiseOperationsAlternative() {
        // This sequence should produce 0xFFFFFFFFFFFFFF00L
        List<String> program = Arrays.asList(
            "MOV R0, 0x00",   // R0 = 0x00
            "NOT R0",          // R0 = 0xFFFFFFFFFFFFFFFF  
            "MOV R1, 0xFF",    // R1 = 0xFF
            "AND R0, R1",      // R0 = 0xFFFFFFFFFFFFFFFF and 0x00000000000000FF == 0x00000000000000FF
            "NOT R0",          // R0 = not 0x00000000000000FF == 0xFFFFFFFFFFFFFF00 ??
            "HALT"
        );
        
        List<Instruction> instructions = assembler.assemble(program);
        processor.execute(instructions, assembler);
        
        long result = processor.getRegisters().get(0);
        assertEquals(0xFFFFFFFFFFFFFF00L, result);
    }
    @Test 
    void testBitwiseStepByStep() {
        List<String> program = Arrays.asList(
            "MOV R0, 0x00",  
            "NOT R0",         
            "HALT"
        );
        
        List<Instruction> instructions = assembler.assemble(program);
        processor.execute(instructions, assembler);
        
        long result = processor.getRegisters().get(0);
        System.out.println("NOT 0x00 = 0x" + Long.toHexString(result));
        assertEquals(0xFFFFFFFFFFFFFFFFL, result);
    }

    @Test
    void testANDOperation() {
        List<String> program = Arrays.asList(
            "MOV R0, 0xFF",   // R0 = 0x00000000000000FF
            "MOV R1, 0x0F",   // R1 = 0x000000000000000F  
            "AND R0, R1",     // R0 = 0x000000000000000F
            "HALT"
        );
        
        List<Instruction> instructions = assembler.assemble(program);
        processor.execute(instructions, assembler);
        
        long result = processor.getRegisters().get(0);
        System.out.println("0xFF AND 0x0F = 0x" + Long.toHexString(result));
        assertEquals(0x0FL, result);
    }
    
    @Test
    void testMemoryOperations() {
        List<String> program = Arrays.asList(
            "MOV [0x1000], 42",
            "MOV R0, [0x1000]",
            "MOV R1, 0x1000",
            "MOV R2, [R1]",
            "HALT"
        );
        
        List<Instruction> instructions = assembler.assemble(program);
        processor.execute(instructions, assembler);
        
        assertEquals(42, processor.getRegisters().get(0));
        assertEquals(42, processor.getRegisters().get(2));
    }
    
    @Test
    void testBranching() {
        List<String> program = Arrays.asList(
            "MOV R0, 10",
            "MOV R1, 10",
            "CMP R0, R1",
            "JE SKIP",
            "MOV R2, 1",  
            "SKIP:",
            "MOV R2, 42",
            "HALT"
        );
        
        List<Instruction> instructions = assembler.assemble(program);
        processor.execute(instructions, assembler);
        
        assertEquals(42, processor.getRegisters().get(2));
        assertTrue(processor.getFlags().isZero());
    }
    
    @Test
    void testDivisionByZero() {
        List<String> program = Arrays.asList(
            "MOV R0, 10",
            "MOV R1, 0",
            "DIV R0, R1",
            "HALT"
        );
        
        List<Instruction> instructions = assembler.assemble(program);
        assertThrows(ProcessorException.class, () -> processor.execute(instructions, assembler));
    }
}
