 
 
MOV R0, 0x42
MOV [0x200], R0      
MOV R1, [0x200]      

 
MOV R2, 0x300
MOV [R2], 0x99       
MOV R3, [R2]         

 
MOV R0, 8
MOV [0x400 + R0], 0x77   
MOV R1, [0x400 + R0]     

 
CMP R1, 0x77
JE TEST3_PASS
HALT

TEST3_PASS:
MOV [0x102], 1   
HALT