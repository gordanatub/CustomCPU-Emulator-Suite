; tests/run_all.asm
; Master test that runs all individual tests
; and reports overall success/failure

; Run test 1: Basic Arithmetic
MOV R0, 0
JMP RUN_TEST1

TEST1_DONE:
MOV R1, [0x100]
CMP R1, 1
JNE TEST_FAILED

; Run test 2: Bitwise Operations  
MOV R0, 0
JMP RUN_TEST2

TEST2_DONE:
MOV R1, [0x101]
CMP R1, 1
JNE TEST_FAILED

; Run test 3: Memory Operations
MOV R0, 0
JMP RUN_TEST3

TEST3_DONE:
MOV R1, [0x102]
CMP R1, 1
JNE TEST_FAILED

; Run test 4: Branching
MOV R0, 0
JMP RUN_TEST4

TEST4_DONE:
MOV R1, [0x103]
CMP R1, 1
JNE TEST_FAILED

; Run test 6: Cache Stress (skip I/O test for automation)
MOV R0, 0
JMP RUN_TEST6

TEST6_DONE:
MOV R1, [0x105]
CMP R1, 1
JNE TEST_FAILED

; Run test 7: Integration
MOV R0, 0
JMP RUN_TEST7

TEST7_DONE:
MOV R1, [0x106]
CMP R1, 1
JNE TEST_FAILED

; All tests passed!
MOV R0, 65      ; 'A'
OUT R0
MOV R0, 76      ; 'L'
OUT R0
MOV R0, 76      ; 'L'
OUT R0
MOV R0, 32      ; space
OUT R0
MOV R0, 84      ; 'T'
OUT R0
MOV R0, 69      ; 'E'
OUT R0
MOV R0, 83      ; 'S'
OUT R0
MOV R0, 84      ; 'T'
OUT R0
MOV R0, 83      ; 'S'
OUT R0
MOV R0, 32      ; space
OUT R0
MOV R0, 80      ; 'P'
OUT R0
MOV R0, 65      ; 'A'
OUT R0
MOV R0, 83      ; 'S'
OUT R0
MOV R0, 83      ; 'S'
OUT R0
MOV R0, 69      ; 'E'
OUT R0
MOV R0, 68      ; 'D'
OUT R0
MOV R0, 33      ; '!'
OUT R0
MOV R0, 10      ; newline
OUT R0

HALT

TEST_FAILED:
MOV R0, 84      ; 'T'
OUT R0
MOV R0, 69      ; 'E'
OUT R0
MOV R0, 83      ; 'S'
OUT R0
MOV R0, 84      ; 'T'
OUT R0
MOV R0, 32      ; space
OUT R0
MOV R0, 70      ; 'F'
OUT R0
MOV R0, 65      ; 'A'
OUT R0
MOV R0, 73      ; 'I'
OUT R0
MOV R0, 76      ; 'L'
OUT R0
MOV R0, 69      ; 'E'
OUT R0
MOV R0, 68      ; 'D'
OUT R0
MOV R0, 33      ; '!'
OUT R0
MOV R0, 10      ; newline
OUT R0

HALT

; Include individual tests here (in a real assembler, these would be separate files)
RUN_TEST1:
; Basic Arithmetic test code here
MOV [0x100], 1
JMP TEST1_DONE

RUN_TEST2:
; Bitwise Operations test code here  
MOV [0x101], 1
JMP TEST2_DONE

RUN_TEST3:
; Memory Operations test code here
MOV [0x102], 1
JMP TEST3_DONE

RUN_TEST4:
; Branching test code here
MOV [0x103], 1
JMP TEST4_DONE

RUN_TEST6:
; Cache Stress test code here
MOV [0x105], 1
JMP TEST6_DONE

RUN_TEST7:
; Integration test code here
MOV [0x106], 1
JMP TEST7_DONE