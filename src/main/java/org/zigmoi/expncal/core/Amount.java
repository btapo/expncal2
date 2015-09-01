package org.zigmoi.expncal.core;

public class Amount implements Comparable {

    double amount = 0;
    
    public Amount(double amount) {
        this.amount = amount;
    }
    
    public Amount(String amount) {
        this.amount = Double.parseDouble(amount);
    }

    @Override
    public int compareTo(Object o) {   
     
        Amount amount2 = (Amount) o;
        if (this.amount == amount2.amount) {
            return 0;
        } else if(this.amount < amount2.amount) {
            return -1;
        } 
        
        return 1;
    }
    
    public static Amount add(Amount amt1, Amount amt2) {
        
        if (amt1 == null) {
            amt1 = new Amount(0);
        }
        if (amt2 == null) {
            amt2 = new Amount(0);
        }
        return new Amount(amt1.amount + amt2.amount);
    }

    public static Amount subtract(Amount amt1, Amount amt2) {
        
        if (amt1 == null) {
            amt1 = new Amount(0);
        }
        if (amt2 == null) {
            amt2 = new Amount(0);
        }
        return new Amount(amt1.amount - amt2.amount);
    }
    
    public String toString() {
        return String.valueOf(amount);
    }
}