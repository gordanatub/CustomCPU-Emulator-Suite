# CustomCPU Emulator Suite

![Java](https://img.shields.io/badge/Java-17-orange?style=flat-square&logo=openjdk)
![Maven](https://img.shields.io/badge/Maven-3.x-blue?style=flat-square&logo=apachemaven)
![JUnit](https://img.shields.io/badge/JUnit-5-green?style=flat-square&logo=junit5)

**CustomCPU Emulator Suite** is a comprehensive Java-based emulator for a custom 64-bit processor architecture, featuring a fully simulated multi-level cache hierarchy. It includes a complete toolchain: an assembler, CPU core, memory manager, and advanced cache simulator supporting LRU and Optimal (Bélády's) replacement policies.

This project serves as an excellent **educational tool** for learning computer architecture, cache behavior, assembly programming, and performance analysis.

## ✨ Key Features

### 🏗️ CPU Architecture
- 64-bit custom Von Neumann processor
- 4 general-purpose registers (R0–R3)
- Full 64-bit byte-addressable memory space
- Rich instruction set: arithmetic, logical, data movement, control flow, I/O, and system instructions

### 💾 Cache Hierarchy
- Configurable multi-level cache (L1, L2, L3)
- Support for **LRU** and **Optimal (Bélády)** replacement algorithms
- Flexible parameters: size, associativity, line size
- Detailed cache statistics (hit/miss rates, compulsory/capacity/conflict misses)
- Side-by-side comparison between replacement policies

### 📜 Assembly Language Support
- Labels and symbolic addressing
- Multiple addressing modes: immediate, direct, indirect, base+offset
- Hex and decimal literals
- Inline comments (`;`)

### 🔧 Instruction Set
**Categories:**
- **Arithmetic**: `ADD`, `SUB`, `MUL`, `DIV`
- **Logical**: `AND`, `OR`, `XOR`, `NOT`
- **Data Transfer**: `MOV`
- **Control Flow**: `JMP`, `JE`, `JNE`, `JGE`, `JL`, `CMP`
- **I/O**: `IN`, `OUT`
- **System**: `HALT`

## 🚀 Quick Start

### Prerequisites
- **Java 17** or higher
- **Maven** 3.6+

### Building the Project
```bash
# Clone the repository
git clone <repository-url>
cd CustomCPU-Emulator-Suite

# Build the project
mvn clean compile
# 1. Basic execution (no cache)
mvn exec:java -Dexec.mainClass="org.cpu.Emulator" -Dexec.args="program.asm"

# 2. With single-level cache (L1)
mvn exec:java -Dexec.mainClass="org.cpu.Emulator" -Dexec.args="program.asm 1:1024:4:64:LRU"

# 3. With multi-level cache (L1 + L2)
mvn exec:java -Dexec.mainClass="org.cpu.Emulator" -Dexec.args="program.asm 1:1024:4:64:LRU 2:8192:8:64:OPTIMAL"
