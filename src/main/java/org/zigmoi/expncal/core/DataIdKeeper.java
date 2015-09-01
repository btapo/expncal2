package org.zigmoi.expncal.core;

import java.util.HashMap;
import java.util.Map;
import org.zigmoi.expncal.commons.HashMapOp;

public class DataIdKeeper {

    public static Map<Integer, String> expenditureIdMap = new HashMap<>();
    public static Map<Integer, String> individualIdMap = new HashMap<>();

    public static int expenditureIdSequence = 0;
    public static int individualIdSequence = 0;

    public static String getExpenditure(int id) {
        return expenditureIdMap.get(id);
    }

    public static int getExpenditureId(String expenditure) {

        if (HashMapOp.getKeyFromValue(expenditureIdMap, expenditure) == null) {
            expenditureIdMap.put(++expenditureIdSequence, expenditure);
            return expenditureIdSequence;
        }
        return (int) HashMapOp.getKeyFromValue(expenditureIdMap, expenditure);
    }

    public static String getIndividual(int id) {
        return individualIdMap.get(id);
    }

    public static int getIndividualId(String individual) {

        if (HashMapOp.getKeyFromValue(individualIdMap, individual) == null) {
            individualIdMap.put(++individualIdSequence, individual);
            return individualIdSequence;
        }
        return (int) HashMapOp.getKeyFromValue(individualIdMap, individual);
    }
}
