package org.zigmoi.expncal.core;

public class MPreCalculation {

    String entryId;
    String expenditureType;
    String amount;
    String paidBy;
    String paidFor;

    public String getEntryId() {
        return entryId;
    }

    public void setEntryId(String entryId) {
        this.entryId = entryId;
    }

    public String getExpenditureType() {
        return expenditureType;
    }

    public void setExpenditureType(String expenditureTypeId) {
        this.expenditureType = expenditureTypeId;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getPaidBy() {
        return paidBy;
    }

    public void setPaidBy(String paidById) {
        this.paidBy = paidById;
    }

    public String getPaidFor() {
        return paidFor;
    }

    public void setPaidFor(String paidForId) {
        this.paidFor = paidForId;
    }
}
