package org.zigmoi.expncal.core;

import java.awt.font.LineBreakMeasurer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.omg.CORBA.INITIALIZE;
import org.zigmoi.expncal.commons.HashMapOp;

public class Calculate {

    ArrayList<MCalculation> mCalcList;

    Map<Integer, ArrayList<MExpenditureType>> expenditureTypeMap = new HashMap<>();
    Map<String, MLinkBalance> linkBalanceMap = new HashMap<>();
    Map<Integer, Amount> balanceMap = new HashMap<>();

    public Calculate(ArrayList<MCalculation> mCalcList) {
        this.mCalcList = mCalcList;
    }

    public void process() {

        for (MCalculation mCalc : mCalcList) {
            addExpenditureType(mCalc);
            calculateForLinkBalance(mCalc);
            calculateforBalance(mCalc);
        }
    }
    public Map<Integer, Amount> getBalanceMap() {
        return balanceMap;
    }

    private void addExpenditureType(MCalculation mCalc) {

        ArrayList<MExpenditureType> mExpenditureTypeList = new ArrayList<>();
        MExpenditureType mExpenditureType = new MExpenditureType();

        if (expenditureTypeMap.containsKey(mCalc.expenditureTypeId)) {
            mExpenditureTypeList = expenditureTypeMap.get(mCalc.expenditureTypeId);

            boolean match = false;
            for (MExpenditureType mExpenditureType2 : mExpenditureTypeList) {
                if (mExpenditureType2.getPaidById() == mCalc.paidById
                    && mExpenditureType2.getPaidForId() == mCalc.paidForId) {
                    mExpenditureType2.setAmount(Amount.add(mExpenditureType2.getAmount(),
                        mCalc.amount));
                    match = true;
                    break;
                } else if (mExpenditureType2.getPaidById() == mCalc.paidForId
                    && mExpenditureType2.getPaidForId() == mCalc.paidById) {
                    mExpenditureType2.setAmount(Amount.add(mExpenditureType2.getAmount(),
                        mCalc.amount));
                    match = true;
                    break;
                }
            }
            if (!match) {
                mExpenditureType.setAmount(mCalc.amount);
                mExpenditureType.setPaidById(mCalc.paidById);
                mExpenditureType.setPaidForId(mCalc.paidForId);

                mExpenditureTypeList.add(mExpenditureType);
            }
        } else {
            mExpenditureType.setAmount(mCalc.amount);
            mExpenditureType.setPaidById(mCalc.paidById);
            mExpenditureType.setPaidForId(mCalc.paidForId);

            mExpenditureTypeList.add(mExpenditureType);

            expenditureTypeMap.put(mCalc.expenditureTypeId, mExpenditureTypeList);
        }
    }

    private void calculateForLinkBalance(MCalculation mCalc) {
        
        LinkedIdMapHelper linkedIdMapHelper = new LinkedIdMapHelper(mCalc.paidById, mCalc.paidForId);
        
        MLinkBalance mLinkBalance;
        if (linkBalanceMap.containsKey(linkedIdMapHelper.linkedKey)) {
            mLinkBalance = linkBalanceMap.get(linkedIdMapHelper.linkedKey);
        } else {
            
            mLinkBalance = new MLinkBalance();
            mLinkBalance.setPaidById(mCalc.paidById);
            mLinkBalance.setPaidForId(mCalc.paidForId);
            mLinkBalance.setAmount(new Amount(0));

            linkBalanceMap.put(linkedIdMapHelper.linkedKey, mLinkBalance);
        }

        if (linkedIdMapHelper.swapped) {
            mLinkBalance.setAmount(Amount.subtract(mLinkBalance.getAmount(), mCalc.getAmount()));
        } else {
            mLinkBalance.setAmount(Amount.add(mLinkBalance.getAmount(), mCalc.getAmount()));
        }
    }

    public void printSummarisedTransferReport() {
        
        Map<Integer, Amount> valueSortedMap = HashMapOp.sortByValues(balanceMap);
        
        List<Integer> sortedKeyList = new ArrayList<>();
        sortedKeyList.addAll(valueSortedMap.keySet());
        
        int leftPointer = 0;
        int rightPointer = valueSortedMap.size() - 1;
        
        Amount leftAmount;
        Amount rightAmount;
        Amount amountDiff;
        
        int indIdOnLeft; 
        int indIdOnRight; 
        
        leftAmount = valueSortedMap.get(sortedKeyList.get(leftPointer));
        rightAmount = valueSortedMap.get(sortedKeyList.get(rightPointer));
        
        while (leftPointer < rightPointer) {
        
            indIdOnLeft = sortedKeyList.get(leftPointer);
            indIdOnRight = sortedKeyList.get(rightPointer);
        
            amountDiff = Amount.add(rightAmount, leftAmount);
      
            if (amountDiff.amount == 0) {
            
                System.out.println(DataIdKeeper.getIndividual(indIdOnLeft) + " to " + 
                        DataIdKeeper.getIndividual(indIdOnRight) + " : " + leftAmount);
                
                leftPointer++;
                rightPointer--;
                
                leftAmount = valueSortedMap.get(sortedKeyList.get(leftPointer));
                rightAmount = valueSortedMap.get(sortedKeyList.get(rightPointer));
        
            } else if (amountDiff.amount >= 0) {
                
                System.out.println(DataIdKeeper.getIndividual(indIdOnLeft) + " to " + 
                   DataIdKeeper.getIndividual(indIdOnRight) + " : " + leftAmount);
                
                rightAmount = amountDiff;
                
                leftPointer++;
                leftAmount = valueSortedMap.get(sortedKeyList.get(leftPointer));
                
            } else {
                
                System.out.println(DataIdKeeper.getIndividual(indIdOnLeft) + " to " + 
                        DataIdKeeper.getIndividual(indIdOnRight) + " : " + rightAmount);
                
                leftAmount = amountDiff;
                
                rightPointer--;
                rightAmount = valueSortedMap.get(sortedKeyList.get(rightPointer));
            }
        }
    }

