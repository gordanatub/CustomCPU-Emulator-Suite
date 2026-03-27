 
 
MOV R0, 0        
MOV R1, 1        
MOV R2, 10       
MOV R3, 0        

LOOP:
 
MOV R3, R1       
ADD R1, R0       
MOV R0, R3       

 
MOV [0x2000 + R3], R1

 
ADD R3, 1
CMP R3, R2
JL LOOP          

 
MOV R0, 0        
MOV R3, 0        
SUM_LOOP:
MOV R1, [0x2000 + R3]
ADD R0, R1
ADD R3, 1
CMP R3, R2
JNE SUM_LOOP

HALT