package org.zigmoi.expncal.core;

public class MExpenditureType {

    public int getPaidById() {
        return paidById;
    }

    public void setPaidById(int paidById) {
        this.paidById = paidById;
    }

    public int getPaidForId() {
        return paidForId;
    }

    public void setPaidForId(int paidForId) {
        this.paidForId = paidForId;
    }

    public Amount getAmount() {
        return amount;
    }

    public void setAmount(Amount amount) {
        this.amount = amount;
    }

    int paidById;
    int paidForId;
    Amount amount;

    void add(MCalculation mCalc) {
        
    }
}