    public void printExpenditureItemWiseSummaryReport() {
        
        for (int expType : expenditureTypeMap.keySet()) {
            
            Amount amountSum = new Amount(0);
            for (MExpenditureType mExpTypeList : expenditureTypeMap.get(expType)) {
                amountSum = Amount.add(amountSum, mExpTypeList.getAmount());
            }
            
            System.out.println("Total Expenditure against :" + DataIdKeeper.getExpenditure(expType) 
                    + " : " + amountSum);
        }
    }
    
    private class LinkedIdMapHelper {
        
        String linkedKey;

        boolean swapped;
        
        LinkedIdMapHelper(int paidBy, int paidFor) {

            String[] strArr = {String.valueOf(paidBy),String.valueOf(paidFor)};
            Arrays.sort(strArr);
            swapped = !strArr[0].equals(String.valueOf(paidBy));
            
            linkedKey = strArr[0] + "-" + strArr[1];
        }
    }

    private void calculateforBalance(MCalculation mCalc) {

        if (balanceMap.containsKey(mCalc.paidById)) {
            Amount amt = balanceMap.get(mCalc.paidById);
            balanceMap.put(mCalc.paidById, Amount.add(amt, mCalc.getAmount()));
        } else {
            balanceMap.put(mCalc.paidById, mCalc.getAmount());
        }

        if (balanceMap.containsKey(mCalc.paidForId)) {
            Amount amt = balanceMap.get(mCalc.paidForId);
            balanceMap.put(mCalc.paidForId, Amount.subtract(amt, mCalc.getAmount()));
        } else {
            balanceMap.put(mCalc.paidForId, Amount.subtract(new Amount(0), mCalc.getAmount()));
        }

//        System.out.println(mCalc.entryId + " : "
//            + mCalc.paidById + " : "
//            + mCalc.paidForId + " : "
//            + mCalc.amount.toString()
//        );
//
//        System.out.println(
//            "1 : " + balanceMap.get(1) + " : "
//            + "2 : " + balanceMap.get(2) + " : "
//            + "3 : " + balanceMap.get(3) + " : "
//            + "4 : " + balanceMap.get(4) + " : "
//        );
    }

    public void printExpenditureReport() {
        
        for (int expenditureId : this.expenditureTypeMap.keySet()) {
            
            for (MExpenditureType mExpenditureType : this.expenditureTypeMap.get(expenditureId)) {
                
//                System.out.print("expenditure.expenditureId : " + expenditureId + " - ");
//                System.out.print("expenditure.amount : " + mExpenditureType.amount.toString() + " - ");
//                System.out.print("expenditure.paidBy : " + mExpenditureType.paidById + " - ");
//                System.out.println("expenditure.paidFor : " + mExpenditureType.paidForId);
                
                System.out.print("expenditure : " + DataIdKeeper.getExpenditure(expenditureId) + " | ");
                System.out.print("amount : " + mExpenditureType.amount.toString() + " | ");
                System.out.print("paidBy : " + DataIdKeeper.getIndividual(mExpenditureType.paidById) + " | ");
                System.out.println("paidFor : " + DataIdKeeper.getIndividual(mExpenditureType.paidForId));
            }
        }
    }

    public void printBalanceReport() {
        
        for (int individualId : balanceMap.keySet()) {
//            System.out.print("balance.individualId : " + individualId + " - ");
//            System.out.println("balance.amount : " + (Amount) balanceMap.get(individualId));
            
            System.out.println(DataIdKeeper.getIndividual(individualId) + "'s balance : "
                    + (Amount) balanceMap.get(individualId));
        }
    }

    public void printIndividualLinkReport() {

        for (String linkedKey : linkBalanceMap.keySet()) {
            
            MLinkBalance mLinkBal = linkBalanceMap.get(linkedKey);
            
            if (mLinkBal.paidById == mLinkBal.paidForId) {continue;}
            
            if (mLinkBal.amount.amount >= 0) {
                System.out.println(DataIdKeeper.getIndividual(mLinkBal.paidForId) + " needs to pay amount : "
                        + mLinkBal.amount + " to " + DataIdKeeper.getIndividual(mLinkBal.paidById));
            } else {
                System.out.println(DataIdKeeper.getIndividual(mLinkBal.paidById) + " needs to pay amount : "
                        + mLinkBal.amount.amount * (-1) + " to " + DataIdKeeper.getIndividual(mLinkBal.paidForId));
            }
        }
    }

    public static void main(String... args) {
        ArrayList<MCalculation> mCalcList = new ArrayList<>();

        MCalculation mCalc = new MCalculation();
        mCalc.entryId = 1;
        mCalc.expenditureTypeId = 1;
        mCalc.amount = new Amount(100);
        mCalc.paidById = 1;
        mCalc.paidForId = 2;
        mCalcList.add(mCalc);

        mCalc = new MCalculation();
        mCalc.entryId = 2;
        mCalc.expenditureTypeId = 2;
        mCalc.amount = new Amount(200);
        mCalc.paidById = 1;
        mCalc.paidForId = 2;
        mCalcList.add(mCalc);

        Calculate calc = new Calculate(mCalcList);
        calc.process();
        calc.printExpenditureReport();
        calc.printBalanceReport();
        calc.printIndividualLinkReport();
    }
}
