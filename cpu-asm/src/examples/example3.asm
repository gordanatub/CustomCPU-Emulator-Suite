
MOV R0, 100
MOV R1, 25
ADD R0, R1       
SUB R0, 50       
MUL R0, 2        
MOV R2, 3
DIV R0, R2       

 
MOV R3, 50
CMP R0, R3
JE TEST1_PASS
HALT             

TEST1_PASS:
MOV [0x100], 1   
HALT