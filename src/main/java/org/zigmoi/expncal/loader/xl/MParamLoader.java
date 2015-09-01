package org.zigmoi.expncal.loader.xl;

import java.util.HashMap;

public class MParamLoader {

    boolean ignoreFirstLine;
    int individualEncodeSize = 1;
    String[] headerOrdered;
    HashMap<String, String> individualCodeMap;
    HashMap<String, String> specialCodeHandleMap;

    public boolean isIgnoreFirstLine() {
        return ignoreFirstLine;
    }

    public void setIgnoreFirstLine(boolean ignoreFirstLine) {
        this.ignoreFirstLine = ignoreFirstLine;
    }

    public int getIndividualEncodeSize() {
        return individualEncodeSize;
    }

    public void setIndividualEncodeSize(int individualEncodeSize) {
        this.individualEncodeSize = individualEncodeSize;
    }

    public String[] getHeaderOrdered() {
        return headerOrdered;
    }

    public void setHeaderOrdered(String[] headerOrdered) {
        this.headerOrdered = headerOrdered;
    }

    public HashMap<String, String> getIndividualCodeMap() {
        return individualCodeMap;
    }

    public void setIndividualCodeMap(HashMap<String, String> individualCodeMap) {
        this.individualCodeMap = individualCodeMap;
    }

    public HashMap<String, String> getSpecialCodeHandleMap() {
        return specialCodeHandleMap;
    }

    public void setSpecialCodeHandleMap(HashMap<String, String> specialCodeHandleMap) {
        this.specialCodeHandleMap = specialCodeHandleMap;
    }
    
    
}
