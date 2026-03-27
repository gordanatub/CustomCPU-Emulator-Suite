
MOV R0, 10
MOV R1, 5
ADD R0, R1      
SUB R0, 3        
MUL R0, 2        
MOV R2, 4
DIV R0, R2       

 
MOV R1, 0xFF
MOV R2, 0x0F
AND R1, R2       
OR  R1, 0xF0     
XOR R1, 0xFF     
NOT R1           

 
MOV [0x1000], 42
MOV R3, [0x1000]  
MOV [R0], R3      

HALT