; tests/input_echo.asm
; Simple program that echoes user input
; Type characters and see them echoed back
; Press Enter (ASCII 13) to exit

MOV R0, 69      ; 'E'
OUT R0
MOV R0, 99      ; 'c'
OUT R0
MOV R0, 104     ; 'h'
OUT R0
MOV R0, 111     ; 'o'
OUT R0
MOV R0, 32      ; space
OUT R0
MOV R0, 80      ; 'P'
OUT R0
MOV R0, 114     ; 'r'
OUT R0
MOV R0, 111     ; 'o'
OUT R0
MOV R0, 103     ; 'g'
OUT R0
MOV R0, 114     ; 'r'
OUT R0
MOV R0, 97      ; 'a'
OUT R0
MOV R0, 109     ; 'm'
OUT R0
MOV R0, 58      ; ':'
OUT R0
MOV R0, 32      ; space
OUT R0
MOV R0, 10      ; newline
OUT R0

INPUT_LOOP:
IN R1           ; Read character from keyboard
MOV R0, R1
OUT R0          ; Echo character back

; Check for Enter key (ASCII 13) to exit
CMP R1, 13
JE EXIT_PROGRAM

; Check for 'q' to quit
CMP R1, 113     ; 'q'
JE EXIT_PROGRAM

JMP INPUT_LOOP

EXIT_PROGRAM:
MOV R0, 10      ; newline
OUT R0
MOV R0, 71      ; 'G'
OUT R0
MOV R0, 111     ; 'o'
OUT R0
MOV R0, 111     ; 'o'
OUT R0
MOV R0, 100     ; 'd'
OUT R0
MOV R0, 98      ; 'b'
OUT R0
MOV R0, 121     ; 'y'
OUT R0
MOV R0, 101     ; 'e'
OUT R0
MOV R0, 33      ; '!'
OUT R0
MOV R0, 10      ; newline
OUT R0
HALT