package org.zigmoi.expncal.commons;

import org.zigmoi.expncal.exceptions.PreContitionFailException;

public class ConditionOp {
    
    public static void preConditionFail(boolean check, String msg) throws PreContitionFailException {
        if (check) {
            throw new PreContitionFailException(msg);
        }
    }
}

