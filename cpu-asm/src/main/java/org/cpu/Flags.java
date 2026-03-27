package org.cpu;

public class Flags {
 private boolean zero;
 private boolean sign;
 private boolean carry;
 private boolean overflow;
 
 public Flags() {
     reset();
 }
 
 public void reset() {
     zero = false;
     sign = false;
     carry = false;
     overflow = false;
 }
 
 public void setForArithmetic(long result, long op1, long op2, String operation) {
     zero = (result == 0);
     sign = (result < 0);
     
     switch (operation) {
         case "ADD":
             carry = (result < op1) && (op2 > 0); 
             overflow = ((op1 > 0 && op2 > 0 && result < 0) || 
                        (op1 < 0 && op2 < 0 && result >= 0));
             break;
         case "SUB":
             carry = (op1 < op2);
             overflow = ((op1 >= 0 && op2 < 0 && result < 0) || 
                        (op1 < 0 && op2 >= 0 && result >= 0));
             break;
         default:
             carry = false;
             overflow = false;
     }
 }
 
 public void setForComparison(long op1, long op2) {
     zero = (op1 == op2);
     sign = (op1 < op2);
     carry = false;
     overflow = false;
 }
 
 
 public boolean isZero() { return zero; }
 public boolean isSign() { return sign; }
 public boolean isCarry() { return carry; }
 public boolean isOverflow() { return overflow; }
 
 @Override
 public String toString() {
     return String.format("Z:%s S:%s C:%s O:%s", 
         zero ? "1" : "0", sign ? "1" : "0", carry ? "1" : "0", overflow ? "1" : "0");
 }
}