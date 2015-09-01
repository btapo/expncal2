package org.zigmoi.expncal.core;

public class MCalculation {

    public int getEntryId() {
        return entryId;
    }

    public void setEntryId(int entryId) {
        this.entryId = entryId;
    }

    public int getExpenditureTypeId() {
        return expenditureTypeId;
    }

    public void setExpenditureTypeId(int expenditureTypeId) {
        this.expenditureTypeId = expenditureTypeId;
    }

    public Amount getAmount() {
        return amount;
    }

    public void setAmount(Amount amount) {
        this.amount = amount;
    }

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

    int entryId;
    int expenditureTypeId;
    Amount amount;
    int paidById;
    int paidForId;
}
